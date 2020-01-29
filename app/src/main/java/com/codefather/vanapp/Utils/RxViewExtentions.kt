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


fun <Event> EditText.watchChanges(mapperFunction: (String) -> Event): Observable<Event> {
    return this.textChanges()
        .skip(1)
        .map { it.toString() }
        .distinctUntilChanged()
        .map(mapperFunction)
}

fun <Event> View.watchClicks(mapperFunction: (Unit?) -> Event): Observable<Event> {
    return this.clicks()
        .map(mapperFunction)
}

fun <Event> RadioButton.watchSelected(mapperFunction: (Unit) -> Event): Observable<Event> {
    return this.checkedChanges()
        .switchMap { isSelected ->
            if (isSelected) {
                Observable.just(mapperFunction(Unit))
            } else {
                Observable.empty()
            }
        }

}

fun <Event> View.watchFocusChange(mapperFunction: (Boolean) -> Event): Observable<Event> {
    return this.focusChanges()
        .distinctUntilChanged()
        .switchMap{ isFocused ->
            Observable.just(mapperFunction(isFocused))
        }
}

fun Switch.watchChange(): Observable<Boolean> {
    return Observable.create<Boolean> { emitter ->
        setOnCheckedChangeListener { _, enabled ->
            emitter.onNext(enabled)
        }
    }
}


fun CheckBox.watchChange(): Observable<Boolean> {
    return this.checkedChanges().distinctUntilChanged()
}


enum class HorizontalSwipeDirection {
    Left, Right
}

fun View.horizontalSwipeEvents(): Observable<HorizontalSwipeDirection> {
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
    }
}

fun TabLayout.tabChanged(): Observable<TabLayout.Tab> {
    return io.reactivex.Observable.create<TabLayout.Tab> {

        val listener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                it.onNext(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        }
        this.addOnTabSelectedListener(listener)
        it.setCancellable { this.removeOnTabSelectedListener(listener) }
    }
}

fun ViewPager.pageChanged(): Observable<Int> {
    return io.reactivex.Observable.create<Int> { emitter ->

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

    }
}

fun AppCompatSpinner.itemSelected(): Observable<Int> {
    return io.reactivex.Observable.create<Int> { emitter ->
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                emitter.onNext(position)
            }

            override fun onNothingSelected(adapter: AdapterView<*>?) {
            }
        }
        this.onItemSelectedListener = listener
        emitter.setCancellable { this.onItemSelectedListener = null }
    }
}

fun CompactCalendarView.dayClicked(): Observable<Date> {
    return io.reactivex.Observable.create<Date> { emitter ->

        val listener = object : CompactCalendarView.CompactCalendarViewListener {

            override fun onDayClick(dateClicked: Date?) {
                dateClicked?.let{ date ->
                    fastLog("date  " + date)
                    emitter.onNext(date)
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                fastLog("month changed")
            }
        }
        this.setListener(listener)
        /*{ calendar, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.YEAR, month)
                emitter.onNext(cal.time)
            }
        this.setOnDateChangeListener(listener)*/

    }
}

fun BottomSheetBehavior<View>.setOnStatesChanged(): Observable<DashboardState.BottomSheetData> {
    return io.reactivex.Observable.create<DashboardState.BottomSheetData> { emitter ->

        val bottomSheetData = DashboardState.BottomSheetData(0, 0f)
        val listener = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheetData.slideOffset = slideOffset
                emitter.onNext(bottomSheetData)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetData.state = newState
                emitter.onNext(bottomSheetData)
            }
        }
        this.setBottomSheetCallback(listener)
        emitter.setCancellable { this.setBottomSheetCallback(null) }
    }
}

fun DrawerLayout.drawerStateChanged(): Observable<Boolean> {
    return io.reactivex.Observable.create<Boolean> { emitter ->

        val listener = object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(p0: Int) {
            }

            override fun onDrawerSlide(p0: View, p1: Float) {
            }

            override fun onDrawerOpened(p0: View) {
                emitter.onNext(true)
            }

            override fun onDrawerClosed(p0: View) {
                emitter.onNext(false)
            }
        }

        this.addDrawerListener(listener)
        emitter.setCancellable { this.removeDrawerListener(listener) }

    }
}

fun SwipeRefreshLayout.refreshed(): Observable<Unit> {
    return io.reactivex.Observable.create<Unit> { emitter ->
        val listener = SwipeRefreshLayout.OnRefreshListener { emitter.onNext(kotlin.Unit) }
        this.setOnRefreshListener(listener)
        emitter.setCancellable { this.setOnRefreshListener { null } }
    }
}

fun NestedScrollView.bottomReached(): Observable<Unit> {
    return io.reactivex.Observable.create<Unit> { emitter ->
        val listener = object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                val view = v
                view?.let { v ->

                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        android.util.Log.wtf("ScrollView", "BOTTOM SCROLL")
                        emitter.onNext(kotlin.Unit)
                    }
                }
            }
        }
        this.setOnScrollChangeListener(listener)
    }
}