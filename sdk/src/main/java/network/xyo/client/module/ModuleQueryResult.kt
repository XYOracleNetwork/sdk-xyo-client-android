package network.xyo.client.module

import network.xyo.client.boundwitness.BoundWitnessJson

typealias ModuleQueryResult<T> = Triple<BoundWitnessJson, List<T>, List<Exception>>