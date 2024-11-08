package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationActivity : AppCompatActivity() {
    private var requestingLocationUpdates: Boolean = false
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(null)

        requestingLocationUpdates = true
        locationCallback = object : LocationCallback() {
            fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.i("xyoClient", "lat: ${location.latitude}, long: ${location.longitude}")
                }
            }
        }
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
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