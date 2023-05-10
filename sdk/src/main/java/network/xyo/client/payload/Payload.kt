package network.xyo.client.payload

import network.xyo.client.XyoSerializable
import org.json.JSONObject
import java.security.MessageDigest

open class Payload(schema: String): JSONObject(mapOf(Pair("schema", schema))) {
    constructor(schema: String, fields: Map<String, Any?>?): this(schema) {
        this.merge(fields)
    }

    constructor(schema: String, fields: JSONObject?): this(schema) {
        this.merge(fields)
    }

    fun merge(fields: JSONObject?) {
        fields?.keys()?.forEach { key -> this.put(key, fields.get(key)) }
    }

    fun merge(fields: Map<String, Any?>?) {
        if (fields != null) {
            this.merge(JSONObject(fields))
        }
    }

    fun sorted(): JSONObject {
        return sort(this)
    }

    fun hash(): String {
        return sha256String(this.sorted().toString())
    }

    val schema: String
        get() {
            return this.getString("schema")
        }

    fun getArrayAsStringList(name: String): List<String> {
        val list = mutableListOf<String>()
        val jsonArray = this.getJSONArray(name)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    fun getArrayAsStringSet(name: String): Set<String> {
        return this.getArrayAsStringList(name).toSet()
    }

    companion object {

        fun fromJson(json: String): Payload {
            val fields = JSONObject(json)
            return Payload(fields.getString("schema"), fields)
        }

        fun fromJson(json: JSONObject): Payload {
            return Payload(json.getString("schema"), json)
        }
        fun sort(obj: JSONObject): JSONObject {
            val keys = obj.keys().asSequence().sorted().toList().toTypedArray()
            val newObj = JSONObject()
            keys.forEach { key ->
                val objField = obj.optJSONObject(key)
                if (objField != null) {
                    newObj.put(key, sort(objField))
                } else {
                    newObj.put(key, obj.get(key))
                }
            }
            return newObj
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

open class PayloadException(message: String): Throwable(message)
open class PayloadValidationException(message: String): PayloadException(message)