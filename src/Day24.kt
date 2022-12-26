import java.util.*

fun main() {
    val testInput = readInput("Day24_test")
    val testMap = BlizzardMap(testInput)
    check(part1(testMap) == 18)
    check(part2(testMap) == 54)

    val input = readInput("Day24")
    val map = BlizzardMap(input)
    println(part1(map))
    println(part2(map))
}

private fun part1(map: BlizzardMap): Int {
    val initialMap = map[0]
    val startI = 0
    val startJ = initialMap[startI].indexOfFirst { it }
    val targetI = initialMap.lastIndex
    val targetJ = initialMap[targetI].indexOfFirst { it }

    return solve(
        map = map,
        startMinute = 0,
        startI = startI,
        startJ = startJ,
        targetI = targetI,
        targetJ = targetJ,
    )
}

private fun part2(map: BlizzardMap): Int {
    val initialMap = map[0]
    val startI = 0
    val startJ = initialMap[startI].indexOfFirst { it }
    val targetI = initialMap.lastIndex
    val targetJ = initialMap[targetI].indexOfFirst { it }

    val m1 = solve(
        map = map,
        startMinute = 0,
        startI = startI,
        startJ = startJ,
        targetI = targetI,
        targetJ = targetJ,
    )

    val m2 = solve(
        map = map,
        startMinute = m1,
        startI = targetI,
        startJ = targetJ,
        targetI = startI,
        targetJ = startJ,
    )

    val m3 = solve(
        map = map,
        startMinute = m2,
        startI = startI,
        startJ = startJ,
        targetI = targetI,
        targetJ = targetJ,
    )

    return m3
}

private fun solve(
    map: BlizzardMap,
    startMinute: Int,
    startI: Int,
    startJ: Int,
    targetI: Int,
    targetJ: Int
): Int {
    val queue = PriorityQueue<Item>()
    queue.offer(Item(startMinute, IJ(startI, startJ)))

    val done = mutableSetOf<Int>()

    while (queue.isNotEmpty()) {
        val (minute, ij) = queue.poll()
        val (i, j) = ij

        val key = minute * 1_000_000 + i * 1_000 + j
        if (key in done) {
            continue
        }
        done.add(key)


        if (i == targetI && j == targetJ) {
            return minute
        }

        val currentMap = map[minute + 1]

        if (currentMap[i][j]) {
            queue.offer(Item(minute + 1, IJ(i, j)))
        }

        if (i > 0 && currentMap[i - 1][j]) {
            queue.offer(Item(minute + 1, IJ(i - 1, j)))
        }
        if (i < currentMap.lastIndex && currentMap[i + 1][j]) {
            queue.offer(Item(minute + 1, IJ(i + 1, j)))
        }
        if (j > 0 && currentMap[i][j - 1]) {
            queue.offer(Item(minute + 1, IJ(i, j - 1)))
        }
        if (j < currentMap[i].lastIndex && currentMap[i][j + 1]) {
            queue.offer(Item(minute + 1, IJ(i, j + 1)))
        }
    }

    error("Path not found")
}

private class BlizzardMap(initialState: List<String>) {
    private val cache = mutableListOf<BooleanMatrix>()
    private val template: BooleanMatrix
    private val blizzards: List<Blizzard>

    init {
        blizzards = buildList {
            initialState.forEachIndexed { i, row ->
                row.forEachIndexed { j, ch ->
                    when (ch) {
                        '>' -> add(Blizzard(MutableIJ(i, j), BlizzardDirection.RIGHT))
                        'v' -> add(Blizzard(MutableIJ(i, j), BlizzardDirection.DOWN))
                        '<' -> add(Blizzard(MutableIJ(i, j), BlizzardDirection.LEFT))
                        '^' -> add(Blizzard(MutableIJ(i, j), BlizzardDirection.UP))
                    }
                }
            }
        }

        template = Array(initialState.size) { i ->
            BooleanArray(initialState[i].length) { j ->
                initialState[i][j] != '#'
            }
        }

        //generate default map
        generateNextMap()
    }

    operator fun get(i: Int): BooleanMatrix {
        while (cache.lastIndex < i) {
            generateNextMap()
        }

        return cache[i]
    }

    private fun generateNextMap() {
        val next = template.deepCopyOf()

        blizzards.forEach {
            with(it) {
                next[position.i][position.j] = false

                //Also, move blizzard to the new position
                when (direction) {
                    BlizzardDirection.RIGHT -> position.j = if (position.j < (template[0].lastIndex - 1)) {
                        position.j + 1
                    } else {
                        1
                    }
                    BlizzardDirection.DOWN -> position.i = if (position.i < (template.lastIndex - 1)) {
                        position.i + 1
                    } else {
                        1
                    }
                    BlizzardDirection.LEFT -> position.j = if (position.j > 1) {
                        position.j - 1
                    } else {
                        template[0].lastIndex - 1
                    }
                    BlizzardDirection.UP -> position.i = if (position.i > 1) {
                        position.i - 1
                    } else {
                        template.lastIndex - 1
                    }
                }
            }
        }

        cache.add(next)
    }
}

private enum class BlizzardDirection { RIGHT, DOWN, LEFT, UP }

private class Blizzard(
    val position: MutableIJ,
    val direction: BlizzardDirection,
)
