package network.xyo.client.archivist.api

import network.xyo.client.XyoApiConfig

class XyoArchivistApiConfig(val archive: String, apiDomain: String, token: String? = null): XyoApiConfig(apiDomain, token)