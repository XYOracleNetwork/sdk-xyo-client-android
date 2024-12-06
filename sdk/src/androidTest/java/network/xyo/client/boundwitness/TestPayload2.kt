import network.xyo.client.payload.Payload

class TestPayload2SubObject {
    var number_value = 2
    var string_value = "yo"
    var optional_field: String? = null
}

class TestPayload2: Payload("network.xyo.test") {
    var timestamp = 1_618_603_439_107
    var object_field = TestPayload2SubObject()
    var string_field = "there"
    var number_field = 1
}

val testPayload2 = TestPayload2()
val testPayload2Hash: String = "c915c56dd93b5e0db509d1a63ca540cfb211e11f03039b05e19712267bb8b6db"
