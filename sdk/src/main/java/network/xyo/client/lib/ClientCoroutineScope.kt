package network.xyo.client.lib

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ClientCoroutineScope : CoroutineScope {

    private var parentJob: CompletableJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    fun onStart() {
        parentJob = SupervisorJob()
    }

    fun onStop() {
        parentJob.cancel()
        // You can also cancel the whole scope with `cancel(cause: CancellationException)`
    }
}


