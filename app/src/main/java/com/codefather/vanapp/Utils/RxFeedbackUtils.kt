package com.codefather.vanapp.Utils

import android.util.Log
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject
import org.notests.rxfeedback.Optional
import org.notests.sharedsequence.*

/**
 *
 * Created by Georges Jamous on 05, March, 2019.
 *
 */

typealias Feedback<State, Event> = (Driver<State>) -> Signal<Event>

fun <State, Event> attachFeedbacks(
    stateDriver: Driver<State>,
    eventsSubject: Subject<Event>,
    vararg feedbacks: Feedback<State, Event>
): Disposable {
    return Signal.merge(
        feedbacks.map { feedback ->
            feedback(stateDriver)
        })
        .asObservable()
        .doOnError {
            //todo add error logs
        }
        .subscribe { event ->
            eventsSubject.onNext(event)
        }
}


fun <State, Query, Event> customFeedback(
    query: (State) -> Optional<Query>,
    effects: (Query, Driver<State>) -> Signal<Event>
): (Driver<State>) -> Signal<Event> =
    { state ->
        state.map(query)
            .distinctUntilChanged { lhs, rhs ->
                when (lhs) {
                    is Optional.Some<Query> -> {
                        when (rhs) {
                            is Optional.Some<Query> -> lhs.data == rhs.data
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
            }
            .switchMapSignal { control: Optional<Query> ->
                if (control !is Optional.Some<Query>) {
                    return@switchMapSignal Signal.empty<Event>()
                }
                effects(control.data, state)
            }

    }