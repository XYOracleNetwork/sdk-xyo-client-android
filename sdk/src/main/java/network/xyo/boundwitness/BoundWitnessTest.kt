package network.xyo.boundwitness

import org.junit.Test

class BoundWitnessTest {
    /*fun generateQuery(nodeUrl: String): RequestDependencies {
        val account = Account()
        val client = NodeClient(nodeUrl, account)
        val query = XyoPayload("network.xyo.query.module.discover")
        val payloads = mutableListOf<XyoPayload>()
        payloads.add(XyoTestPayload1())
        return RequestDependencies(client, query, payloads)
    }*/

    /*fun testSendQueryBW(nodeUrl: String) {
        runBlocking {
            val(client, query, payloads) = generateQuery(nodeUrl)
            val postResult = client.query(query, payloads, null)
            assertEquals(null, postResult.errors)
        }
    }*/

    @Test
    fun testSendQueryBWSendLocal() {
        //testSendQueryBW(apiDomainLocal)
    }

    @Test
    fun testSendQueryBWSendBeta() {
        //testSendQueryBW(apiDomainBeta)
    }
}