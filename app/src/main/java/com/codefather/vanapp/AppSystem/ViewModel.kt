package com.codefather.vanapp.AppSystem

import com.codefather.vanapp.BusActivity
import com.codefather.vanapp.Models.ClientDto
import com.codefather.vanapp.Models.HistoryDto
import com.codefather.vanapp.R
import com.codefather.vanapp.Utils.DateUtilities
import com.codefather.vanapp.Utils.getString
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

val BusSystem.BusState.isBackButtonVisible: Boolean
    get() = currentFragmentTag != BusActivity.TAG_MENU

val BusSystem.BusState.setFragmentTitle: String
    get()  = when (currentFragmentTag) {
        BusActivity.TAG_MENU -> getString(R.string.app_name)
        BusActivity.TAG_HISTORY -> getString(R.string.see_history)
        BusActivity.TAG_ADD_CLIENT -> getString(R.string.add_client)
        else -> ""
    }

val BusSystem.BusState.getDate: Date
    get() = selectedDate

val BusSystem.BusState.getHistoryViewModels: List<HistoryViewModel>
    get() = historyItems.map { it.toHistoryViewModel() }

data class HistoryViewModel(
    val historyId: Int,
    val clientId: Int,
    val date: Date,
    val isPaid: Boolean,
    val deleted: Boolean,
    val client: ClientDto
)

data class ClientViewModel(
    val clientId: Int,
    val clientName: String,
    val deleted: Boolean
){
    fun getAlias(): String {
        return (clientName.take(1)).toUpperCase()
    }
}