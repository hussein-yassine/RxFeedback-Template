package com.codefather.vanapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.codefather.vanapp.Utils.FragmentListener
import com.codefather.vanapp.Utils.attachFeedbacks
import com.codefather.vanapp.VanApplication.Companion.baseActivity
import com.codefather.vanapp.VanApplication.Companion.context
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.notests.rxfeedback.Bindings
import org.notests.rxfeedback.bindSafe
import org.notests.rxfeedback.system
import org.notests.sharedsequence.*


// BaseActivity this is our root base activity for all activities
// this contains useful functions and implement the drawer and toolbar

typealias Feedback<State, Event> = (Driver<State>) -> Signal<Event>

abstract class BaseActivity<State, Event> : AppCompatActivity(),
    FragmentListener<Feedback<State, Event>> {

    private var compositeDisposable = CompositeDisposable()
    lateinit var stateDriver: Driver<State>
    var savedState: State? = null
    private val eventsSubject = PublishSubject.create<Event>()
    private val eventsSignal = Signal(eventsSubject)

    private var mToolBarNavigationListenerIsRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        baseActivity = this
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable = CompositeDisposable()

        stateDriver = onCreateSystem()

        stateDriver.drive().addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.dispose()
    }

    abstract fun onCreateSystem(): Driver<State>

    override fun onAttachFeedbacks(vararg feedbacks: Feedback<State, Event>): Disposable {
        return attachFeedbacks(stateDriver, eventsSubject, *feedbacks)
    }

    fun createSystem(
        initialState: State,
        init: State?.() -> Unit,
        eventReducer: (State, Event) -> State,
        vararg feedbacks: (Driver<State>) -> Signal<Event>
    ): Driver<State> {
        val state = savedState.apply(init) ?: initialState
        val driver = Driver.system(
            state,
            eventReducer,
            *feedbacks
        )
        return driver
    }


    fun bindEventsSubject(): Feedback<State, Event> {
        return bindSafe {
            Bindings.safe(
                subscriptions = listOf(),
                events = listOf(eventsSignal)
            )
        }
    }

    fun saveState(): Feedback<State, Event> {
        return bindSafe { stateDriver ->
            Bindings.safe(
                subscriptions = listOf(
                    stateDriver.drive { savedState = it }
                ),
                events = listOf(
                    Signal.never()
                )
            )
        }
    }

    protected fun getEventsSubject(): PublishSubject<Event> {
        return eventsSubject
    }

    /**
     * Helper functions for fragment transactions
     */
    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }


    /**
     * pushFragment is used to push a fragment over the current fragment.
     * use popLastFragment to go back.
     */
    fun pushFragment(
        fragment: Fragment,
        frameId: Int,
        fragmentTag: String,
        addToBackStack: Boolean = true,
        backStackName: String = ""
    ) {

        supportFragmentManager.inTransaction {

            if (addToBackStack) {

                replace(frameId, fragment, fragmentTag)
                addToBackStack(backStackName)

            } else {

                replace(frameId, fragment, fragmentTag)
            }
        }
    }

    /**
     * popLastFragment removes the current fragment, returns the previous one,
     */
    protected fun popLastFragment() {
        supportFragmentManager.popBackStackImmediate()
    }

    protected fun popAllFragment() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    /**
     * onSupportNavigateUp is called when back button is pressed
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * setBackButtonVisible shows or hides back button
     */
    protected fun setBackButtonVisible(boolean: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(boolean)
        supportActionBar?.setHomeButtonEnabled(boolean)
        supportActionBar?.setDisplayShowHomeEnabled(boolean)
    }
}