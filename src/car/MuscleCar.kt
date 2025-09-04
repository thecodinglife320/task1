package car

class MuscleCar : Car(5.0) {
    override fun getMakeEngineSound() = {
        when {
            speed < 10.0 -> println("Vrooooom")
            speed < 20.0 -> println("Vrooooooooom")
            else -> println("Vrooooooooooooooooooom!")
        }
    }
}