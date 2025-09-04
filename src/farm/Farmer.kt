package farm

class Farmer(
    override val name: String
) : Named {
    val greet = { animal: FarmAnimal ->
        println("Good morning, ${animal.name}!")
        if (animal is Chicken) println("You have ${animal.numberOfEggs} eggs today!")
        animal.speak()
    }
}