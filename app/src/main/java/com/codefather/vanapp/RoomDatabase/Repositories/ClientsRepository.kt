package com.codefather.vanapp.RoomDatabase.Repositories

import com.codefather.vanapp.RoomDatabase.DAOs.ClientDao
import com.codefather.vanapp.RoomDatabase.Entities.ClientEntity
import io.reactivex.Flowable

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class ClientsRepository (private val clientDao: ClientDao) {

    fun getAll(): Flowable<List<ClientEntity>> {
        return clientDao.getAll()
    }

    fun insert(entity: ClientEntity) {
        clientDao.insertItem(entity)
    }

    fun insertActivities(activities: List<ClientEntity>) {
        clientDao.insertItems(activities)
    }

    fun update(entity: ClientEntity) {
        clientDao.updateItem(entity)
    }

    fun delete(entity: ClientEntity){
        clientDao.deleteItem(entity)
    }
}