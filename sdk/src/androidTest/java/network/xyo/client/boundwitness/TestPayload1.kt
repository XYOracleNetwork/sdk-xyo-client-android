import network.xyo.client.payload.Payload

class TestPayload1SubObject {
    var number_value = 2
    var string_value = "yo"
}

class TestPayload1: Payload("network.xyo.test") {
    var timestamp = 1_618_603_439_107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

val testPayload1 = TestPayload1()
val testPayload1Hash: String = "c915c56dd93b5e0db509d1a63ca540cfb211e11f03039b05e19712267bb8b6db"
