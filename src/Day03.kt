fun main() {
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.sumOf(::calculatePriority)
}

private fun part2(input: List<String>): Int {
    return input
        .chunked(3) { group ->
            commonPriority(group[0], group[1], group[2])
        }
        .sum()
}

private fun calculatePriority(row: String): Int {
    val second = row.takeLast(row.length / 2).toSet()

    row.forEach { ch ->
        if (ch in second) {
            return ch.toPriority()
        }
    }

    throw IllegalStateException("No found")
}

private fun commonPriority(first: String, second: String, third: String): Int {
    val secondSet = second.toSet()
    val thirdSet = third.toSet()

    first.forEach { ch ->
        if (ch in secondSet && ch in thirdSet) {
            return ch.toPriority()
        }
    }

    throw IllegalStateException("No found")
}

private fun Char.toPriority(): Int {
    return if (this.isLowerCase()) {
        1 + code - 'a'.code
    } else {
        27 + code - 'A'.code
    }
}