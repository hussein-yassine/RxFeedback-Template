package com.codefather.vanapp.RoomDatabase.Entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.codefather.vanapp.Models.HistoryDto
import com.codefather.vanapp.Utils.DateUtilities
import java.util.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */
@Entity (tableName = "History",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "historyId")
    var historyId: Int = 0,
    @ColumnInfo(name = "clientId")
    var clientId: Int = 0,
    @ColumnInfo(name = "date")
    var date: Date = DateUtilities.today(),
    @ColumnInfo(name = "isPaid")
    var isPaid: Boolean = false,
    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
){
    fun toHistoryDto(): HistoryDto {
        return HistoryDto(
            historyId = historyId,
            clientId = clientId,
            date = date,
            isPaid = isPaid,
            deleted = deleted
        )
    }
}