package network.xyo.account

open class MemoryData(size: Int, sourceBytes: ByteArray): XyoData(size) {
    private val _bytes = sourceBytes.copyInto(ByteArray(_size), sourceBytes.size - _size)

    init {
        checkSize()
    }

    override val bytes: ByteArray
        get() {
            return _bytes
        }
}