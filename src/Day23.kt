import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
     check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

private val DIRECTIONS = listOf(
    //N, NE, NW
    listOf(
        intArrayOf(-1, 0),
        intArrayOf(-1, 1),
        intArrayOf(-1, -1)
    ),
    //S, SE, SW
    listOf(
        intArrayOf(1, 0),
        intArrayOf(1, 1),
        intArrayOf(1, -1)
    ),
    //W, NW, SW
    listOf(
        intArrayOf(0, -1),
        intArrayOf(-1, -1),
        intArrayOf(1, -1)
    ),
    //E, NE, SE
    listOf(
        intArrayOf(0, 1),
        intArrayOf(-1, 1),
        intArrayOf(1, 1)
    ),
)

private val ALL_ADJACENT = listOf(
    intArrayOf(-1, -1),
    intArrayOf(-1, 0),
    intArrayOf(-1, 1),
    intArrayOf(0, 1),
    intArrayOf(1, 1),
    intArrayOf(1, 0),
    intArrayOf(1, -1),
    intArrayOf(0, -1),
)

private fun part1(input: List<String>): Int {
    var elves = input.toElves()

    val moveMap = mutableMapOf<IJ, IJ>()
    val counts = mutableMapOf<IJ, Int>()
    repeat(10) { iterationNumber ->
        moveMap.clear()
        counts.clear()

        elves.calculateMoves(iterationNumber, moveMap, counts)

        elves = elves.map { elf ->
            moveMap[elf]?.takeIf { counts[it] == 1 } ?: elf
        }.toSet()
    }

    var iFrom = elves.first().i
    var jFrom = elves.first().j
    var iTo = iFrom
    var jTo = jFrom
    elves.forEach {
        iFrom = min(iFrom, it.i)
        jFrom = min(jFrom, it.j)
        iTo = max(iTo, it.i)
        jTo = max(jTo, it.j)
    }

    var count = 0
    for (i in iFrom..iTo) {
        for (j in jFrom..jTo) {
            if (IJ(i, j) !in elves) {
                ++count
            }
        }
    }

    return count
}

private fun part2(input: List<String>): Int {
    var elves = input.toElves()

    var iterationNumber = 0

    val moveMap = mutableMapOf<IJ, IJ>()
    val counts = mutableMapOf<IJ, Int>()
    while(true) {
        moveMap.clear()
        counts.clear()

        elves.calculateMoves(iterationNumber, moveMap, counts)

        if (moveMap.isEmpty()) {
            break
        }

        elves = elves.map { elf ->
            moveMap[elf]?.takeIf { counts[it] == 1 } ?: elf
        }.toSet()

        ++iterationNumber
    }

    return iterationNumber + 1
}

private fun List<String>.toElves(): Set<IJ> {
    return this.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, ch ->
            if (ch == '#') {
                IJ(i, j)
            } else {
                null
            }
        }
    }.toSet()
}

private fun Set<IJ>.calculateMoves(
    iterationNumber: Int,
    moveMap: MutableMap<IJ, IJ>,
    counts: MutableMap<IJ, Int>
) {
    this.forEach { elf ->
        val shouldMove = ALL_ADJACENT.any { delta ->
            IJ(elf.i + delta[0], elf.j + delta[1]) in this
        }

        if (shouldMove) {
            val moveDirection = DIRECTIONS.indices.firstNotNullOfOrNull { i ->
                DIRECTIONS[(i + iterationNumber) % DIRECTIONS.size].takeIf { dir ->
                    dir.all { delta -> IJ(elf.i + delta[0], elf.j + delta[1]) !in this }
                }
            }

            if (moveDirection != null) {
                val newLocation = IJ(elf.i + moveDirection[0][0], elf.j + moveDirection[0][1])
                moveMap[elf] = newLocation
                counts[newLocation] = counts.getOrDefault(newLocation, 0) + 1
            }
        }
    }
}