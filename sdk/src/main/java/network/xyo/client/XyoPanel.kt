package network.xyo.client

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.datastore.XyoAccountPrefsRepository
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)
data class XyoPanelReportQueryResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostQueryResult>?, val payloads: List<XyoPayload>?)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(val context: Context, private val archivists: List<XyoArchivistApiClient>?, private val witnesses: List<XyoWitness<XyoPayload>>?, private val nodeUrlsAndAccounts: ArrayList<Pair<String, XyoAccount?>>?) {
    var previousHash: String? = null
    private var nodes: MutableList<NodeClient>? = null
    var defaultAccount: XyoAccount? = null

    @Deprecated("use constructors without deprecated archive field")
    constructor(
        context: Context,
        archive: String? = null,
        apiDomain: String? = null,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ) :
            this(
                context,
                listOf(
                    XyoArchivistApiClient.get(
                        XyoArchivistApiConfig(
                            archive ?: DefaultArchive,
                            apiDomain ?: DefaultApiDomain
                        )
                    )
                ),
                witnesses,
                null
            )

    constructor(
        context: Context,
        // ArrayList to not cause compiler confusion with other class constructor signatures
        nodeUrlsAndAccounts: ArrayList<Pair<String, XyoAccount?>>,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ): this(
            context,
            listOf(
                XyoArchivistApiClient.get(
                    XyoArchivistApiConfig(
                        DefaultArchive,
                        DefaultApiDomain
                    )
                )
            ),
            witnesses,
            nodeUrlsAndAccounts
        )

    constructor(
        context: Context,
        observe: ((context: Context, previousHash: String?) -> XyoEventPayload?)?
    ): this(
        context,
        arrayListOf(Pair("$DefaultApiDomain/Archivist", XyoAccount())),
        listOf(XyoWitness(observe)),
    )

    suspend fun resolveNodes(resetNodes: Boolean = false) {
        if (resetNodes) nodes = null
        this.defaultAccount = XyoAccountPrefsRepository(context).getAccount()
        if (nodeUrlsAndAccounts?.isNotEmpty() == true) {
            nodes = mutableListOf<NodeClient>().let {
                this@XyoPanel.nodeUrlsAndAccounts?.forEach { pair ->
                    val nodeUrl = pair.first
                    val account = pair.second ?: defaultAccount
                    it.add(NodeClient(nodeUrl, account))
                }
                it
            }
        }
    }

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
                _, previousHash -> XyoEventPayload(event)
            })
        )
        return this.reportAsync(adhocWitnessList)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsyncQuery(event: String): XyoPanelReportQueryResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                    _, previousHash -> XyoEventPayload(event)
            })
        )
        return reportAsyncQuery(adhocWitnessList)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun report(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()) {
        xyoScope.launch {
            reportAsync(adhocWitnesses)
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun reportQuery(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()) {
        xyoScope.launch {
            reportAsyncQuery(adhocWitnesses)
        }
    }

    private suspend fun generateBoundWitnessJson(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoBoundWitnessJson {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = generatePayloads()
        return XyoBoundWitnessBuilder()
            .payloads(payloads)
            .witnesses(witnesses)
            .build(previousHash)
    }


    private suspend fun generatePayloads(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): List<XyoPayload> {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }

        return payloads.mapNotNull { payload -> payload }
    }

    @Deprecated("use reportAsyncQuery instead")
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsync(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportResult {
        val bw = generateBoundWitnessJson(adhocWitnesses)
        previousHash = bw._hash
        val results = mutableListOf<PostBoundWitnessesResult>()
        archivists?.forEach { archivist ->
            results.add(archivist.postBoundWitnessAsync(bw))
        }
        return XyoPanelReportResult(bw, results)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsyncQuery(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportQueryResult {
        if (nodes == null) resolveNodes()
        val bw = generateBoundWitnessJson()
        val payloads = generatePayloads(adhocWitnesses)
        val results = mutableListOf<PostQueryResult>()

        if (nodes.isNullOrEmpty()) {
            Log.e("xyoClient", "No Nodes found, so no payloads will be sent to archivist(s)")
        }

        nodes?.forEach { node ->
            val archivist = ArchivistWrapper(node)
            val payloadsWithBoundWitness = payloads.plus(bw)
            val queryResult = archivist.insert(payloadsWithBoundWitness, previousHash)
            results.add(queryResult)
        }
        return XyoPanelReportQueryResult(bw, results, payloads)
    }

    companion object {
        const val DefaultApiDomain = "https://api.archivist.xyo.network"
        const val DefaultArchive = "temp"
    }
}