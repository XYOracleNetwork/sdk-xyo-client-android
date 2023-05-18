package network.xyo.boundwitness

class BoundWitnessBuilder: AbstractBoundWitnessBuilder<IBoundWitness, BoundWitnessBuilder>() {
    override fun build(): IBoundWitness {
        return JSONBoundWitness()
    }
}