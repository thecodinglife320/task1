package basic

fun main() {
    var i = 1
    while (i <= 5) {
        println("While loop: $i")
        i++
    }

    var j = 1
    do {
        println("Do-while loop: $j")
        j++
    } while (j <= 5)
}