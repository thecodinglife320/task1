package farm

fun main() {
    val farmer = Farmer("Old McDonald")
    val cow = Cow("Bessie")
    val pig = Pig("Porky", 2)
    val chicken = Chicken("Cluck", 3)
    val snail = Snail("Shelly")

    val animals = listOf(
        cow, pig, chicken, snail
    )

    animals.forEach {
        farmer.greet(it)
    }

    animals.forEach {
        if (it is Eater) {
            print(it.name)
            it.eat()
        }
    }
}