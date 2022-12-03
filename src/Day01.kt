fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.toCalories().maxOrNull() ?: 0
}

private fun part2(input: List<String>): Int {
    return input
        .toCalories()
        .sorted()
        .takeLast(3)
        .sum()
}

private fun List<String>.toCalories(): List<Int> {
    val result = mutableListOf<Int>()
    var current = 0

    this.forEach { row ->
        if (row.isEmpty()) {
            result.add(current)
            current = 0
        } else {
            current += row.toInt()
        }
    }
    result.add(current)

    return result
}
