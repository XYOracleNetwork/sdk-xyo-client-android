package network.xyo.payload

import org.json.JSONObject

interface IPayload {
    val schema: String
    fun toJSON(): JSONObject
    fun hash(): String
}