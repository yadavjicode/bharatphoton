package app.bharatphoton.service.utils;

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


object ConnectivityObserver {

    private var CoroutineIO = CoroutineScope(Dispatchers.IO)

    private var _internetConnectivityFlow : Channel<Boolean> = Channel()
    var internetConnectivityFlow : kotlinx.coroutines.flow.Flow<Boolean> = _internetConnectivityFlow.receiveAsFlow()

    var _networkStateFlow = MutableStateFlow(false)
    val networkStateFlow = _networkStateFlow.asStateFlow()
    private var connectivityManager : ConnectivityManager? = null

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                _networkStateFlow.value = true

                CoroutineIO.launch {
                    _internetConnectivityFlow.send(true)
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                _networkStateFlow.value = false
                CoroutineIO.launch {
                    _internetConnectivityFlow.send(false)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _networkStateFlow.value = false
                CoroutineIO.launch {
                    _internetConnectivityFlow.send(false)
                }
            }
    }

    fun init(context : Context){
         connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerNetworkCallback(networkCallback)
    }
    private fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
    }
    fun unregisterNetworkCallback(){
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
    fun isInternetAvailable(): Boolean? {
        return try {
            connectivityManager?.activeNetwork != null
        }catch (e:Exception){
            null
        }
    }
}