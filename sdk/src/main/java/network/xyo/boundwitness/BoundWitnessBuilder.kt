package network.xyo.boundwitness

class BoundWitnessBuilder: AbstractBoundWitnessBuilder<IBoundWitness, BoundWitnessBuilder>() {
    override fun build(): IBoundWitness {
        val bw = JSONBoundWitness()
        this.setFields(bw)
        return bw
    }
}