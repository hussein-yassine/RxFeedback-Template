package com.codefather.vanapp.RoomDatabase.DAOs

import android.arch.persistence.room.*
import com.codefather.vanapp.Utils.LoggerUtil
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

interface BaseDao<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(objs: List<T>)

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(obj:T){
        Completable
            .fromAction { insert(obj) }
            .subscribeOn(Schedulers.io())
            .subscribe(object: CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    LoggerUtil.fastLog("onComplete")
                }

                override fun onError(e: Throwable) {
                    LoggerUtil.fastLog(e.message)
                }
            })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(objs: List<T>){
        Completable
            .fromAction { insertList(objs) }
            .subscribeOn(Schedulers.io())
            .subscribe(object: CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    LoggerUtil.fastLog("onComplete")
                }

                override fun onError(e: Throwable) {
                    LoggerUtil.fastLog(e.message)
                }
            })
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(obj:T){
        Completable
            .fromAction { update(obj) }
            .subscribeOn(Schedulers.io())
            .subscribe(object: CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    LoggerUtil.fastLog("onComplete")
                }

                override fun onError(e: Throwable) {
                    LoggerUtil.fastLog(e.message)
                }
            })
    }

    @Delete
    fun deleteItem(obj:T){
        Completable
            .fromAction { delete(obj) }
            .subscribeOn(Schedulers.io())
            .subscribe(object: CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    LoggerUtil.fastLog("onComplete")
                }

                override fun onError(e: Throwable) {
                    LoggerUtil.fastLog(e.message)
                }
            })
    }
}