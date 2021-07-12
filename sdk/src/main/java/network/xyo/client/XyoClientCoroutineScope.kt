package network.xyo.client

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class XyoClientCoroutineScope : CoroutineScope {

    private var parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    fun onStart() {
        parentJob = Job()
    }

    fun onStop() {
        parentJob.cancel()
        // You can also cancel the whole scope with `cancel(cause: CancellationException)`
    }
}

val xyoScope = XyoClientCoroutineScope()
