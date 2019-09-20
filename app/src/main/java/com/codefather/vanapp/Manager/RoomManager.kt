package com.codefather.vanapp.Manager

import com.codefather.vanapp.RoomDatabase.Repositories.ClientsRepository
import com.codefather.vanapp.RoomDatabase.Repositories.HistoryRepository
import com.codefather.vanapp.VanApplication

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

object RoomManager {
    fun getClientsRepository(): ClientsRepository {
        return ClientsRepository(VanApplication.database.clientDao())
    }
    fun getHistoryRepository(): HistoryRepository {
        return HistoryRepository(VanApplication.database.historyDao())
    }
}