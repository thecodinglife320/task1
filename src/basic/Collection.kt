package basic

fun main() {

    listOf(1, 2, 3).also {
        println(it)
    }

    listOf(1, 1, 2, 3).toSet().also {
        println(it)
    }

    mapOf(
        1 to "Một",
        2 to "Hai",
        3 to "Ba",
        4 to "Bốn",
        5 to "Năm",
        6 to "Sáu",
        7 to "Bảy",
        8 to "Tám",
        9 to "Chín",
        10 to "Mười"
    ).also {
        println(it)
    }

    listOf(1, 2, 3).map {
        it + 1
    }.also {
        println(it)
    }

    listOf(1, 2, 3, 4, 5, 6).filter {
        it % 2 == 0
    }.also {
        println(it)
    }

    listOf(1, 2, 3, 4, 5, 6).reduce { acc, i ->
        acc + i
    }.also {
        println(it)
    }

    listOf(1, 2, 3, 4, 5, 6).fold(12) { acc, i ->
        acc + i
    }.also {
        println(it)
    }
}