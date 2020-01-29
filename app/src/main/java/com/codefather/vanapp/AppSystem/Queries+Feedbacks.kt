package com.codefather.vanapp.AppSystem

import com.codefather.vanapp.Manager.RoomManager
import com.codefather.vanapp.RoomDatabase.Entities.ClientEntity
import com.codefather.vanapp.RoomDatabase.Entities.HistoryEntity
import com.codefather.vanapp.StateFeedback
import com.codefather.vanapp.Utils.LoadState
import com.codefather.vanapp.Utils.LoggerUtil
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import org.notests.rxfeedback.Optional
import org.notests.rxfeedback.react

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
    return react<BusSystem.BusState, Unit, BusSystem.BusEvent>(
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
                .map<BusSystem.BusEvent> { data ->
                    BusSystem.BusEvent.PopulatedData(data)
                }.doOnError { error ->
                    LoggerUtil.crashLog("Error Log", error)
                }
        }
    )
}

private fun createBusData(clients: List<ClientEntity>,
                          historyEntities: List<HistoryEntity>): BusSystem.BusState.BusData{
    val clientsList = clients.map { it.toClientDto() }
    val historyList = historyEntities.map { it.toHistoryDto() }
    historyList.forEach { history ->
        history.customerDto = clientsList.firstOrNull{ it.customerId == history.clientId }
    }
    return BusSystem.BusState.BusData(
        historyList.sortedByDescending { it.date },
        clientsList
    )
}