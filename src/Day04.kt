fun main() {
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input
        .map(String::parseSectionRanges)
        .count { it.first in it.second || it.second in it.first }
}

private fun part2(input: List<String>): Int {
    return input
        .map(String::parseSectionRanges)
        .count { it.first.first in it.second || it.second.first in it.first }
}

private val ROW_FORMAT = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")

private fun String.parseSectionRanges(): Pair<IntRange, IntRange> {
    val match = ROW_FORMAT.find(this)
    val (a, b, c, d) = requireNotNull(match).destructured
    return IntRange(a.toInt(), b.toInt()) to IntRange(c.toInt(), d.toInt())
}



