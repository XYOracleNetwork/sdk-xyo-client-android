package network.xyo.client.payload

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonClass
import network.xyo.client.XyoSerializable
import org.json.JSONObject
import java.security.MessageDigest

open class XyoException(message: String): Throwable(message)
open class XyoValidationException(message: String): XyoException(message)
open class XyoInvalidSchemaException(val schema: String): XyoValidationException("'schema' must be lowercase [${schema}]")
open class XyoInvalidPreviousHashException(val previousHash: String?): XyoValidationException("'previous_hash' must be lowercase [${previousHash}]")

@JsonClass(generateAdapter = true)
@Deprecated("Use Payload instead")
open class XyoPayload(schema: String, previousHash: String? = null): XyoSerializable() {
    var schema = schema.lowercase()
    var previousHash = previousHash?.lowercase()
    @Throws(XyoValidationException::class)
    open fun validate() {
        if (schema != schema.lowercase()) {
            throw XyoInvalidSchemaException(schema)
        }
        if (previousHash != previousHash?.lowercase()) {
            throw XyoInvalidPreviousHashException(previousHash)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
open class Payload(schema: String, fields: Map<String, Any>): JSONObject(fields) {
    init {
        this.put("schema", schema)
    }

    fun sorted(): JSONObject {
        return sort(this)
    }

    fun hash(): String {
        return sha256String(this.sorted().toString())
    }

    companion object {
        fun sort(obj: JSONObject): JSONObject {
            val keys = obj.keys().asSequence().sorted().toList().toTypedArray()
            return JSONObject(obj, keys)
        }

        fun sha256(value: JSONObject): ByteArray {
            return sha256(sort(value).toString())
        }
        fun sha256(value: String): ByteArray {
            val md = MessageDigest.getInstance("SHA256")
            md.update(value.encodeToByteArray())
            return md.digest()
        }

        fun sha256String(value: JSONObject): String {
            return sha256String(sort(value).toString())
        }

        fun sha256String(value: String): String {
            val shaBytes = XyoSerializable.sha256(value)
            return XyoSerializable.bytesToHex(shaBytes)
        }
    }
}