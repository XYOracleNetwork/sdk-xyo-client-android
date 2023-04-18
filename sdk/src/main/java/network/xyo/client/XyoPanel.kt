package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val apiResults: List<PostQueryResult>)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(val context: Context, val nodes: List<NodeClient>, private val witnesses: List<XyoWitness<XyoPayload>>?) {
    var previousHash: String? = null

    constructor(
        context: Context,
        nodeUrl: String,
        accountToUse: XyoAccount,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ) :
            this(
                context,
                listOf(
                    NodeClient(nodeUrl, accountToUse)
                ),
                witnesses
            )



    constructor(
        context: Context,
        nodes: List<NodeClient>,
        observe: ((context: Context, previousHash: String?) -> XyoEventPayload?)?,
    ):this(
        context,
        nodes,
        listOf(XyoWitness(observe)),
    )

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun event(event: String) {
        xyoScope.launch {
            this@XyoPanel.eventAsync(event)
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsync(event: String): XyoPanelReportResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                _, previousHash -> XyoEventPayload(event, previousHash)
            })
        )
        return this.reportAsync(adhocWitnessList)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun report(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()) {
        xyoScope.launch {
            reportAsync(adhocWitnesses)
        }
    }

    private fun generatePayloads(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): List<XyoPayload> {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }
        return payloads.mapNotNull { payload -> payload }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsync(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportResult {
        val payloads = generatePayloads(adhocWitnesses)
        val results = mutableListOf<PostQueryResult>()
        nodes.forEach { node ->
            val archivist = ArchivistWrapper(node)
            val queryResult = archivist.insert(payloads, previousHash)
            results.add(queryResult)
        }
        return XyoPanelReportResult(results)
    }
}