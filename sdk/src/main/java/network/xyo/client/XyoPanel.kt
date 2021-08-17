package network.xyo.client

import android.content.Context
import kotlinx.coroutines.launch
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)

class XyoPanel(val context: Context, val archivists: List<XyoArchivistApiClient>, val witnesses: List<XyoWitness<XyoPayload>>?) {
    constructor(
        context: Context,
        archive: String? = null,
        apiDomain: String? = null,
        witnesses: List<XyoWitness<XyoPayload>>? = null,
        token: String? = null
    ) :
        this(
            context,
            listOf(
                XyoArchivistApiClient.get(
                    XyoArchivistApiConfig(
                        archive ?: DefaultApiArchive,
                        apiDomain ?: DefaultApiDomain
                    )
                )
            ),
            witnesses
        )

    var previousHash: String? = null

    constructor(
        context: Context,
        observe: ((context: Context, previousHash: String?) -> XyoEventPayload?)?
    ):this(
        context,
        emptyList<XyoArchivistApiClient>(),
        listOf(XyoWitness(observe)))

    fun event(event: String) {
        xyoScope.launch {
            this@XyoPanel.eventAsync(event)
        }
    }

    suspend fun eventAsync(event: String): XyoPanelReportResult {
        val adhocWitnessList = listOf(
            XyoWitness({
                context, previousHash -> XyoEventPayload(event, previousHash)
            })
        )
        return this.reportAsync(adhocWitnessList)
    }

    fun report(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()) {
        xyoScope.launch {
            reportAsync(adhocWitnesses)
        }
    }

    fun generateBoundWitnessJson(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoBoundWitnessJson {
        val witnesses: List<XyoWitness<XyoPayload>> = (this.witnesses ?: emptyList()).plus(adhocWitnesses)
        val payloads = witnesses.map { witness ->
            witness.observe(context)
        }
        return XyoBoundWitnessBuilder()
            .payloads(payloads.mapNotNull { payload -> payload })
            .witnesses(witnesses)
            .build(previousHash)
    }

    suspend fun reportAsync(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportResult {
        val bw = generateBoundWitnessJson(adhocWitnesses)
        previousHash = bw._hash
        val results = mutableListOf<PostBoundWitnessesResult>()
        archivists.forEach { archivist ->
            results.add(archivist.postBoundWitnessAsync(bw))
        }
        return XyoPanelReportResult(bw, results)
    }

    companion object {
        const val DefaultApiArchive = "default"
        const val DefaultApiDomain = "https://archivist.xyo.network"
        val defaultArchivist: XyoArchivistApiClient
            get() {
                val apiConfig = XyoArchivistApiConfig(this.DefaultApiArchive, this.DefaultApiDomain)
                return XyoArchivistApiClient.get(apiConfig)
            }
    }
}