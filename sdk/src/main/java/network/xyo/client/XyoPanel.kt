package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.payload.XyoPayload

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)

@RequiresApi(Build.VERSION_CODES.M)
class XyoPanel(val context: Context, val archivists: List<XyoArchivistApiClient>, val witnesses: List<XyoWitness<XyoPayload>>?) {
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
        listOf(
            XyoArchivistApiClient.get(
                XyoArchivistApiConfig(
                    DefaultApiArchive,
                    DefaultApiDomain
                )
            )
        ),
        listOf(XyoWitness(observe)))

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

    companion object {
        const val DefaultApiArchive = "temp"
        const val DefaultApiDomain = "https://api.archivist.xyo.network"
        val defaultArchivist: XyoArchivistApiClient
            get() {
                val apiConfig = XyoArchivistApiConfig(this.DefaultApiArchive, this.DefaultApiDomain)
                return XyoArchivistApiClient.get(apiConfig)
            }
    }
}