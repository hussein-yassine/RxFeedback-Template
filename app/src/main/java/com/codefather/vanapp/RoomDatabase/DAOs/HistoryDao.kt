package com.codefather.vanapp.RoomDatabase.DAOs

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.codefather.vanapp.RoomDatabase.Entities.HistoryEntity
import io.reactivex.Flowable

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */
@Dao
interface HistoryDao : BaseDao<HistoryEntity> {
    @Query("SELECT * FROM History WHERE deleted = 0")
    fun getAllList(): List<HistoryEntity>

    @Query("SELECT * FROM History WHERE deleted = 0")
    fun getAll(): Flowable<List<HistoryEntity>>

    @Query("SELECT * FROM History WHERE clientId = :clientId AND deleted = 0")
    fun findByClientId(clientId: Int): HistoryEntity

    @Query("DELETE FROM History")
    fun deleteAll()
}