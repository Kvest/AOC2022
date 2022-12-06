fun main() {
    val testInput = readInput("Day06_test")
    check(part1(testInput[0]) == 5)
    check(part1(testInput[1]) == 6)
    check(part1(testInput[2]) == 10)
    check(part1(testInput[3]) == 11)

    check(part2(testInput[0]) == 23)
    check(part2(testInput[1]) == 23)
    check(part2(testInput[2]) == 29)
    check(part2(testInput[3]) == 26)

    val input = readInput("Day06")
    println(part1(input[0]))
    println(part2(input[0]))
}

private fun part1(input: String): Int = findUniqueSequence(input, 4)
private fun part2(input: String): Int = findUniqueSequence(input, 14)

private fun findUniqueSequence(input: String, targetCount: Int): Int {
    val counts = mutableMapOf<Char, Int>()
    repeat(targetCount - 1) { i ->
        counts[input[i]] = counts.getOrDefault(input[i], 0) + 1
    }

    for (i in (targetCount - 1)..input.lastIndex) {
        counts[input[i]] = counts.getOrDefault(input[i], 0) + 1

        if (counts.size == targetCount) {
            return i + 1
        }

        if (counts[input[i - targetCount + 1]] == 1) {
            counts.remove(input[i - targetCount + 1])
        } else {
            counts[input[i - targetCount + 1]] = counts.getValue(input[i - targetCount + 1]) - 1
        }
    }

    error("Not found")
}