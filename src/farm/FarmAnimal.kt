package farm

interface FarmAnimal : Named, Speaker {
    override val name: String
        get() = "farm animal's name"
    override val speak: () -> Unit
        get() = { println("...") }
}