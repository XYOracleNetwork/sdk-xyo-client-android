package network.xyo.client

import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig

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

    suspend fun event(event: String): List<PostBoundWitnessesResult> {
        val adhocWitnessList = listOf(
            XyoWitness(
                {
                        previousHash -> XyoEventPayload(event, previousHash)
                }
            )
        )
        return this.report(adhocWitnessList)
    }

    suspend fun report(adhocWitnesses: List<XyoWitness<XyoPayload>> = emptyList()): List<PostBoundWitnessesResult> {
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
            val result = archivist.postBoundWitnessAsync(bw)
            if (result.errors != null) {
                results = results.plus(result)
            }
        }
        return results
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