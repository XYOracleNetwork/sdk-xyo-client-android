import network.xyo.client.payload.Payload

class IdPayload(val salt: String): Payload("network.xyo.id") {
    constructor(salt: UInt) : this("${salt}")
}

data class BoundWitnessSequenceTestCase(
    val mnemonics: List<String>,
    val paths: List<String>,
    val addresses: List<String>,
    val payloads: List<Payload>,
    val payloadHashes: List<String>,
    val previousHashes: List<String?>,
    val dataHash: String
)

// Define the PayloadsWithHashes data class
data class PayloadsWithHashes(
    val payloads: List<Payload>,
    val payloadHashes: List<String>
)

// Initialize payload sequences
val payloadSequences: List<PayloadsWithHashes> = listOf(
    PayloadsWithHashes(
        payloads = listOf(IdPayload(0u)),
        payloadHashes = listOf(
            "ada56ff753c0c9b2ce5e1f823eda9ac53501db2843d8883d6cf6869c18ef7f65"
        )
    ),
    PayloadsWithHashes(
        payloads = listOf(IdPayload(1u)),
        payloadHashes = listOf(
            "3a3b8deca568ff820b0b7c8714fbdf82b40fb54f4b15aca8745e06b81291558e"
        )
    ),
    PayloadsWithHashes(
        payloads = listOf(IdPayload(2u), IdPayload(3u)),
        payloadHashes = listOf(
            "1a40207fab71fc184e88557d5bee6196cbbb49f11f73cda85000555a628a8f0a",
            "c4bce9b4d3239fcc9a248251d1bef1ba7677e3c0c2c43ce909a6668885b519e6"
        )
    ),
    PayloadsWithHashes(
        payloads = listOf(IdPayload(4u), IdPayload(5u)),
        payloadHashes = listOf(
            "59c0374dd801ae64ddddba27320ca028d7bd4b3d460f6674c7da1b4aa9c956d6",
            "5d9b8e84bc824280fcbb6290904c2edbb401d626ad9789717c0a23d1cab937b0"
        )
    )
)

// Define wallet mnemonics, paths, and addresses
const val wallet1Mnemonic =
    "report door cry include salad horn recipe luxury access pledge husband maple busy double olive"
const val wallet1Path = "m/44'/60'/0'/0/0"
const val wallet1Address = "25524Ca99764D76CA27604Bb9727f6e2f27C4533"

const val wallet2Mnemonic =
    "turn you orphan sauce act patient village entire lava transfer height sense enroll quit idle"
const val wallet2Path = "m/44'/60'/0'/0/0"
const val wallet2Address = "FdCeD2c3549289049BeBf743fB721Df211633fBF"

// Initialize BoundWitnessSequenceTestCase instances
val boundWitnessSequenceTestCase1 = BoundWitnessSequenceTestCase(
    mnemonics = listOf(wallet1Mnemonic),
    paths = listOf(wallet1Path),
    addresses = listOf(wallet1Address),
    payloads = payloadSequences[0].payloads,
    payloadHashes = payloadSequences[0].payloadHashes,
    previousHashes = listOf(null),
    dataHash = "750113b9826ba94b622667b06cd8467f1330837581c28907c16160fec20d0a4b"
)

val boundWitnessSequenceTestCase2 = BoundWitnessSequenceTestCase(
    mnemonics = listOf(wallet2Mnemonic),
    paths = listOf(wallet2Path),
    addresses = listOf(wallet2Address),
    payloads = payloadSequences[1].payloads,
    payloadHashes = payloadSequences[1].payloadHashes,
    previousHashes = listOf(null),
    dataHash = "bacd010d79126a154339e59c11c5b46be032c3bef65626f83bcafe968dc6dd1b"
)

val boundWitnessSequenceTestCase3 = BoundWitnessSequenceTestCase(
    mnemonics = listOf(wallet1Mnemonic, wallet2Mnemonic),
    paths = listOf(wallet1Path, wallet2Path),
    addresses = listOf(wallet1Address, wallet2Address),
    payloads = payloadSequences[2].payloads,
    payloadHashes = payloadSequences[2].payloadHashes,
    previousHashes = listOf(
        "750113b9826ba94b622667b06cd8467f1330837581c28907c16160fec20d0a4b",
        "bacd010d79126a154339e59c11c5b46be032c3bef65626f83bcafe968dc6dd1b"
    ),
    dataHash = "73245ef73517913f4b57c12d56d81199968ecd8fbefea9ddc474f43dd6cfa8c8"
)

val boundWitnessSequenceTestCase4 = BoundWitnessSequenceTestCase(
    mnemonics = listOf(wallet1Mnemonic, wallet2Mnemonic),
    paths = listOf(wallet1Path, wallet2Path),
    addresses = listOf(wallet1Address, wallet2Address),
    payloads = payloadSequences[3].payloads,
    payloadHashes = payloadSequences[3].payloadHashes,
    previousHashes = listOf(
        "73245ef73517913f4b57c12d56d81199968ecd8fbefea9ddc474f43dd6cfa8c8",
        "73245ef73517913f4b57c12d56d81199968ecd8fbefea9ddc474f43dd6cfa8c8"
    ),
    dataHash = "210d86ea43d82b85a49b77959a8ee4e6016ff7036254cfa39953befc66073010"
)

// Aggregate all test cases into a list
val boundWitnessSequenceTestCases = listOf(
    boundWitnessSequenceTestCase1,
    boundWitnessSequenceTestCase2,
    boundWitnessSequenceTestCase3,
    boundWitnessSequenceTestCase4
)