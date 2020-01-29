package com.codefather.vanapp.Adapters

import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.codefather.vanapp.AppSystem.HistoryViewModel
import com.codefather.vanapp.R
import com.codefather.vanapp.Utils.ImageUtilities
import com.codefather.vanapp.VanApplication.Companion.context
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.client_icon_layout.view.*
import kotlinx.android.synthetic.main.client_icon_name_layout.view.*
import kotlinx.android.synthetic.main.history_cell.view.*

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var historyList = listOf<HistoryViewModel>()
    private val paidClickSubject = PublishSubject.create<Int>()
    private val deleteClickSubject = PublishSubject.create<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val clientImageView: ImageView = view.clientImageView
        val clientIconNameTextView: TextView = view.clientIconNameTextView
        val historyCardItemTextView: TextView = view.historyCardItemTextView
        val historyCardBottomLayout: View = view.historyCardBottomLayout
        val historyCardPaid: View = view.historyCardPaid
        val historyCardDelete: View = view.historyCardDelete

        fun bind(history: HistoryViewModel, pos: Int) {

            historyCardPaid.setOnClickListener { paidClickSubject.onNext(pos) }
            historyCardDelete.setOnClickListener { deleteClickSubject.onNext(pos) }

            ImageUtilities.loadRoundedImageWithText(
                context,
                clientImageView,
                history.client.getAlias(),
                ImageUtilities.ImageSize.THUMBNAIL
            )

            clientIconNameTextView.text = history.client.customerName
            clientIconNameTextView.setTypeface(
                ResourcesCompat.getFont(context, R.font.nunito),
                Typeface.NORMAL
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = historyList[position]
        holder.bind(request, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return ViewHolder(layoutInflater.inflate(R.layout.history_cell, parent, false))
    }

    override fun getItemCount(): Int = historyList.size


    fun update(requestsViewModels: List<HistoryViewModel>) {
        historyList = requestsViewModels
        notifyDataSetChanged()
    }

    fun paidClicks(): Observable<Int> {
        return paidClickSubject
    }

    fun deleteClicks(): Observable<Int> {
        return deleteClickSubject
    }
}