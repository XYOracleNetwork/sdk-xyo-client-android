package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"

class LocationActivity : Activity() {
    private var requestingLocationUpdates: Boolean = false
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestingLocationUpdates = true
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // No action necessary but we need to start the activity to get location results
                 Log.i("xyoClient", "LocationActivity: locationCallback fired successfully")
            }
        }
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    // Already checking in class
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (LocationPermissions.check((this))) {
            val locationRequest: LocationRequest = LocationRequest.Builder(5000).build()
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
            return
        }
    }
}