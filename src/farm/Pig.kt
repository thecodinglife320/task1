package farm

class Pig(
    override val name: String,
    val excitementLevel: Int,
) : FarmAnimal, Eater by Scarfer("corn") {
    override val speak = {
        repeat(excitementLevel) {
            println("Oink!")
        }
    }
}

