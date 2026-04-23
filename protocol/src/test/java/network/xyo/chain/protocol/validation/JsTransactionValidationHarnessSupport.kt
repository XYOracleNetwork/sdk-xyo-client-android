package network.xyo.chain.protocol.validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.chain.protocol.rpc.schema.rpcMoshi
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedTransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesHex
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.payload.PayloadBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path

internal object JsTransactionValidationHarnessSupport {
    private val repoRoot = Path.of("").toAbsolutePath().let { cwd ->
        if (cwd.fileName.toString() == "protocol") cwd.parent else cwd
    }

    private val moshi = rpcMoshi.newBuilder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val boundWitnessAdapter = moshi.adapter(SignedTransactionBoundWitness::class.java)

    fun defaultXl1Repo(): Path = Path.of(
        System.getenv("XL1_PROTOCOL_REPO")
            ?: "/Users/arietrouw/GitHub/XYOracleNetwork/xl1-protocol",
    )

    fun runValidation(
        transaction: SignedHydratedTransaction,
        xl1Repo: Path = defaultXl1Repo(),
    ): NodeValidationResult {
        val payload = buildHarnessInput(transaction)
        val inputFile = Files.createTempFile("xl1-transaction-validation", ".json")
        try {
            Files.write(inputFile, payload.toString().toByteArray())

            val process = ProcessBuilder(
                "node",
                repoRoot.resolve("scripts/validate-transaction-with-xl1.mjs").toString(),
                inputFile.toString(),
            )
                .directory(repoRoot.toFile())
                .redirectErrorStream(true)
                .apply {
                    environment()["XL1_PROTOCOL_REPO"] = xl1Repo.toString()
                }
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            check(exitCode == 0) { "Node validator exited with $exitCode:\n$output" }

            val parsed = JSONObject(output)
            val errors = parsed.optJSONArray("errors")
                ?.let { jsonErrors ->
                    (0 until jsonErrors.length()).map { index ->
                        jsonErrors.getJSONObject(index).getString("message")
                    }
                }
                ?: emptyList()

            return NodeValidationResult(errors)
        } finally {
            Files.deleteIfExists(inputFile)
        }
    }

    private fun buildHarnessInput(transaction: SignedHydratedTransaction): JSONObject {
        val root = JSONObject()
        root.put("context", JSONObject().put("chainId", transaction.boundWitness.chain))

        val txTuple = JSONArray()
        txTuple.put(boundWitnessJson(transaction.boundWitness))

        val payloads = JSONArray()
        transaction.payloads.forEach { payload ->
            payloads.put(JSONObject(PayloadBuilder.addHashMeta(payload as network.xyo.client.payload.Payload)))
        }
        txTuple.put(payloads)

        root.put("transaction", txTuple)
        return root
    }

    private fun boundWitnessJson(boundWitness: SignedTransactionBoundWitness): JSONObject {
        val json = JSONObject(boundWitnessAdapter.toJson(boundWitness))
        json.put("_hash", transactionHash(boundWitness))
        json.put("_dataHash", transactionDataHash(boundWitness))
        return json
    }

    private fun transactionHash(boundWitness: SignedTransactionBoundWitness): String {
        return JsonSerializable.sha256String(
            HashableSignedTransactionBoundWitness(
                from = boundWitness.from,
                chain = boundWitness.chain,
                nbf = boundWitness.nbf,
                exp = boundWitness.exp,
                fees = boundWitness.fees,
                script = boundWitness.script,
                addresses = boundWitness.addresses,
                payload_hashes = boundWitness.payload_hashes,
                payload_schemas = boundWitness.payload_schemas,
                previous_hashes = boundWitness.previous_hashes,
                schema = boundWitness.schema,
                timestamp = boundWitness.timestamp,
                signatures = boundWitness.signatures,
            ),
            JsonSerializable.Companion.MetaExclusion.STORAGE_META,
        )
    }

    private fun transactionDataHash(boundWitness: SignedTransactionBoundWitness): String {
        return JsonSerializable.sha256String(
            HashableSignedTransactionBoundWitness(
                from = boundWitness.from,
                chain = boundWitness.chain,
                nbf = boundWitness.nbf,
                exp = boundWitness.exp,
                fees = boundWitness.fees,
                script = boundWitness.script,
                addresses = boundWitness.addresses,
                payload_hashes = boundWitness.payload_hashes,
                payload_schemas = boundWitness.payload_schemas,
                previous_hashes = boundWitness.previous_hashes,
                schema = boundWitness.schema,
                timestamp = boundWitness.timestamp,
                signatures = boundWitness.signatures,
            ),
            JsonSerializable.Companion.MetaExclusion.ALL_META,
        )
    }
}

internal data class NodeValidationResult(
    val errors: List<String>,
)

@JsonClass(generateAdapter = true)
data class HashableSignedTransactionBoundWitness(
    val from: String,
    val chain: String,
    val nbf: Long,
    val exp: Long,
    val fees: TransactionFeesHex,
    val script: List<String>? = null,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String> = emptyList(),
) : JsonSerializable()
