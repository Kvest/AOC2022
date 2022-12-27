import java.util.*

fun main() {
    val testInput = readInput("Day17_test")[0]
    check(part1(testInput) == 3068)
    check(part2(testInput) == 1514285714288L)

    val input = readInput("Day17")[0]
    println(part1(input))
    println(part2(input))
}

private const val PART1_ROCKS_COUNT = 2022
private const val PART2_ROCKS_COUNT = 1_000_000_000_000L

private fun part1(input: String): Int {
    val shifter = Shifter(input)
    val cave = LinkedList<Int>()
    val generator = RockGenerator()

    repeat(PART1_ROCKS_COUNT) {
        val rock = generator.nextRock()
        cave.fall(rock, shifter)
    }

    return cave.size
}

private const val PATTERN_SIZE = 20

private fun part2(input: String): Long {
    val shifter = Shifter(input)
    val cave = LinkedList<Int>()
    val generator = RockGenerator()

    var pattern: List<Int>? = null
    var patternFoundIteration = 0L
    var patternFoundCaveSize = 0
    var skippedCaveSize = 0L

    var count = PART2_ROCKS_COUNT
    while (count > 0) {
        val rock = generator.nextRock()
        cave.fall(rock, shifter)

        --count

        //State of the cave repeats iteratively. Find this repetition and use it to skip the huge amount of calculations
        if (pattern == null) {
            //Try to find pattern
            val index = cave.findPattern(PATTERN_SIZE)
            if (index >= 0) {
                pattern = cave.takeLast(PATTERN_SIZE)
                patternFoundIteration = count
                patternFoundCaveSize = cave.size
            }
        } else {
            //Wait for the next repetition of the pattern
            if (cave.subList(cave.size - PATTERN_SIZE, cave.size) == pattern) {
                val rocksPerIteration = patternFoundIteration - count
                val caveIncreasePerIteration = cave.size - patternFoundCaveSize

                val skippedIterations = count / rocksPerIteration
                skippedCaveSize = skippedIterations * caveIncreasePerIteration
                count %= rocksPerIteration
            }
        }
    }

    return skippedCaveSize + cave.size
}

private fun List<Int>.findPattern(patternSize: Int): Int {
    if (this.size < 2 * patternSize) {
        return -1
    }

    val tail = this.subList(this.size - patternSize, this.size)
    for (i in (this.size - patternSize) downTo patternSize) {
        if (tail == this.subList(i - patternSize, i)) {
            return i
        }
    }

    return -1
}

private fun LinkedList<Int>.fall(rock: IntArray, shifter: Shifter) {
    val caveRows = IntArray(rock.size)

    //First 4 shifts happen before rock will rich the most top row of the cave
    repeat(4) {
        shifter.next().invoke(rock, caveRows)
    }

    var bottom = this.lastIndex

    while (true) {
        if (bottom == -1) {
            break
        }

        for (i in caveRows.lastIndex downTo 1) {
            caveRows[i] = caveRows[i - 1]
        }
        caveRows[0] = this[bottom]

        val canFall = rock.foldIndexed(true) { i, acc, value -> acc && (value and caveRows[i] == 0) }
        if (canFall) {
            --bottom
        } else {
            break
        }

        shifter.next().invoke(rock, caveRows)
    }

    rock.forEachIndexed { i, value ->
        if ((bottom + i + 1) in this.indices) {
            this[bottom + i + 1] = this[bottom + i + 1] or value
        } else {
            this.addLast(value)
        }
    }
}

private const val LEFT_BORDER = 0b1000000
private const val RIGHT_BORDER = 0b0000001

class Shifter(private val pattern: String) {
    private var current: Int = 0

    private val left: (IntArray, IntArray) -> Unit = { rock, caveRows ->
        val canShift = rock.foldIndexed(true) { i, acc, value ->
            acc && ((value and LEFT_BORDER) == 0) && ((value shl 1) and caveRows[i] == 0)
        }

        if (canShift) {
            rock.forEachIndexed { i, value -> rock[i] = value shl 1 }
        }
    }

    private val right: (IntArray, IntArray) -> Unit = { rock, caveRows ->
        val canShift = rock.foldIndexed(true) { i, acc, value ->
            acc && (value and RIGHT_BORDER == 0) && ((value shr 1) and caveRows[i] == 0)
        }

        if (canShift) {
            rock.forEachIndexed { i, value -> rock[i] = value shr 1 }
        }
    }

    fun next(): (IntArray, IntArray) -> Unit {
        return (if (pattern[current] == '<') left else right).also {
            current = (current + 1) % pattern.length
        }
    }
}

private class RockGenerator {
    private var i = 0
    val rocks = listOf(
        intArrayOf(
            0b0011110,
        ),
        intArrayOf(
            0b0001000,
            0b0011100,
            0b0001000,
        ),
        intArrayOf(
            0b0011100,
            0b0000100,
            0b0000100,
        ),
        intArrayOf(
            0b0010000,
            0b0010000,
            0b0010000,
            0b0010000,
        ),
        intArrayOf(
            0b0011000,
            0b0011000,
        ),
    )

    fun nextRock(): IntArray {
        return rocks[i].copyOf().also {
            i = (i + 1) % rocks.size
        }
    }
}
