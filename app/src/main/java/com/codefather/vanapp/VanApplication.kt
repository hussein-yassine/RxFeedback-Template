package com.codefather.vanapp

import android.content.Intent
import android.os.Handler
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.codefather.vanapp.RoomDatabase.RoomDatabase.AppDatabase
import com.codefather.vanapp.Utils.LoggerUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import org.notests.sharedsequence.api.ErrorReporting
import org.notests.sharedsequence.drive
import java.io.IOException
import java.net.SocketException

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class VanApplication : MultiDexApplication() {

    companion object {
        var baseActivity: AppCompatActivity? = null
        private val compositeDisposable = CompositeDisposable()
        lateinit var context: VanApplication
        lateinit var database: AppDatabase
        const val ENGLISH = 1
        const val ARABIC = 2
        var language = ENGLISH
    }

    override fun onCreate() {
        super.onCreate()

        ErrorReporting.exceptions().subscribe {
            LoggerUtil.crashLog("ErrorReporting exception", it)
        }

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                LoggerUtil.crashLog("UndeliverableException", e)
            }
            if (e is IOException || e is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                LoggerUtil.log("IOException or SocketException", e.message)
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                Log.wtf("InterruptedException", e.message)
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                LoggerUtil.crashLog("NullPointer or  IllegalArgument Exception", e)
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                LoggerUtil.crashLog("IllegalStateException", e)
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            LoggerUtil.crashLog("Undeliverable exception received, not sure what to do", e)
        }
        context = this
        database = AppDatabase(this)
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}