package car

open class Clunker(acceleration: Double) : Car(acceleration) {
    override fun getMakeEngineSound() = {
        println("putt-putt-putt")
    }
}