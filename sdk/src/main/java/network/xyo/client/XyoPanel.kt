package network.xyo.client

import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig

data class XyoPanelReportResult(val bw: XyoBoundWitnessJson, val apiResults: List<PostBoundWitnessesResult>)

class XyoPanel {
    constructor(archivists: List<XyoArchivistApiClient>, witnesses: List<XyoWitness<XyoPayload>>) {
        this._archivists = archivists
        this._witnesses = witnesses
    }

    constructor(archive: String? = null, apiDomain: String? = null, witnesses: List<XyoWitness<XyoPayload>>? = null, token: String? = null) {
        val apiConfig = XyoArchivistApiConfig(archive ?: XyoPanel.DefaultApiArchive, apiDomain ?: XyoPanel.DefaultApiDomain)
        val archivist = XyoArchivistApiClient.get(apiConfig)
        this._archivists = listOf(archivist)
        if (witnesses != null) {
            this._witnesses = witnesses
        }
    }

    constructor(observe: ((previousHash: String?) -> XyoEventPayload?)?) {
        if (observe != null) {
            this._witnesses = listOf(XyoWitness(observe))
        }
    }

    private var _archivists: List<XyoArchivistApiClient> = emptyList()
    private var _witnesses: List<XyoWitness<XyoPayload>> = emptyList()

    suspend fun event(event: String): XyoPanelReportResult {
        val adhocWitnessList = listOf(
            XyoWitness(
                {
                        previousHash -> XyoEventPayload(event, previousHash)
                }
            )
        )
        return this.report(adhocWitnessList)
    }

    suspend fun report(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): XyoPanelReportResult {
        val witnesses = emptyList<XyoWitness<XyoPayload>>().plus(adhocWitnesses).plus(this._witnesses)
        val payloads = witnesses.map { witness ->
                witness.observe()
        }
        val bw = XyoBoundWitnessBuilder()
            .payloads(payloads.mapNotNull { payload -> payload })
            .witnesses(witnesses)
            .build()
        var results = emptyList<PostBoundWitnessesResult>()
        _archivists.forEach { archivist ->
            results = results.plus(archivist.postBoundWitnessAsync(bw))
        }
        return XyoPanelReportResult(bw, results)
    }

    companion object {
        val DefaultApiArchive = "default"
        val DefaultApiDomain = "https://archivist.xyo.network"
        val defaultArchivist: XyoArchivistApiClient
            get() {
                val apiConfig = XyoArchivistApiConfig(this.DefaultApiArchive, this.DefaultApiDomain)
                return XyoArchivistApiClient.get(apiConfig)
            }
    }
}