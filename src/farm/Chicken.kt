package farm

class Chicken(
    override val name: String,
    var numberOfEggs: Int = 0,
) : FarmAnimal, Eater by Muncher("bugs") {

    override val speak = {
        println("Cuc ta cuc tac")
    }

}

