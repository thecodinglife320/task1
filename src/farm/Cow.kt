package farm

class Cow(
    override val name: String,
) : FarmAnimal, Eater by Muncher("grass") {
    override val speak = { println("Moo!") }
}