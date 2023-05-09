package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import network.xyo.client.address.Account
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.module.AbstractModule
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.ModuleResolver
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

data class SentinelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)
data class SentinelReportQueryResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostQueryResult>)


class Sentinel(val context: Context, params: ModuleParams<ModuleConfig>, private val archivists: List<XyoArchivistApiClient>, private val witnesses: List<XyoWitness<XyoPayload>>?): AbstractModule<ModuleConfig, ModuleParams<ModuleConfig>>(params) {
    var previousHash: String? = null
    private var nodes: MutableList<NodeClient>? = null
    override var address: String
        get() {
            return this.params.account.address.hex
        }
        set(value) {}

    override var config: ModuleConfig
        get() {
            return this.params.config
        }
        set(value) {}

    override var queries: List<String>
        get() = TODO("Not yet implemented")
        set(value) {}

    override var downResolver: ModuleResolver = CompositeModuleResolver()

    @Deprecated("use constructors without deprecated archive field")
    constructor(
        context: Context,
        params: ModuleParams<ModuleConfig>,
        archive: String? = null,
        apiDomain: String? = null,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ) :
            this(
                context,
                params,
                listOf(
                    XyoArchivistApiClient.get(
                        XyoArchivistApiConfig(
                            archive ?: DefaultArchive,
                            apiDomain ?: DefaultApiDomain
                        )
                    )
                ),
                witnesses
            )

    constructor(
        context: Context,
        params: ModuleParams<ModuleConfig>,
        // ArrayList to not cause compiler confusion with other class constructor signatures
        nodeUrlsAndAccounts: ArrayList<Pair<String, Account?>>,
        witnesses: List<XyoWitness<XyoPayload>>? = null
    ): this(
            context,
            params,
            listOf(
                XyoArchivistApiClient.get(
                    XyoArchivistApiConfig(
                        DefaultArchive,
                        DefaultApiDomain
                    )
                )
            ),
            witnesses
        )
    {
        if (nodeUrlsAndAccounts.isNotEmpty()) {
            nodes = mutableListOf<NodeClient>().let {
                nodeUrlsAndAccounts.forEach(){ pair ->
                    val nodeUrl = pair.first
                    val account = pair.second
                    it.add(NodeClient(nodeUrl, account ?: Account()))
                }
                it
            }
        }
    }

    constructor(
        context: Context,
        params: ModuleParams<ModuleConfig>,
        observe: ((context: Context, previousHash: String?) -> XyoEventPayload?)?
    ): this(
        context,
        params,
        arrayListOf(Pair("$DefaultApiDomain/Archivist", Account())),
        listOf(XyoWitness(observe))
    )

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun event(event: String) {
        xyoScope.launch {
            this@Sentinel.eventAsync(event)
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsync(event: String): SentinelReportResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                _, previousHash -> XyoEventPayload(event, previousHash)
            })
        )
        return this.reportAsync(adhocWitnessList)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun eventAsyncQuery(event: String): SentinelReportQueryResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                    _, previousHash -> XyoEventPayload(event, previousHash)
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

    private fun generateBoundWitnessJson(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoBoundWitnessJson {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = generatePayloads()
        return XyoBoundWitnessBuilder()
            .payloads(payloads)
            .witnesses(witnesses)
            .build(previousHash)
    }


    private fun generatePayloads(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): List<XyoPayload> {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }
        return payloads.mapNotNull { payload -> payload }
    }

    @Deprecated("use reportAsyncQuery instead")
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsync(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): SentinelReportResult {
        val bw = generateBoundWitnessJson(adhocWitnesses)
        previousHash = bw._hash
        val results = mutableListOf<PostBoundWitnessesResult>()
        archivists.forEach { archivist ->
            results.add(archivist.postBoundWitnessAsync(bw))
        }
        return SentinelReportResult(bw, results)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsyncQuery(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): SentinelReportQueryResult {
        val bw = generateBoundWitnessJson()
        val payloads = generatePayloads(adhocWitnesses)
        val results = mutableListOf<PostQueryResult>()

        if (nodes.isNullOrEmpty()) {
            throw Error("Called reportAsync without first constructing any nodeClients")
        }

        nodes?.forEach { node ->
            val archivist = ArchivistWrapper(node)
            val queryResult = archivist.insert(payloads, previousHash)
            results.add(queryResult)
        }
        return SentinelReportQueryResult(bw, results)
    }

    companion object {
        const val DefaultApiDomain = "https://api.archivist.xyo.network"
        const val DefaultArchive = "temp"
    }
}