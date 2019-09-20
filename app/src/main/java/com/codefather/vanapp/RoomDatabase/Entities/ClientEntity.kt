package com.codefather.vanapp.RoomDatabase.Entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.codefather.vanapp.Models.ClientDto
import com.codefather.vanapp.Utils.DateUtilities
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */
@Entity(tableName = "Client")
data class ClientEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "clientId")
    var clientId: Int = 0,
    @ColumnInfo(name = "clientName")
    var clientName: String = "",
    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
){
    fun toClientDto(): ClientDto{
        return ClientDto(
            clientId = clientId,
            clientName = clientName,
            deleted = deleted
        )
    }
}