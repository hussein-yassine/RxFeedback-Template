package com.codefather.vanapp.Models

import com.codefather.vanapp.AppSystem.ClientViewModel

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

data class CustomerDto (
    var customerId: Int = 0,
    var customerName: String = "",
    var deleted: Boolean = false
){

    fun getAlias(): String {
        return (customerName.take(1)).toUpperCase()
    }

    fun toClientViewModel(): ClientViewModel {
        return ClientViewModel(
            clientId = customerId,
            clientName = customerName,
            deleted = deleted
        )
    }
}