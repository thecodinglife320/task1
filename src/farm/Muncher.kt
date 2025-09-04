package farm

class Muncher(private val food: String) : Eater {
    override val eat =
        {
            println("Eating $food - munch, munch!")
        }

}