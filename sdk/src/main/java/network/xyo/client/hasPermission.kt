package network.xyo.client

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

fun hasPermission(context: Context, permission: String, message: String? = null): Boolean {
    val result = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
    if (!result && message != null) {
        Log.e("xyoClientSdk", "Missing Permission: ${message}")
    }
    return result
}