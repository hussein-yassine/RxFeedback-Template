package com.codefather.vanapp.Utils

import android.util.Log
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject
import org.notests.rxfeedback.Optional
import org.notests.sharedsequence.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

typealias Feedback<State, Event> = (ObservableSchedulerContext<State>) -> Observable<Event>

fun <State, Event> attachFeedbacks(
    stateDriver: ObservableSchedulerContext<State>,
    eventsSubject: Subject<Event>,
    vararg feedbacks: Feedback<State, Event>
): Disposable {
    return Observable.merge(
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

