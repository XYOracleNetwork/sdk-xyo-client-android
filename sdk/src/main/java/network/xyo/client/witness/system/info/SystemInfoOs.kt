package network.xyo.client.witness.system.info

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import org.json.JSONObject

data class SystemInfoOs (
    val base_os: String?,
    val codename: String?,
    val incremental: String?,
    val preview_sdk_int: Int?,
    val release: String?,
    val sdk_int: Int?,
    val security_patch: String?
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.M)
        fun detect(): JSONObject {
            val gson = Gson()
            return JSONObject(gson.toJson(SystemInfoOs(
                Build.VERSION.BASE_OS,
                Build.VERSION.CODENAME,
                Build.VERSION.INCREMENTAL,
                Build.VERSION.PREVIEW_SDK_INT,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.VERSION.SECURITY_PATCH
            )))
        }
    }
}