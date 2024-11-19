package network.xyo.client

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)
data class XyoPanelReportQueryResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostQueryResult>?, val payloads: List<XyoPayload>?)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(
    val context: Context,
    val account: AccountInstance,
    private val archivists: List<XyoArchivistApiClient>?,
    private val witnesses: List<XyoWitness<XyoPayload>>?,
    private val nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>?
) {
    private var nodes: MutableList<NodeClient>? = null
    var defaultAccount: AccountInstance? = null

    @Deprecated("use constructors without deprecated archive field")
    constructor(
        context: Context,
        account: AccountInstance,
        archive: String? = null,
        apiDomain: String? = null,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ) :
            this(
                context,
                account,
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
        account: AccountInstance,
        // ArrayList to not cause compiler confusion with other class constructor signatures
        nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ): this(
            context,
            account,
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
        account: AccountInstance,
        observe: ((context: Context) -> List<XyoEventPayload>?)?
    ): this(
        context,
        account,
        arrayListOf(Pair("$DefaultApiDomain/Archivist", Account.random())),
        listOf(XyoWitness(observe)),
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

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun event() {
        xyoScope.launch {
            this@XyoPanel.eventAsync()
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsync(): XyoPanelReportResult {
        return this.reportAsync()
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsyncQuery(event: String): XyoPanelReportQueryResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                    _, -> listOf(XyoEventPayload(event))
            })
        )
        return reportAsyncQuery(adhocWitnessList)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun report() {
        xyoScope.launch {
            reportAsync()
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun reportQuery(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()) {
        xyoScope.launch {
            reportAsyncQuery(adhocWitnesses)
        }
    }

    private suspend fun generateBoundWitnessJson(): XyoBoundWitnessJson {
        val payloads = generatePayloads()
        return XyoBoundWitnessBuilder(context)
            .payloads(payloads)
            .signer(account)
            .build()
    }


    private suspend fun generatePayloads(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): List<XyoPayload> {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }

        return payloads.mapNotNull { payload -> payload }.flatten()
    }

    @Deprecated("use reportAsyncQuery instead")
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsync(): XyoPanelReportResult {
        val bw = generateBoundWitnessJson()
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
            val queryResult = archivist.insert(payloadsWithBoundWitness)
            results.add(queryResult)
        }
        return XyoPanelReportQueryResult(bw, results, payloads)
    }

    companion object {
        const val DefaultApiDomain = "https://api.archivist.xyo.network"
        const val DefaultArchive = "temp"
    }
}