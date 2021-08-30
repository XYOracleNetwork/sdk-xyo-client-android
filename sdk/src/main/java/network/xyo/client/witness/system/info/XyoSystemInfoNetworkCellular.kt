package network.xyo.client.witness.system.info

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import com.squareup.moshi.JsonClass
import network.xyo.client.hasPermission
import java.net.NetworkInterface

@JsonClass(generateAdapter = true)
data class XyoSystemInfoNetworkCellularProvider(
    val name: String?,
)

@JsonClass(generateAdapter = true)
class XyoSystemInfoNetworkCellular(
    val ip: String?,
    val provider: XyoSystemInfoNetworkCellularProvider?
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoNetworkCellular? {
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
                        val provider = XyoSystemInfoNetworkCellularProvider(
                            telephonyManager.networkOperatorName
                        )

                        return XyoSystemInfoNetworkCellular(getIpAddress(), provider)
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