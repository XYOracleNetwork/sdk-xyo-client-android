package network.xyo.client.witness.system.info

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.net.NetworkInterface

data class XyoSystemInfoNetworkCellularProvider(
    val name: String?,
)

class XyoSystemInfoNetworkCellular(
    val provider: XyoSystemInfoNetworkCellularProvider?,
    val ip: String?
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoNetworkCellular? {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCaps = connectivityManager.getNetworkCapabilities(network)
            if (networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val provider = XyoSystemInfoNetworkCellularProvider(
                        telephonyManager.networkOperatorName
                    )

                    return XyoSystemInfoNetworkCellular(provider, getIpAddress())
                } else {
                    Log.w(XyoSystemInfoNetworkCellular::class.java.name, "Manifest.permission.READ_PHONE_STATE required")
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