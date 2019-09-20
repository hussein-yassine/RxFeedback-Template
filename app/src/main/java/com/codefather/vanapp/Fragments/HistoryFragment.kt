package com.codefather.vanapp.BusInfo.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codefather.vanapp.*
import com.codefather.vanapp.Adapters.HistoryAdapter
import com.codefather.vanapp.AppSystem.BusSystem
import com.codefather.vanapp.AppSystem.HistoryViewModel
import com.codefather.vanapp.AppSystem.getDate
import com.codefather.vanapp.AppSystem.getHistoryViewModels
import com.codefather.vanapp.Utils.Constants
import com.codefather.vanapp.Utils.DateUtilities
import com.codefather.vanapp.Utils.FragmentListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_history.*
import org.notests.rxfeedback.Bindings
import org.notests.rxfeedback.bindSafe
import org.notests.sharedsequence.distinctUntilChanged
import org.notests.sharedsequence.drive
import org.notests.sharedsequence.map
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class HistoryFragment : BaseFragment() {

    private var listener: FragmentListener<StateFeedback>? = null
    private var currentDate = DateUtilities.getCalendar()
    private var compositeDisposable = CompositeDisposable()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FragmentListener<*>) {
            listener = context as FragmentListener<StateFeedback>
        } else {
            throw RuntimeException("Activity must implement FragmentListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable = CompositeDisposable()
        listener?.onFragmentResumed(BusActivity.TAG_HISTORY)

        val stateSubscription = listener?.onAttachFeedbacks(
            bindUI
        )
        stateSubscription?.addTo(compositeDisposable)

        loadData()
        initDate()
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.dispose()
    }

    private val bindUI = bindSafe<BusSystem.BusState, BusSystem.BusEvent> { state ->
        Bindings.safe(
            subscriptions = listOf(
                state.map { it.getHistoryViewModels }.distinctUntilChanged().drive {
                    updateList(it)
                },
                state.map { it.getDate }.distinctUntilChanged().drive { dateSelector.refresh(it) }
            ),
            events = listOf(
                dateSelector.prevClicks <BusSystem.BusEvent>{ BusSystem.BusEvent.ClickedPreviousDate },
                dateSelector.nextClicks <BusSystem.BusEvent>{ BusSystem.BusEvent.ClickedNextDate },
                historyAdapter.paidClicks().map<Int, BusSystem.BusEvent> {
                    BusSystem.BusEvent.ClickedPaid(it)
                },
                historyAdapter.deleteClicks().map<Int, BusSystem.BusEvent> {
                    BusSystem.BusEvent.ClickedDeleteUser(it)
                }
            )
        )
    }

    private fun updateList(historysViewModels: List<HistoryViewModel>) {
        historyAdapter.update(historysViewModels)
    }

    private fun loadData(){
        historyAdapter = HistoryAdapter()
        rvHistory.layoutManager = LinearLayoutManager(context)
        rvHistory.adapter = historyAdapter
    }

    private fun initDate(){
        // initialize date selector
        dateSelector.dateFormat = Constants.DateFormats.FULL_DATE_DAY_FORMAT
        dateSelector.step = Calendar.DAY_OF_WEEK
        dateSelector.preferedUppercasingText = true
        dateSelector.setDate(DateUtilities.today(), null, null) {selectedDate ->
            currentDate = DateUtilities.getCalendar(selectedDate)
        }
    }
}