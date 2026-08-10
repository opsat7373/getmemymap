package com.opsat.getmemymap.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor() : ViewModel() {

    var networkPolicy: NetworkPolicy? = null
        private set

    fun setNetworkPolicy(policy: NetworkPolicy) {
        networkPolicy = policy
    }
}

enum class NetworkPolicy {
    WIFI_ONLY,
    ANY_NETWORK
}