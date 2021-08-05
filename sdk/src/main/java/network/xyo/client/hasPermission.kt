package network.xyo.client.hasPermission

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

fun hasPermission(context: Context, permission: String, message: String? = null): Boolean {
    val result = ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
    if (!result && message != null) {
        Log.w("Missing Permission", message)
    }
    return result
}