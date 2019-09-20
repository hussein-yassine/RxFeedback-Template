package com.codefather.vanapp.RoomDatabase.DAOs

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.codefather.vanapp.RoomDatabase.Entities.ClientEntity
import io.reactivex.Flowable

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

@Dao
interface ClientDao : BaseDao<ClientEntity> {
    @Query("SELECT * FROM Client WHERE deleted = 0")
    fun getAllList(): List<ClientEntity>

    @Query("SELECT * FROM Client WHERE deleted = 0")
    fun getAll(): Flowable<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE clientId = :clientId AND deleted = 0")
    fun findById(clientId: Int): ClientEntity

    @Query("DELETE FROM Client")
    fun deleteAll()
}