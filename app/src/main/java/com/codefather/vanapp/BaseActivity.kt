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
import com.codefather.vanapp.Utils.crashLog
import com.codefather.vanapp.VanApplication.Companion.baseActivity
import com.codefather.vanapp.VanApplication.Companion.context
import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.notests.rxfeedback.*
import org.notests.sharedsequence.*


// BaseActivity this is our root base activity for all activities
// this contains useful functions and implement the drawer and toolbar

typealias Feedback<State, Event> = (ObservableSchedulerContext<State>) -> Observable<Event>

abstract class BaseActivity<State, Event> : AppCompatActivity(),
    FragmentListener<Feedback<State, Event>> {

    private var compositeDisposable = CompositeDisposable()
    lateinit var stateSystem: ObservableSchedulerContext<State>
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

        stateSystem = onCreateSystem()
        stateSystem.source.subscribe().addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.dispose()
    }
    abstract fun onCreateSystem(): ObservableSchedulerContext<State>

    override fun onAttachFeedbacks(vararg feedbacks: Feedback<State, Event>): Disposable {
        return attachFeedbacks(stateSystem, eventsSubject, *feedbacks)
    }


    fun createSystem(
        initialState: State,
        init: State?.() -> Unit,
        eventReducer: (State, Event) -> State,
        vararg feedbacks: (ObservableSchedulerContext<State>) -> Observable<Event>
    ): ObservableSchedulerContext<State> {
        val state = savedState.apply(init) ?: initialState
        val systemSource = Observables.system(
            state,
            eventReducer,
            AndroidSchedulers.mainThread(),
            *feedbacks
        ).doOnError { crashLog("createSystemException", it) }.replayingShare()
        return ObservableSchedulerContext(systemSource, AndroidSchedulers.mainThread())
    }


    fun bindEventsSubject(): Feedback<State, Event> {
        return bind {
            Bindings(
                subscriptions = listOf(),
                events = listOf(eventsSubject)
            )
        }
    }

    fun saveState(): Feedback<State, Event> {
        return bind { stateDriver ->
            Bindings(
                subscriptions = listOf(
                    stateDriver.source.subscribe { savedState = it }
                ),
                events = listOf(
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