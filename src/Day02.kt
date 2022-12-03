fun main() {
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.sumOf(String::toScore)
}

private fun part2(input: List<String>): Int {
    return input
        .map(String::correctMove)
        .sumOf(String::toScore)
}

private val SCORES = mapOf(
    "AX" to 4,
    "AY" to 8,
    "AZ" to 3,
    "BX" to 1,
    "BY" to 5,
    "BZ" to 9,
    "CX" to 7,
    "CY" to 2,
    "CZ" to 6,
)

private fun String.toScore(): Int {
    return SCORES.getValue("${this[0]}${this[2]}")
}

private val CORRECTIONS = mapOf(
    "AX" to "A Z",
    "AY" to "A X",
    "AZ" to "A Y",
    "BX" to "B X",
    "BY" to "B Y",
    "BZ" to "B Z",
    "CX" to "C Y",
    "CY" to "C Z",
    "CZ" to "C X",
)

private fun String.correctMove(): String {
    return CORRECTIONS.getValue("${this[0]}${this[2]}")
}