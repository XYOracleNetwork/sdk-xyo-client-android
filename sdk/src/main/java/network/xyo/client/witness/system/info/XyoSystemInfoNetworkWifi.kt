package network.xyo.client.witness.system.info

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonClass
import java.net.NetworkInterface
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("HardwareIds")
        fun detect(context: Context): XyoSystemInfoNetworkWifi? {
            // setup latch and wifiInfo inside companion so it isn't shared across instances
            val latch = CountDownLatch(1)
            val wifiInfo = accessNetworkChanges(context, latch)

            try {
                // Wait for up to 5 seconds for the network capabilities
                latch.await(5, TimeUnit.SECONDS)
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
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun accessNetworkChanges(context: Context, latch: CountDownLatch): WifiInfo? {
            var wifiInfo: WifiInfo? = null
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

            val networkCallback = object : NetworkCallback() {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val localWifiInfo = networkCapabilities.transportInfo as? WifiInfo
                    wifiInfo = localWifiInfo
                    // countDown to zero to lift the latch
                    latch.countDown()
                }
            }

            // For requesting a network
            connectivityManager.requestNetwork(request, networkCallback)

            // For listening to network changes
            connectivityManager.registerNetworkCallback(request, networkCallback)

            return wifiInfo
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