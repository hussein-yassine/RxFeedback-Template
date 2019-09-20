package com.codefather.vanapp.Utils

import android.util.Log
// LoggerUtil Util is responsible for logging and backing up logs
// TODO:
object LoggerUtil {

    fun Debug(string: String) {
        Log.d("[DEBUG]", string)
    }

    fun fastLog(string: String?) {
        Log.wtf("[LOG]", string)
    }

    fun log(title: String,string: String?) {
        Log.wtf(title, string)
    }

    fun crashLog(title: String, e: Throwable){
        Log.wtf(title, e.message)
    }
}