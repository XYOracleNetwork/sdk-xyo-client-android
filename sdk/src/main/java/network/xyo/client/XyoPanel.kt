package network.xyo.client

import android.content.Context
import android.os.Build
import android.provider.Settings.Panel
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import com.network.xyo.client.data.PrefsDataStoreProtos.PrefsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.datastore.DATA_STORE_FILE_NAME
import network.xyo.client.datastore.PrefsRepository
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)
data class XyoPanelReportQueryResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostQueryResult>)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(val context: Context, private val archivists: List<XyoArchivistApiClient>, private val witnesses: List<XyoWitness<XyoPayload>>?) {
    var previousHash: String? = null
    private var nodes: MutableList<NodeClient>? = null

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
                witnesses
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
            witnesses
        )
    {
        if (nodeUrlsAndAccounts.isNotEmpty()) {
            nodes = mutableListOf<NodeClient>().let {
                resolveDefaultAccount(context).map { defaultAccount ->
                    nodeUrlsAndAccounts.forEach(){ pair ->
                        val nodeUrl = pair.first
                        val account = pair.second ?: defaultAccount
                        it.add(NodeClient(nodeUrl, account))
                    }
                }
                it
            }
        }
    }

    constructor(
        context: Context,
        observe: ((context: Context, previousHash: String?) -> XyoEventPayload?)?
    ): this(
        context,
        arrayListOf(Pair("$DefaultApiDomain/Archivist", XyoAccount())),
        listOf(XyoWitness(observe))
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
    suspend fun eventAsyncQuery(event: String): XyoPanelReportQueryResult {
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
    suspend fun reportAsync(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportResult {
        val bw = generateBoundWitnessJson(adhocWitnesses)
        previousHash = bw._hash
        val results = mutableListOf<PostBoundWitnessesResult>()
        archivists.forEach { archivist ->
            results.add(archivist.postBoundWitnessAsync(bw))
        }
        return XyoPanelReportResult(bw, results)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun reportAsyncQuery(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportQueryResult {
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
        return XyoPanelReportQueryResult(bw, results)
    }

    companion object {
        const val DefaultApiDomain = "https://api.archivist.xyo.network"
        const val DefaultArchive = "temp"

        fun resolveDefaultAccount(context: Context): Flow<XyoAccount> {
            val prefsRepository = PrefsRepository(context)
            return prefsRepository.getAccountKey().map { account ->
                if (account.isNotEmpty()) {
                    val accountBytes = account.encodeToByteArray()
                     XyoAccount(accountBytes)

                } else {
                    val newAccount = XyoAccount()
                    prefsRepository.setAccountKey(newAccount.private.hex)
                    newAccount
                }
            }
        }
    }
}