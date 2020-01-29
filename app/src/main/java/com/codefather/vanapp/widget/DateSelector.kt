package com.codefather.vanapp.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.codefather.vanapp.R
import com.codefather.vanapp.Utils.DateUtilities
import com.codefather.vanapp.Utils.ViewUtilities
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import org.notests.sharedsequence.Signal
import org.notests.sharedsequence.asSignal
import org.notests.sharedsequence.empty
import org.notests.sharedsequence.map
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */


/**
 * Created by Joseph Jreij on 11/21/2017.
 */

class DateSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    lateinit var initalDate: Date
    var date: Date = DateUtilities.today()
    private var minDateLimit: Date? = null
    private var maxDateLimit: Date? = null
    private var callback: ((Date) -> Unit)? = null

    // customizations
    var dateFormat: String = ""
    var step = Calendar.DATE
    var preferedUppercasingText = false


    private val previousBtnLayout: FrameLayout
    private val previousBtn: ImageView
    private val nextBtnLayout: FrameLayout
    private val nextBtn: ImageView
    private val labelTxt: TextView

    private val nextButtonDrawable: Int
    private val previousButtonDrawable: Int
    private val buttonWidth: Int
    private val buttonHeight: Int
    private val buttonPaddingLeft: Int
    private val buttonPaddingTop: Int

    private val labelTextAppearance: Int

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.DCSelector, 0, 0)

        try {
            previousButtonDrawable =
                attributes.getResourceId(R.styleable.DCSelector_srcPreviousButton, R.drawable.arrow_left_gray)

            nextButtonDrawable =
                attributes.getResourceId(R.styleable.DCSelector_srcNextButton, R.drawable.arrow_right_gray)

            buttonWidth = attributes.getDimensionPixelSize(
                R.styleable.DCSelector_buttonWidth,
                context.resources.getDimensionPixelSize(R.dimen.card_arrow_icon_width_height)
            )
            buttonHeight = attributes.getDimensionPixelSize(
                R.styleable.DCSelector_buttonHeight,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            buttonPaddingLeft = context.resources.getDimensionPixelSize(R.dimen.spacing_normal)
            buttonPaddingTop = context.resources.getDimensionPixelSize(R.dimen.spacing_tiny)

            labelTextAppearance = attributes.getResourceId(R.styleable.DCSelector_textAppearance, 0)
        } finally {
            attributes.recycle()
        }
        nextBtnLayout = FrameLayout(context)
        nextBtn = ImageView(context)
        previousBtnLayout = FrameLayout(context)
        previousBtn = ImageView(context)
        labelTxt = TextView(context)

        loadPreviousButton()
        loadLabelText()
        loadNextButton()
    }

    private fun loadPreviousButton() {
        loadButton(previousBtn, previousBtnLayout, previousButtonDrawable)
        previousBtnLayout.setOnClickListener { onPreviousButtonClick() }

        val parentParams = previousBtnLayout.layoutParams as RelativeLayout.LayoutParams
        parentParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
    }

    private fun loadNextButton() {
        loadButton(nextBtn, nextBtnLayout, nextButtonDrawable)
        nextBtnLayout.setOnClickListener { onNextButtonClick() }

        val parentParams = nextBtnLayout.layoutParams as RelativeLayout.LayoutParams
        parentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
    }

    private fun loadButton(btn: ImageView, layout: FrameLayout, buttonDrawable: Int) {
        btn.setImageResource(buttonDrawable)
        btn.adjustViewBounds = true
        btn.cropToPadding = true
        this.addView(layout)
        layout.addView(btn)

        val params = btn.layoutParams as FrameLayout.LayoutParams
        params.width = buttonWidth
        params.height = buttonHeight

        layout.setPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingLeft, buttonPaddingTop)
    }

    private fun loadLabelText() {
        ViewUtilities.setDCTextAppearance(
            context, labelTxt, labelTextAppearance,
            R.dimen.font_normal, R.color.textColorDark, R.font.nunito, true, R.dimen.spacing_small
        )

        labelTxt.setOnClickListener { onLabelClick() }
        labelTxt.gravity = Gravity.CENTER
        this.addView(labelTxt)
        val params = labelTxt.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

    }

    protected fun setLabelText(text: String?) {
        labelTxt.text = text
    }

    protected fun modifyNextButtonState(isEnabled: Boolean) {
        nextBtnLayout.isEnabled = isEnabled
        nextBtn.isEnabled = isEnabled
    }

    protected fun modifyPreviousButtonState(isEnabled: Boolean) {
        previousBtnLayout.isEnabled = isEnabled
        previousBtn.isEnabled = isEnabled
    }

    fun setDate(date: Date?, minDateLimit: Date? = null, maxDateLimit: Date? = null, callback: ((Date) -> Unit)? = null) {
        date?.let {
            initalDate = date
            this.date = date
        }
        this.minDateLimit = minDateLimit
        this.maxDateLimit = maxDateLimit
        // prevent overriding old callback
        callback?.let {
            this.callback = it
        }
        //refresh()
    }


    fun refresh(date:Date) {
        // this is done to support current implementation and prevent
        // many changes in the current app.
        var text = ""
        if (dateFormat == "") {
            text = DateUtilities.geSimpleDateFormatForDisplay(context, date)
        } else {
            text = DateUtilities.getDateWithFormat(date, dateFormat)
        }
        if (preferedUppercasingText) {
            text = text.toUpperCase()
        }
        setLabelText(text)

        if (minDateLimit != null && date <= minDateLimit) {
            modifyPreviousButtonState(false)
        } else {
            modifyPreviousButtonState(true)
        }
        /*if (maxDateLimit != null && date >= maxDateLimit) {
            modifyNextButtonState(false)
        } else {
            modifyNextButtonState(true)
        }*/
        if (maxDateLimit != null && date >= DateUtilities.dateByAddingDays(-1, maxDateLimit!!)) {
            modifyNextButtonState(false)
        } else {
            modifyNextButtonState(true)
        }
    }

    fun onNextButtonClick() {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(step, 1)
        date = calendar.time
        refresh(date)
        callback?.invoke(date)
    }

    fun onPreviousButtonClick() {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(step, -1)
        date = calendar.time
        refresh(date)
        callback?.invoke(date)
    }

    fun onLabelClick() {
    }



    fun  <Event> prevClicks(mapperFunction: (Unit?) -> Event): Observable<Event> {
        return this.previousBtnLayout.clicks()
            .map(mapperFunction)
    }

    fun  <Event> nextClicks(mapperFunction: (Unit?) -> Event): Observable<Event> {
        return this.nextBtnLayout.clicks()
            .map(mapperFunction)
    }
}