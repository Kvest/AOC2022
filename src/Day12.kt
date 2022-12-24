import java.util.*

fun main() {
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))

}

private fun part1(input: List<String>, ): Int = solve(input, startMarkers = setOf('S'))
private fun part2(input: List<String>, ): Int = solve(input, startMarkers = setOf('S', 'a'))

private fun solve(input: List<String>, startMarkers: Set<Char>): Int {
    val area = input.toMatrix()
    val starts = input.findAllChars(startMarkers)
    val end = input.findChar('E')
    val visited = mutableSetOf<IJ>()

    val queue = PriorityQueue<Item>()

    starts.forEach {
        queue.offer(Item(0, it))
    }

    while (queue.isNotEmpty()) {
        val (steps, ij) = queue.poll()

        if (ij in visited) {
            continue
        }

        if (ij == end) {
            return steps
        }

        val (i, j) = ij
        if (i > 0 && (area[i - 1][j] - area[i][j]) <= 1) {
            queue.offer(Item(steps + 1, IJ(i - 1, j)))
        }
        if (j > 0 && (area[i][j - 1] - area[i][j]) <= 1) {
            queue.offer(Item(steps + 1, IJ(i, j - 1)))
        }
        if (i < area.lastIndex && (area[i + 1][j] - area[i][j]) <= 1) {
            queue.offer(Item(steps + 1, IJ(i + 1, j)))
        }
        if (j < area[i].lastIndex && (area[i][j + 1] - area[i][j]) <= 1) {
            queue.offer(Item(steps + 1, IJ(i, j + 1)))
        }

        visited.add(ij)
    }

    error("Path not found")
}

private fun List<String>.toMatrix(): Matrix {
    return Array(this.size) { i ->
        IntArray(this[i].length) { j ->
            when (this[i][j]) {
                'S' -> 0
                'E' -> 'z'.code - 'a'.code
                else -> this[i][j].code - 'a'.code
            }
        }
    }
}

private fun List<String>.findChar(target: Char): IJ {
    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, ch ->
            if (ch == target) {
                return IJ(i, j)
            }
        }
    }

    error("$target not found")
}

private fun List<String>.findAllChars(targets: Set<Char>): List<IJ> {
    val result = mutableListOf<IJ>()

    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, ch ->
            if (ch in targets) {
                result.add(IJ(i, j))
            }
        }
    }

    return result
}