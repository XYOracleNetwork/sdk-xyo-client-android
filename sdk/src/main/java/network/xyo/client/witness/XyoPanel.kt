package network.xyo.client.witness

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.boundwitness.BoundWitnessBuilder
import network.xyo.client.boundwitness.BoundWitnessJson
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.Payload

data class XyoPanelReportQueryResult(val bw: BoundWitnessJson, val apiResults: List<PostQueryResult>?, val payloads: List<Payload>?)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(
    val context: Context,
    val account: AccountInstance,
    private val witnesses: List<XyoWitness<Payload>>?,
    private val nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>?
) {
    private var nodes: MutableList<NodeClient>? = null

    constructor(
        context: Context,
        account: AccountInstance,
        nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>,
        witnesses: List<XyoWitness<Payload>>? = null
    ): this(
            context,
            account,
            witnesses,
            nodeUrlsAndAccounts
        )

    constructor(
        context: Context,
        account: AccountInstance,
        observe: ((context: Context) -> List<Payload>?)?
    ): this(
        context,
        account,
        listOf(XyoWitness(observe)),
        null,
    )

    fun resolveNodes(resetNodes: Boolean = false) {
        if (resetNodes) nodes = null
        if (nodeUrlsAndAccounts?.isNotEmpty() == true) {
            nodes = mutableListOf<NodeClient>().let {
                this@XyoPanel.nodeUrlsAndAccounts.forEach { pair ->
                    val nodeUrl = pair.first
                    val account = pair.second
                    it.add(NodeClient(nodeUrl, account, context))
                }
                it
            }
        }
    }

    private suspend fun generateBoundWitnessJson(payloads: List<Payload>): BoundWitnessJson {
        return BoundWitnessBuilder()
            .payloads(payloads)
            .signer(account)
            .build()
    }


    private suspend fun generatePayloads(adhocWitnesses: List<XyoWitness<Payload>> = emptyList()): List<Payload> {
        val witnesses: List<XyoWitness<Payload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }

        return payloads.mapNotNull { payload -> payload }.flatten()
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsyncQuery(adhocWitnesses: List<XyoWitness<Payload>> = emptyList()): XyoPanelReportQueryResult {
        if (nodes == null) resolveNodes()
        val payloads = generatePayloads(adhocWitnesses)
        val bw = generateBoundWitnessJson(payloads)
        val results = mutableListOf<PostQueryResult>()

        if (nodes.isNullOrEmpty()) {
            Log.e("xyoClient", "No Nodes found, so no payloads will be sent to archivist(s)")
        }

        nodes?.forEach { node ->
            val archivist = ArchivistWrapper(node)
            val payloadsWithBoundWitness = payloads.plus(bw)
            val queryResult = archivist.insert(payloadsWithBoundWitness)
            results.add(queryResult)
        }
        return XyoPanelReportQueryResult(bw, results, payloads)
    }
}