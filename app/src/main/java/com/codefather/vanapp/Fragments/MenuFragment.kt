package com.codefather.vanapp.BusInfo.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codefather.vanapp.*
import com.codefather.vanapp.AppSystem.BusSystem
import com.codefather.vanapp.Utils.FragmentListener
import com.codefather.vanapp.Utils.watchClicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.notests.rxfeedback.Bindings
import org.notests.rxfeedback.bindSafe
import kotlinx.android.synthetic.main.fragment_menu.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class MenuFragment: BaseFragment() {

    private var listener: FragmentListener<StateFeedback>? = null
    private var compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FragmentListener<*>) {
            listener = context as FragmentListener<StateFeedback>
        } else {
            throw RuntimeException("Activity must implement FragmentListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable = CompositeDisposable()
        listener?.onFragmentResumed(BusActivity.TAG_MENU)

        val stateSubscription = listener?.onAttachFeedbacks(
            bindUI
        )
        stateSubscription?.addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.dispose()
    }

    private val bindUI = bindSafe<BusSystem.BusState, BusSystem.BusEvent> { state ->
        Bindings.safe(
            subscriptions = listOf(),
            events = listOf(
                btHistory.watchClicks<BusSystem.BusEvent> { BusSystem.BusEvent.ClickedHistory },
                btAddClient.watchClicks<BusSystem.BusEvent> { BusSystem.BusEvent.ClickedAddClient }
            )
        )
    }
}