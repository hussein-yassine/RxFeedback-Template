package com.codefather.vanapp.Models

import android.arch.persistence.room.ColumnInfo
import com.codefather.vanapp.AppSystem.HistoryViewModel
import com.codefather.vanapp.Utils.DateUtilities
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

data class HistoryDto (
    var historyId: Int = 0,
    var clientId: Int = 0,
    var date: Date = DateUtilities.today(),
    var client: ClientDto? = ClientDto(),
    var isPaid: Boolean = false,
    var deleted: Boolean = false
){
    fun toHistoryViewModel(): HistoryViewModel{
        return HistoryViewModel(
            historyId = historyId,
            clientId = clientId,
            date = date,
            client = client ?: ClientDto(),
            isPaid = isPaid,
            deleted = deleted
        )
    }
}