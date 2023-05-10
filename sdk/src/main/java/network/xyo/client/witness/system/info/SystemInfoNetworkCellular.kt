package network.xyo.client.witness.system.info

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import network.xyo.client.hasPermission
import org.json.JSONObject
import java.net.NetworkInterface

data class SystemInfoNetworkCellularProvider(
    val name: String?,
)

class SystemInfoNetworkCellular(
    val ip: String?,
    val provider: SystemInfoNetworkCellularProvider?
): JSONObject() {
    companion object {
        fun detect(context: Context): SystemInfoNetworkCellular? {
            if (Build.VERSION.SDK_INT >= 23) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                val networkCaps = connectivityManager.getNetworkCapabilities(network)
                if (networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                    val telephonyManager =
                        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    if (hasPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE,
                            "Manifest.permission.READ_PHONE_STATE required"
                        )
                    ) {
                        val provider = SystemInfoNetworkCellularProvider(
                            telephonyManager.networkOperatorName
                        )

                        return SystemInfoNetworkCellular(getIpAddress(), provider)
                    }
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