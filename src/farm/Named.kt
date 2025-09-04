package farm

interface Named {
    val name: String
}

abstract class Nickname : Named {
    abstract override val name: String
}