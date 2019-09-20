package com.codefather.vanapp.Models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey
import com.codefather.vanapp.AppSystem.ClientViewModel

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

data class ClientDto (
    var clientId: Int = 0,
    var clientName: String = "",
    var deleted: Boolean = false
){

    fun getAlias(): String {
        return (clientName.take(1)).toUpperCase()
    }

    fun toClientViewModel(): ClientViewModel {
        return ClientViewModel(
            clientId = clientId,
            clientName = clientName,
            deleted = deleted
        )
    }
}