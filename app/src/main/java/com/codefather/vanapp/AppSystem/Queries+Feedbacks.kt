package com.codefather.vanapp.AppSystem

import android.util.Log
import com.codefather.vanapp.Feedback
import com.codefather.vanapp.Manager.RoomManager
import com.codefather.vanapp.RoomDatabase.Entities.ClientEntity
import com.codefather.vanapp.RoomDatabase.Entities.HistoryEntity
import com.codefather.vanapp.StateFeedback
import com.codefather.vanapp.Utils.LoadState
import com.codefather.vanapp.Utils.reactSafely
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import org.notests.rxfeedback.Optional
import org.notests.sharedsequence.Signal
import org.notests.sharedsequence.asSignal
import org.notests.sharedsequence.just

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

fun BusSystem.BusState.populateData(): Optional<Unit> {
    return if (dataResult is LoadState.Loading){
       Optional.Some(Unit)
    }else{
        Optional.None()
    }
}

fun populateData(): StateFeedback {
    return reactSafely<BusSystem.BusState, Unit, BusSystem.BusEvent>(
        query = {
            it.populateData()
        },
        effects = {
            val clients = RoomManager.getClientsRepository().getAll().toObservable()
            val historyEntities = RoomManager.getHistoryRepository().getAll().toObservable()

            val busObservable =  Observables.combineLatest(
                clients,
                historyEntities,
                ::createBusData
            )

            busObservable
                .subscribeOn(Schedulers.io())
                .doOnError { Log.wtf("error ", "is " + it.message) }
                .map<BusSystem.BusEvent> { data ->
                    BusSystem.BusEvent.PopulatedData(data)
                }.asSignal{
                    Signal.just(BusSystem.BusEvent.PopulatedData(BusSystem.BusState.BusData()))
                }
        }
    )
}

private fun createBusData(clients: List<ClientEntity>,
                          historyEntities: List<HistoryEntity>): BusSystem.BusState.BusData{
    val clientsList = clients.map { it.toClientDto() }
    val historyList = historyEntities.map { it.toHistoryDto() }
    historyList.forEach { history ->
        history.client = clientsList.firstOrNull{ it.clientId == history.clientId }
    }
    return BusSystem.BusState.BusData(
        historyList.sortedByDescending { it.date },
        clientsList
    )
}