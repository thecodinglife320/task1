package basic

fun main() {
    print("Enter a number between 1 and 10: ")
    val number = when (readln()) {
        "1" -> 1
        "2" -> 2
        "3" -> 3
        "4" -> 4
        "5" -> 5
        "6" -> 6
        "7" -> 7
        "8" -> 8
        "9" -> 9
        "10" -> 10
        else -> throw Exception("Invalid number")
    }
    println(number)
}