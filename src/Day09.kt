import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val testInput1 = readInput("Day09_test1")
    val testInput2 = readInput("Day09_test2")
    check(part1(testInput1) == 13)
    check(part2(testInput1) == 1)
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = solve(input, 2)
private fun part2(input: List<String>): Int = solve(input, 10)

private fun solve(input: List<String>, ropeSize: Int): Int {
    val rope = List(ropeSize) { MutableXY(0, 0) }
    val adjacentNodes = rope.windowed(2, 1)
    val head = rope.first()
    val tail = rope.last()
    val visitedCells = mutableSetOf<XY>()
    var dx = 0
    var dy = 0

    input.forEach { action ->
        val cnt = action.substringAfter(" ").toInt()
        when {
            action.startsWith("L") -> {
                dx = -1
                dy = 0
            }
            action.startsWith("R") -> {
                dx = 1
                dy = 0
            }
            action.startsWith("U") -> {
                dx = 0
                dy = 1
            }
            action.startsWith("D") -> {
                dx = 0
                dy = -1
            }
        }

        repeat(cnt) {
            head.x += dx
            head.y += dy

            adjacentNodes.forEach { (first, second) ->
                adjustNodes(first, second)
            }

            visitedCells.add(tail.toXY())
        }
    }

    return visitedCells.size
}

private fun adjustNodes(first: MutableXY, second: MutableXY) {
    if (abs(first.x - second.x) >= 2 || abs(first.y - second.y) >= 2) {
        second.x = second.x + 1 * (first.x - second.x).sign
        second.y = second.y + 1 * (first.y - second.y).sign
    }
}