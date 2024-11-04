package network.xyo.client.module

import network.xyo.client.boundwitness.XyoBoundWitnessJson

typealias ModuleQueryResult<T> = Triple<XyoBoundWitnessJson, List<T>, List<Exception>>