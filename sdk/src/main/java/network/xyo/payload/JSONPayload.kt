package network.xyo.payload

import network.xyo.Bytes
import org.json.JSONObject
import java.security.InvalidParameterException
import java.security.MessageDigest
import kotlin.experimental.or

open class JSONPayload(schema: String): JSONObject(mapOf(Pair("schema", schema))), IPayload {
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

    override fun hash(): String {
        return sha256String(this.sorted().toString())
    }

    override val schema: String
        get() {
            return this.getString("schema")
        }

    override fun toJSON(): JSONObject {
        return this
    }

    fun getArrayAsObjectList(name: String) : List<JSONObject> {
        val list = mutableListOf<JSONObject>()
        val jsonArray = this.getJSONArray(name)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getJSONObject(i))
        }
        return list
    }

    fun getArrayAsObjectSet(name: String): Set<JSONObject> {
        return this.getArrayAsObjectList(name).toSet()
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

        fun fromJson(json: String): JSONPayload {
            val fields = JSONObject(json)
            return JSONPayload(fields.getString("schema"), fields)
        }

        fun fromJson(json: JSONObject): JSONPayload {
            return JSONPayload(json.getString("schema"), json)
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
            val shaBytes = sha256(value)
            return Bytes.bytesToHex(shaBytes)
        }
    }
}

open class PayloadException(message: String): Throwable(message)
open class PayloadValidationException(message: String): PayloadException(message)