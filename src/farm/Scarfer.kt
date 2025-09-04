package farm

class Scarfer(private val food: String) : Eater {
    override val eat = { println("Scarfing down $food - NOM NOM NOM!!!") }
}