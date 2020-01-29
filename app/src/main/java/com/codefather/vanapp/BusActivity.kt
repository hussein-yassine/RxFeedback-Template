package com.codefather.vanapp

import android.os.Bundle
import com.codefather.vanapp.AppSystem.BusSystem
import com.codefather.vanapp.AppSystem.isBackButtonVisible
import com.codefather.vanapp.AppSystem.populateData
import com.codefather.vanapp.AppSystem.setFragmentTitle
import com.codefather.vanapp.BusInfo.Fragments.AddCustomerFragment
import com.codefather.vanapp.BusInfo.Fragments.HistoryFragment
import com.codefather.vanapp.BusInfo.Fragments.MenuFragment
import com.codefather.vanapp.Utils.FragmentListener
import com.codefather.vanapp.Utils.collectNotNull
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.notests.rxfeedback.*

import com.codefather.vanapp.AppSystem.BusSystem.BusState as State
import com.codefather.vanapp.AppSystem.BusSystem.BusEvent as Event

typealias StateFeedback = (ObservableSchedulerContext<State>) -> Observable<Event>
class BusActivity : BaseActivity<State, Event>(),
    FragmentListener<StateFeedback> {

    companion object{
        const val TAG_MENU = "fragment_menu"
        const val TAG_HISTORY = "fragment_history"
        const val TAG_ADD_CLIENT = "fragment_add_client"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(customToolbar)
        pushFragment(MenuFragment(), R.id.mainContainer,
            TAG_MENU, false)
    }

    //<editor-fold desc="State Driver and its feedback section">
    override fun onCreateSystem(): ObservableSchedulerContext<State> {

        return createSystem(
            State(),
            {
                this?.route = Optional.None()
                //this?.dataResult = LoadState.Loading()
            },
            BusSystem::reduce,
            bindUI(),
            bindRoutes(),
            bindEventsSubject(),
            populateData(),
            saveState()
        )
    }

    private fun bindUI(): StateFeedback {
        return bind {   stateSource ->
            val state = stateSource.source
            Bindings(
                subscriptions = listOf(
                    state.map { it.isBackButtonVisible }.distinctUntilChanged().subscribe {
                        setBackButtonVisible(it)
                    },
                    state.map { it.setFragmentTitle }.distinctUntilChanged().subscribe {
                        setActionBarTitle(it)
                    }
                ),
                events = listOf()
            )
        }
    }

    private fun bindRoutes(): StateFeedback {
        return bind {   stateSource ->
            val state = stateSource.source
            Bindings(
                subscriptions = listOf(
                    state.map { it.route }.collectNotNull().distinctUntilChanged().subscribe {
                        gotoRoute(it)
                    }
                ),
                events = listOf()
            )
        }
    }

    private fun gotoRoute(route: State.Route) {
        when (route) {
            State.Route.Menu -> {
                popAllFragment()
            }
            State.Route.History -> {
                pushFragment(HistoryFragment(), R.id.mainContainer,
                    TAG_HISTORY, true, "${route}")
            }
            State.Route.AddClient -> {
                pushFragment(AddCustomerFragment(), R.id.mainContainer,
                    TAG_ADD_CLIENT, true, "${route}")
            }
        }

    }
    //</editor-fold>

    private fun setActionBarTitle(text: String) {
        supportActionBar?.title = text
    }

    override fun onFragmentResumed(fragmentTag: String) {
        getEventsSubject().onNext(Event.Navigated(fragmentTag))
    }
}
