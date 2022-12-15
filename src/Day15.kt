import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
    val testInput = readInput("Day15_test")
    val (testSensors, testBeacons) = parseInput(testInput)
    check(part1(testSensors, testBeacons, targetLine = 10) == 26)
    check(part2(testSensors, bound = 20) == 56000011L)

    val input = readInput("Day15")
    val (sensors, beacons) = parseInput(input)
    println(part1(sensors, beacons, targetLine = 2000000))
    println(part2(sensors, bound = 4000000))
}

private fun part1(sensors: List<Sensor>, beacons: Set<XY>, targetLine: Int): Int {
    val intervals = calculateLineCoverage(sensors, targetLine)

    val beaconsOnLine = beacons.filter { beacon ->
        beacon.y == targetLine
    }.count()

    return intervals.sumOf { it.size } - beaconsOnLine
}

private fun part2(sensors: List<Sensor>, bound: Int): Long {
    for (y in 0..bound) {
        val intervals = calculateLineCoverage(sensors, y)

        if (intervals[0].first > 0) {
            return y.toLong()
        }

        for (i in 1..intervals.lastIndex) {
            if (intervals[i].first - intervals[i - 1].last > 1) {
                return (intervals[i - 1].last + 1) * 4000000L + y
            }
        }

        if (intervals.last().last < bound) {
            return (intervals.last().last + 1) * 4000000L + y
        }
    }

    error("Not found")
}

private val ROW_FORMAT = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
private fun parseInput(input: List<String>): Pair<List<Sensor>, Set<XY>> {
    val sensors = mutableListOf<Sensor>()
    val beacons = mutableSetOf<XY>()

    input.forEach {
        val match = ROW_FORMAT.find(it)
        val (x, y, beaconX, beaconY) = requireNotNull(match).destructured

        val radius = abs(x.toInt() - beaconX.toInt()) + abs(y.toInt() - beaconY.toInt())
        sensors.add(Sensor(x = x.toInt(), y = y.toInt(), radius = radius))

        beacons.add(XY(x = beaconX.toInt(), y = beaconY.toInt()))
    }

    return Pair(sensors, beacons)
}

private fun calculateLineCoverage(sensors: List<Sensor>, line: Int): List<IntRange> {
    val intervals = sensors
        .mapNotNull { sensor ->
            val count = sensor.radius - abs(sensor.y - line)

            if (count > 0) {
                ((sensor.x - count)..(sensor.x + count))
            } else {
                null
            }
        }
        .sortedBy { it.first }

    val result = LinkedList<IntRange>()

    //collapse intervals if possible
    result.addLast(intervals.first())
    for (i in 1..intervals.lastIndex) {
        if (result.last.last >= intervals[i].first) {
            val tmp = result.pollLast()
            result.addLast(tmp.first..max(tmp.last, intervals[i].last))
        } else {
            result.addLast(intervals[i])
        }
    }

    return result
}

private data class Sensor(
    val x: Int,
    val y: Int,
    val radius: Int,
)