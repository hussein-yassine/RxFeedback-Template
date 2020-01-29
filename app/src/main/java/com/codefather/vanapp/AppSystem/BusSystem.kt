package com.codefather.vanapp.AppSystem

import com.codefather.vanapp.BusActivity
import com.codefather.vanapp.Models.CustomerDto
import com.codefather.vanapp.Models.HistoryDto
import com.codefather.vanapp.Utils.DateUtilities
import com.codefather.vanapp.Utils.LoadState
import com.codefather.vanapp.Utils.fastLog
import org.notests.rxfeedback.Optional
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

object BusSystem {

    data class BusState (

        var selectedDate: Date = Date(),
        var clients:List<CustomerDto> = listOf(),
        var historyItems:List<HistoryDto> = listOf(),
        var busData: BusData = BusData(),
        var dataResult: LoadState<BusData> = LoadState.Loading(),
        var route: Optional<Route> = Optional.Some(Route.Menu),
        var currentFragmentTag: String = BusActivity.TAG_MENU
    ){
        sealed class Route {
            object AddClient : Route()
            object History : Route()
            object Menu : Route()
        }

        data class BusData(
            var historyItems:List<HistoryDto> = listOf(),
            var clients:List<CustomerDto> = listOf()
        )
    }

    sealed class BusEvent {
        data class ClickedPaid(val position: Int): BusEvent()
        data class ClickedDeleteUser(val position: Int): BusEvent()

        object ClickedPreviousDate: BusEvent()
        object ClickedNextDate: BusEvent()
        object ClickedHistory: BusEvent()
        object ClickedAddClient: BusEvent()
        data class PopulatedData(val busData: BusState.BusData): BusEvent()
        data class Navigated(val fragmentTag: String): BusEvent()
    }

    fun reduce(state: BusState, event: BusEvent): BusState {
        fastLog("New Event: $event")
        return when(event){
            is BusEvent.ClickedPreviousDate -> state.copy().apply {
                selectedDate = DateUtilities.dateByAddingDays(-1, selectedDate)
            }
            is BusEvent.ClickedNextDate -> state.copy().apply {
                selectedDate = DateUtilities.dateByAddingDays(1, selectedDate)
            }
            is BusEvent.PopulatedData -> state.copy().apply {
                busData = event.busData
                clients = busData.clients
                historyItems = busData.historyItems
                dataResult = LoadState.Loaded(busData)
            }
            is BusEvent.Navigated -> state.copy().apply {
                currentFragmentTag = event.fragmentTag
                route = Optional.None()
            }
            BusEvent.ClickedHistory -> state.copy().apply {
                route = Optional.Some(BusState.Route.History)
            }
            BusEvent.ClickedAddClient -> state.copy().apply {
                route = Optional.Some(BusState.Route.AddClient)
            }
            is BusEvent.ClickedPaid -> state.copy().apply {

            }
            is BusEvent.ClickedDeleteUser -> state.copy().apply {

            }
        }
    }
}