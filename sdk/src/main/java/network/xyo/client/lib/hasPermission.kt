package network.xyo.client.lib

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

fun hasPermission(context: Context, permission: String, message: String? = null): Boolean {
    val result = ContextCompat.checkSelfPermission(
        context.applicationContext,
        permission
    ) == PackageManager.PERMISSION_GRANTED
    if (!result && message != null) {
        Log.e("xyoClientSdk", "Missing Permission: ${message}")
    }
    return result
}