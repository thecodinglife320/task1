package basic

fun main() {
    println("1. Lặp qua một phạm vi (bao gồm):")
    for (i in 1..5) {
        print("$i ")
    }
    println("\n")

    println("2. Lặp qua một phạm vi (loại trừ):")
    for (i in 1 until 5) {
        print("$i ")
    }
    println("\n")

    println("3. Lặp ngược:")
    for (i in 5 downTo 1) {
        print("$i ")
    }
    println("\n")

    println("4. Lặp với một bước nhảy:")
    for (i in 1..10 step 2) {
        print("$i ")
    }
    println("\n")

    println("5. Lặp ngược với một bước nhảy:")
    for (i in 10 downTo 1 step 2) {
        print("$i ")
    }
    println("\n")

    val items = listOf("táo", "chuối", "cam")
    println("6. Lặp qua một bộ sưu tập:")
    for (item in items) {
        print("$item ")
    }
    println("\n")

    println("7. Lặp qua một bộ sưu tập với chỉ mục:")
    for ((index, item) in items.withIndex()) {
        println("   item tại chỉ mục $index là $item")
    }
    println()

    println("8. Lặp qua một chuỗi:")
    for (char in "Kotlin") {
        print("$char ")
    }
    println("\n")
}
