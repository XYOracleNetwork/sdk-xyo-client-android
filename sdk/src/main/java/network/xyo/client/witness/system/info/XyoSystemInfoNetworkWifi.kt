package network.xyo.client.witness.system.info

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonClass
import java.net.NetworkInterface

@JsonClass(generateAdapter = true)

class XyoSystemInfoNetworkWifi (
    @Suppress("unused")
    val ip: String?,
    @Suppress("unused")
    val mac: String?,
    @Suppress("unused")
    val rssi: Int?,
    @Suppress("unused")
    val ssid: String?
) {
    companion object {
        var wifiInfo: WifiInfo? = null

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("HardwareIds")
        fun detect(context: Context): XyoSystemInfoNetworkWifi? {
            accessNetworkChanges(context)
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

            if (Build.VERSION.SDK_INT >= 23) {
                val network = connectivityManager.activeNetwork
                val networkCaps = connectivityManager.getNetworkCapabilities(network)
                if (networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    return XyoSystemInfoNetworkWifi(
                        getIpAddress(),
                        wifiInfo?.macAddress,
                        wifiInfo?.rssi,
                        wifiInfo?.ssid?.replace("\"", "")
                    )
                }
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun accessNetworkChanges(context: Context) {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

            val networkCallback = object : NetworkCallback() {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val localWifiInfo = networkCapabilities.transportInfo as? WifiInfo
                    wifiInfo = localWifiInfo
                }
            }

            // For requesting a network
            connectivityManager.requestNetwork(request, networkCallback)

            // For listening to network changes
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }

        private fun getIpAddress(): String? {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            networkInterfaces.asSequence().forEach { networkInterface ->
                networkInterface.inetAddresses.asSequence().forEach { inetAddress ->
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress
                    }
                }
            }
            return null
        }
    }
}