package com.codefather.vanapp.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Environment
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.codefather.vanapp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by Joseph Jreij on 11/29/2017.
 */
object ImageUtilities {

    private var colorGenerator: ColorGenerator? = null

    enum class ImageSize(@DimenRes val dimenIdRes: Int? = null) {
        THUMBNAIL(R.dimen.image_width_thumbnail),
        SMALL(R.dimen.image_width_small),
        LARGE(R.dimen.image_width_large),
        FULL,
    }

    private fun getColorGenerator(context: Context): ColorGenerator {
        if (colorGenerator == null) {
            val placeholderColors = mutableListOf<Int>()
            val ta = context.resources.obtainTypedArray(R.array.placeholderColors)
            (0 until ta.length()).mapTo(placeholderColors) { ta.getColor(it, -1) }
            ta.recycle()
            colorGenerator = ColorGenerator.create(placeholderColors)
        }

        return colorGenerator!!
    }

    fun loadRoundedImageWithText(
        context: Context,
        imageView: ImageView,
        text: String,
        imageSize: ImageSize
    ) {
        val widthPx = getImageSizePixels(context, imageSize)
        val drawable = getPlaceholderDrawable(context, text, widthPx)
        imageView.setImageDrawable(drawable)
    }

    private fun getPlaceholderDrawable(context: Context, placeholderText: String, widthPx: Int): TextDrawable? {
        return TextDrawable.builder()
            .beginConfig()
            .width(100)
            .height(100)
            .textColor(Color.WHITE)
            .useFont(ResourcesCompat.getFont(context, R.font.nunito))
            .bold()
            .endConfig()
            .buildRoundRect(placeholderText, getColorGenerator(context).getColor(placeholderText), widthPx)

    }


    private fun getImageSizePixels(context: Context, imageSize: ImageSize): Int =
        if (imageSize.dimenIdRes == null) 0 else context.resources.getDimensionPixelSize(imageSize.dimenIdRes)

    private val targets = ArrayList<com.squareup.picasso.Target>()
    fun downloadImage(
        context: Context,
        imageUrl: String,
        imageName: String,
        destinationPath: String,
        callback: ((success: Boolean) -> Unit)
    ) {

        Log.wtf("here ", "is downloadImage")
        val target = picassoImageTarget(destinationPath, imageName+".jpeg") {
            Log.wtf("in picaso download", "is " + it)
            callback.invoke(it)
            return@picassoImageTarget
        }
        targets.add(target)
        Picasso.with(context).load(imageUrl).into(target)
    }

    private fun picassoImageTarget(
        imageDir: String,
        imageName: String,
        callback: ((success: Boolean) -> Unit)
    ): com.squareup.picasso.Target {

        val directory = File(Environment.getExternalStorageDirectory(), imageDir)
        if (!directory.exists()) {
            directory.mkdir()
        }

        return object : com.squareup.picasso.Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                callback.invoke(false)
                targets.clear()
                Log.wtf("here ", "is onBitmapFailed: " + errorDrawable.toString())

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                Thread(Runnable {
                    val myImageFile = File(directory, imageName) // Create image file
                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(myImageFile)
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            fos!!.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    Log.wtf("image", "image saved to >>>" + myImageFile.absolutePath)
                    Log.wtf("here ", "is onBitmapLoaded")
                    targets.clear()
                    callback.invoke(true)
                }).start()

            }
        }
    }

}