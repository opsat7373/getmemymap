package com.opsat.getmemymap.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkChecker @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun isWifiConnected(): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = cm.activeNetwork ?: return false

        val capabilities = cm.getNetworkCapabilities(network)
            ?: return false

        return capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        )
    }

    fun isMobileDataConnected(): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = cm.activeNetwork ?: return false

        val capabilities = cm.getNetworkCapabilities(network)
            ?: return false

        return capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        )
    }

    fun hasInternet(): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = cm.activeNetwork ?: return false

        val capabilities = cm.getNetworkCapabilities(network)
            ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}