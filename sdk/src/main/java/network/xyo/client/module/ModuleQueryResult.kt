package network.xyo.client.module

import network.xyo.client.boundwitness.BoundWitness

typealias ModuleQueryResult<T> = Triple<BoundWitness, List<T>, List<Exception>>