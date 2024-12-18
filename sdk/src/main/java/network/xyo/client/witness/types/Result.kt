package network.xyo.client.witness.types

sealed class WitnessResult<out R> {
    data class Success<out T>(val data: T) : WitnessResult<T>()
    data class Error(val exception: MutableList<kotlin.Error>) : WitnessResult<Nothing>()
}