package com.codefather.vanapp.Utils

/**
 *
 * Created by Georges Jamous on 05, March, 2019.
 *
 */


import android.util.Log
import com.codefather.vanapp.Utils.LoggerUtil
import io.reactivex.Observable
import org.notests.rxfeedback.Optional
import org.notests.rxfeedback.system
import org.notests.sharedsequence.*

fun <T, U> Driver<T>.mapDistinct(transform: (T) -> U): Driver<U> {
    return this.map(transform).distinctUntilChanged()
}

fun <U> Observable<U>.asSignalElement(onError: (Throwable) -> U): Signal<U> {
    return asSignal { Signal.just(onError(it)) }
}

fun <U> Driver<Optional<U>>.collectNotNull(): Driver<U> {
    return filter { it is Optional.Some }.map { (it as Optional.Some).data }
}

fun <T, U> Driver<T>.collectNotNull(transform: (T) -> Optional<U>): Driver<U> {
    return this.map(transform).collectNotNull()
}

fun <U> Observable<Optional<U>>.collectNotNull(): Observable<U> {
    return filter { it is Optional.Some }.map { (it as Optional.Some).data }
}

fun <U> Observable<U>.asSignalLogFailure(): Signal<U> {
    return asSignal {
        LoggerUtil.crashLog("Signal crashLog failure", it)
        Signal.empty()
    }
}

fun <State, Query, Event> reactSafely(
    query: (State) -> Optional<Query>,
    areEqual: (Query, Query) -> Boolean = { lhs, rhs -> lhs == rhs },
    effects: (Query) -> Signal<Event>
): (Driver<State>) -> Signal<Event> =
    { state ->
        state.map(query)
            .distinctUntilChanged { lhs, rhs ->
                when (lhs) {
                    is Optional.Some<Query> -> {
                        when (rhs) {
                            is Optional.Some<Query> -> areEqual(lhs.data, rhs.data)
                            is Optional.None<Query> -> false
                        }
                    }
                    is Optional.None<Query> -> {
                        when (rhs) {
                            is Optional.Some<Query> -> false
                            is Optional.None<Query> -> true
                        }
                    }
                }
            }.switchMapSignal { control: Optional<Query> ->
                if (control !is Optional.Some<Query>) {
                    return@switchMapSignal Signal.empty<Event>()
                }

                return@switchMapSignal effects(control.data)
                    .asObservable()
                    .observeOn(Signal.scheduler)
                    .subscribeOn(Signal.scheduler)
                    .asSignal<Event> { Signal.empty() }
            }
    }

fun <U> Observable<U>.asDriverLogFailure(): Driver<U> {
    return asDriver {
        //todo crashLog error
        Driver.empty()
    }
}
