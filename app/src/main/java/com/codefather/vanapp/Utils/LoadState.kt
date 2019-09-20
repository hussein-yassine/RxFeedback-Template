package com.codefather.vanapp.Utils

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

sealed class LoadState<T>{
    class Idle<T> : LoadState<T>()
    class Loading<T>: LoadState<T>()
    class Loaded<T>(val data: T): LoadState<T>()
    class Error<T>(val throwable: Throwable): LoadState<T>()
}