package network.xyo.client.archivist.api

import network.xyo.client.XyoApiConfig

@Deprecated("Use NodeClient.class instead")
class XyoArchivistApiConfig(val archive: String, apiDomain: String, token: String? = null): XyoApiConfig(apiDomain, token)