fun main() {
    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
}

private fun part1(input: List<String>): String {
    val sum = input.sumOf(String::SNAFUToInt)

    return sum.toSNAFU()
}

private fun String.SNAFUToInt(): Long {
    var result = 0L
    var pow = 1L

    for (i in this.lastIndex downTo 0) {
        result += when (this[i]) {
            '-' -> -pow
            '=' -> -2L * pow
            else -> this[i].digitToInt() * pow
        }

        pow *= 5L
    }

    return result
}

private fun Long.toSNAFU(): String {
    var tmp = this
    var result = ""

    while (tmp != 0L) {
        val rem = tmp % 5L
        tmp /= 5L

        if (rem <= 2) {
            result = rem.toString() + result
        } else {
            ++tmp

            result = when (rem) {
                3L -> "="
                4L -> "-"
                else -> error(Unit)
            } + result
        }
    }

    return result
}
