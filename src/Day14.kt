import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private val SAND_START_POINT = XY(500, 0)

private fun part1(input: List<String>): Int {
    val points = mutableSetOf<XY>()
    parseInput(input, points)

    val bottom = points.maxOf { it.y }
    val rocksCount = points.size

    solve(bottom, points, SAND_START_POINT, exitOnBottom = true)

    return points.size - rocksCount
}

private fun part2(input: List<String>): Int {
    val points = mutableSetOf<XY>()
    parseInput(input, points)

    val bottom = points.maxOf { it.y } + 2
    val rocksCount = points.size

    solve(bottom, points, SAND_START_POINT, exitOnBottom = false)

    return points.size - rocksCount
}

private fun parseInput(input: List<String>, points: MutableSet<XY>) {
    input
        .map { it.split(" -> ") }
        .forEach { row ->
            row.windowed(2, 1) { (from, to) ->
                val xFrom = from.substringBefore(",").toInt()
                val yFrom = from.substringAfter(",").toInt()

                val xTo = to.substringBefore(",").toInt()
                val yTo = to.substringAfter(",").toInt()

                for (x in min(xFrom, xTo)..max(xFrom, xTo)) {
                    for (y in min(yFrom, yTo)..max(yFrom, yTo)) {
                        points.add(XY(x = x, y = y))
                    }
                }
            }
        }
}

private fun solve(bottom: Int, points: MutableSet<XY>, point: XY, exitOnBottom: Boolean): Boolean {
    if (points.contains(point)) {
        return false
    }

    if (point.y == bottom) {
        return exitOnBottom
    }

    if (solve(bottom, points, XY(x = point.x, y = point.y + 1), exitOnBottom)) return true
    if (solve(bottom, points, XY(x = point.x - 1, y = point.y + 1), exitOnBottom)) return true
    if (solve(bottom, points, XY(x = point.x + 1, y = point.y + 1), exitOnBottom)) return true

    points.add(point)

    return false
}