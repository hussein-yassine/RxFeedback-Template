package com.codefather.vanapp.Utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.SearchView
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.codefather.vanapp.R
import com.codefather.vanapp.VanApplication
import com.codefather.vanapp.VanApplication.Companion.context

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

object ViewUtilities {

    fun loadHtmlText(text:String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    fun setDCTextAppearance(context: Context, labelTxt: TextView, labelTextAppearance: Int,
                            labelTextSizeDefault: Int, labelTextColorDefault: Int, labelFontFamilyDefault: Int, labelBoldDefault: Boolean, labelTextPaddingDefault: Int) {
        val textAppearance = context.obtainStyledAttributes(labelTextAppearance, R.styleable.DCTextAppearance)

        try {
            val labelTextSize = textAppearance.getDimensionPixelSize(R.styleable.DCTextAppearance_textSize, context.resources.getDimensionPixelSize(labelTextSizeDefault))
            val labelTextColor = textAppearance.getColor(R.styleable.DCTextAppearance_textColor, ResourcesCompat.getColor(context.resources, labelTextColorDefault, null))
            val labelFontFamily = textAppearance.getResourceId(R.styleable.DCTextAppearance_textFontFamily, labelFontFamilyDefault)
            val labelBold = textAppearance.getBoolean(R.styleable.DCTextAppearance_textBold, labelBoldDefault)
            val labelTextPadding = textAppearance.getDimensionPixelSize(R.styleable.DCTextAppearance_textPadding,
                if (labelTextPaddingDefault != 0) context.resources.getDimensionPixelSize(labelTextPaddingDefault) else 0)

            labelTxt.setTextColor(labelTextColor)
            labelTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize.toFloat())
            val font = ResourcesCompat.getFont(context, labelFontFamily)
            val typeface: Int = if (labelBold) Typeface.BOLD else Typeface.NORMAL
            labelTxt.setTypeface(font, typeface)
            labelTxt.setPadding(labelTextPadding, labelTextPadding, labelTextPadding, labelTextPadding)
        } finally {
            textAppearance.recycle()
        }
    }

    fun setSearchViewFont(context: Context, searchView: SearchView, font: Int) {
        //TODO android bug workaround, remove when fixed
        val searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as TextView
        val typeface = ResourcesCompat.getFont(context, font)
        searchText.typeface = typeface
    }

    fun showKeyboard() {

        VanApplication.context?.let {

            val inputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun hideKeyboard(){

        VanApplication.context?.let {
            val imm =  it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    fun hideKeyboardFrom(activity: Activity) {
        val view = activity.currentFocus
        view?.let { v ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }
}