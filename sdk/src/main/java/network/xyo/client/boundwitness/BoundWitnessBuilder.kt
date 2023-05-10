package network.xyo.client.boundwitness

class BoundWitnessBuilder: AbstractBoundWitnessBuilder<BoundWitness, BoundWitnessBuilder>() {
    override fun createInstance(): BoundWitness {
        return BoundWitness()
    }
}