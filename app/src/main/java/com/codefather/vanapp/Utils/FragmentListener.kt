package com.codefather.vanapp.Utils

import io.reactivex.disposables.Disposable

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */
 
interface FragmentListener<Feedback> {
    fun onAttachFeedbacks(vararg feedbacks: Feedback): Disposable
    fun onFragmentResumed(fragmentTag: String)
}