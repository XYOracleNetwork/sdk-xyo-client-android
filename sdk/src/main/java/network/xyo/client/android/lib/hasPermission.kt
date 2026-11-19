package network.xyo.client.android.lib

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

fun hasPermission(context: Context, permission: String, message: String? = null): Boolean {
    val result = context.applicationContext.checkSelfPermission(
        permission
    ) == PackageManager.PERMISSION_GRANTED
    if (!result && message != null) {
        Log.e("xyoClientSdk", "Missing Permission: ${message}")
    }
    return result
}
