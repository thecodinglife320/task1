package car

open class Car(private val acceleration: Double) {

    protected var speed = 0.0
        private set

    protected open fun getMakeEngineSound(): () -> Unit = { println("vrrrrr...") }

    fun getAccelerate() = {
        speed += acceleration
        getMakeEngineSound()()
    }
}

