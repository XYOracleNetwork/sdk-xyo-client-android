package network.xyo.client.witness.system.info

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.squareup.moshi.JsonClass
import java.net.NetworkInterface

@JsonClass(generateAdapter = true)
class XyoSystemInfoNetworkWifi (
    val ip: String?,
    val mac: String?,
    val rssi: Int?,
    val ssid: String?
) {
    companion object {

        @SuppressLint("HardwareIds")
        fun detect(context: Context): XyoSystemInfoNetworkWifi? {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= 23) {
                val network = connectivityManager.activeNetwork
                val networkCaps = connectivityManager.getNetworkCapabilities(network)
                if (networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    // WifiInfo has moved to the ConnectivityManager and requires API level 29
                    // see - https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()
                    val wifiInfo = wifiManager.connectionInfo
                    //we remove the extra quotes because for some reason the system puts the SSID in quotes
                    return XyoSystemInfoNetworkWifi(
                        getIpAddress(),
                        wifiInfo.macAddress,
                        wifiInfo.rssi,
                        wifiInfo.ssid.replace("\"", "")
                    )
                }
            }
            return null
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