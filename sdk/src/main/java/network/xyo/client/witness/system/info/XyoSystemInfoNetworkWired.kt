package network.xyo.client.witness.system.info

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.squareup.moshi.JsonClass
import java.net.NetworkInterface

@JsonClass(generateAdapter = true)
class XyoSystemInfoNetworkWired(
    val ip: String?
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoNetworkWired? {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= 23) {
                val network = connectivityManager.activeNetwork
                val networkCaps = connectivityManager.getNetworkCapabilities(network)
                if (networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true) {
                    return XyoSystemInfoNetworkWired(getIpAddress())
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