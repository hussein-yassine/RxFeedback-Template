package com.codefather.vanapp.Utils

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import org.notests.sharedsequence.*
import java.util.*


//This files contains extensions to reduce Rx Views boilerplate code


fun <Event> EditText.watchChanges(mapperFunction: (String) -> Event): Signal<Event> {
    return this.textChanges()
        .skip(1)
        .map { it.toString() }
        .distinctUntilChanged()
        .asSignal(Signal.empty())
        .map(mapperFunction)
}

fun <Event> View.watchClicks(mapperFunction: (Unit?) -> Event): Signal<Event> {
    return this.clicks()
        .asSignal(Signal.empty())
        .map(mapperFunction)
}

fun <Event> RadioButton.watchSelected(mapperFunction: (Unit) -> Event): Signal<Event> {
    return this.checkedChanges().asSignal(Signal.empty())
        .switchMapSignal { isSelected ->
            if (isSelected) {
                Signal(Observable.just(mapperFunction(Unit)))
            } else {
                Signal.empty()
            }
        }

}

fun <Event> View.watchFocusChange(mapperFunction: (Boolean) -> Event): Signal<Event> {
    return this.focusChanges()
        .distinctUntilChanged()
        .asSignal(Signal.empty())
        .switchMapSignal { isFocused ->
            Signal(Observable.just(mapperFunction(isFocused)))
        }
}

fun Switch.watchChange(): Signal<Boolean> {
    return Observable.create<Boolean> { emitter ->
        setOnCheckedChangeListener { _, enabled ->
            emitter.onNext(enabled)
        }
    }.asSignalLogFailure()
}


fun CheckBox.watchChange(): Observable<Boolean> {
    return this.checkedChanges().distinctUntilChanged()
}


enum class HorizontalSwipeDirection {
    Left, Right
}

fun View.horizontalSwipeEvents(): Signal<HorizontalSwipeDirection> {
    return Observable.create<HorizontalSwipeDirection> { emitter ->
        this.setOnTouchListener(object : View.OnTouchListener {
            private val MIN_DISTANCE = (100 * resources.displayMetrics.density + 0.5f).toInt()
            private var x1: Float = 0.toFloat()
            private var x2: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> x1 = event.x
                    MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX = x2 - x1
                        if (deltaX < -MIN_DISTANCE) {
                            emitter.onNext(HorizontalSwipeDirection.Left)
                        }
                        if (deltaX > MIN_DISTANCE) {
                            emitter.onNext(HorizontalSwipeDirection.Right)
                        }
                    }
                }
                return false
            }
        })
        emitter.setCancellable { this.setOnTouchListener(null) }
    }.asSignal(Signal.empty())
}

fun TabLayout.tabChanged(): Signal<TabLayout.Tab> {
    return Observable.create<TabLayout.Tab> {

        val listener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                it.onNext(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        }
        this.addOnTabSelectedListener(listener)
        it.setCancellable { this.removeOnTabSelectedListener(listener) }
    }.asSignal { Signal.empty() }
}

fun ViewPager.pageChanged(): Signal<Int> {
    return Observable.create<Int> { emitter ->

        val listener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                emitter.onNext(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        }
        this.addOnPageChangeListener(listener)
        emitter.setCancellable { this.removeOnPageChangeListener(listener) }

    }.asSignal { Signal.empty() }
}


fun AppCompatSpinner.itemSelected(): Signal<Int> {
    return Observable.create<Int> { emitter ->
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                emitter.onNext(position)
            }

            override fun onNothingSelected(adapter: AdapterView<*>?) {
            }
        }
        this.onItemSelectedListener = listener
        emitter.setCancellable { this.onItemSelectedListener = null }
    }.asSignalLogFailure()
}



