package network.xyo.client.witness.location.info

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GoogleApiAvailability

class LocationPermissions {
    companion object {
        fun check(context: Context): Boolean {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.e("xyoClient", "ACCESS_FINE_LOCATION permission not allowed")
                return false
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.e("xyoClient", "ACCESS_COARSE_LOCATION permission not allowed")
                return false
            }
            return true
        }

        fun checkGooglePlayServices(context: Context): Boolean {
            val gpInstance = GoogleApiAvailability.getInstance()
            val available = gpInstance.isGooglePlayServicesAvailable(context)
            val googlePlayServicesAvailable = available == 1
            if (googlePlayServicesAvailable) {
                Log.e("xyoClient", "Google Play Service not installed")
                return false
            }
            return true
        }
    }
}