package com.codefather.vanapp.Utils

import android.util.Log


fun Any.fastLog(message: String) {
    LoggerUtil.fastLog(message)
}

fun Any.warn(message: String) {
    Log.w(this::class.java.simpleName, message)
}

fun Any.debug(message: String) {
    Log.d(this::class.java.simpleName, message)
}

fun Any.crashLog(title: String, e: Throwable){
    LoggerUtil.crashLog(title, e)
}