package com.codefather.vanapp.RoomDatabase.Repositories

import com.codefather.vanapp.RoomDatabase.DAOs.HistoryDao
import com.codefather.vanapp.RoomDatabase.Entities.HistoryEntity
import io.reactivex.Flowable

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class HistoryRepository (private val historyDao: HistoryDao) {

    fun getAll(): Flowable<List<HistoryEntity>> {
        return historyDao.getAll()
    }

    fun insert(entity: HistoryEntity) {
        historyDao.insertItem(entity)
    }

    fun insertActivities(activities: List<HistoryEntity>) {
        historyDao.insertItems(activities)
    }

    fun update(entity: HistoryEntity) {
        historyDao.updateItem(entity)
    }

    fun delete(entity: HistoryEntity){
        historyDao.deleteItem(entity)
    }
}