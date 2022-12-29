import java.util.*
import kotlin.math.max

fun main() {
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

private const val PART1_MINUTES = 30
private const val PART2_MINUTES = 26
private const val START_VALVE = "AA"

private fun part1(input: List<String>): Int {
    val valves = input.parseInputAndOptimize()
    val bitsMapper = valves.calcBitsMapper()

    return solve(
        minutesLeft = PART1_MINUTES,
        name = START_VALVE,
        openedBitmask = 0,
        valves = valves,
        bitsMapper = bitsMapper
    )
}

private fun part2(input: List<String>): Int {
    val valves = input.parseInputAndOptimize()
    val bitsMapper = valves.calcBitsMapper()

    val valvesToOpen = bitsMapper.keys.toList() - START_VALVE
    val allBits = valvesToOpen.fold(0L) { acc, valve -> acc or bitsMapper.getValue(valve) }

    val memo = mutableMapOf<Long, Int>()

    //Brut-force all possible variants where some valves will be opened by me and the rest - by elephant
    return valvesToOpen.allCombinations()
        .map { valves ->
            valves.fold(0L) { acc, valve -> acc or bitsMapper.getValue(valve) }
        }
        .maxOf { valveBits ->
            val otherBits = allBits and valveBits.inv()

            val first = memo.getOrPut(valveBits) {
                solve(
                    minutesLeft = PART2_MINUTES,
                    name = START_VALVE,
                    openedBitmask = valveBits,
                    valves = valves,
                    bitsMapper = bitsMapper
                )
            }

            val second = memo.getOrPut(otherBits) {
                solve(
                    minutesLeft = PART2_MINUTES,
                    name = START_VALVE,
                    openedBitmask = otherBits,
                    valves = valves,
                    bitsMapper = bitsMapper
                )
            }

            first + second
        }
}

private fun solve(
    minutesLeft: Int,
    name: String,
    openedBitmask: Long,
    valves: Map<String, Valve>,
    bitsMapper: Map<String, Long>,
    memo: MutableMap<String, Int> = mutableMapOf()
): Int {
    val key = "${minutesLeft}_${name}_$openedBitmask"
    if (key in memo) {
        return memo.getValue(key)
    }

    val valve = valves.getValue(name)

    val resultInc = valve.rate * minutesLeft
    var result = resultInc

    valve.destinations.forEach { (nextName, timeToReachAndOpen) ->
        val nextBit = bitsMapper.getValue(nextName)

        if (minutesLeft > timeToReachAndOpen && (openedBitmask and nextBit) == 0L) {
            result = max(
                result,
                resultInc + solve(
                    minutesLeft = minutesLeft - timeToReachAndOpen,
                    name = nextName,
                    openedBitmask = (openedBitmask or nextBit),
                    valves = valves,
                    bitsMapper = bitsMapper,
                    memo = memo
                )
            )
        }
    }

    memo[key] = result

    return result
}

private val ROW_FORMAT = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? ([A-Z, ]+)")
private fun List<String>.parseInputAndOptimize(): Map<String, Valve> {
    val rates = mutableMapOf<String, Int>()
    val tunnelsFromValve = mutableMapOf<String, List<String>>()

    this.forEach {
        val match = ROW_FORMAT.find(it)
        val (valve, rate, tunnels) = requireNotNull(match).destructured

        rates[valve] = rate.toInt()
        tunnelsFromValve[valve] = tunnels.split(", ")
    }

    // Consider only valves with rate > 0
    // Don't forget to add START valve
    return rates.filter { it.value > 0 || it.key == START_VALVE }
        .mapValues { (name, rate) ->
            Valve(
                rate = rate,
                destinations = buildTunnelsChains(name, tunnelsFromValve, rates)
            )
        }
}

private fun buildTunnelsChains(
    start: String,
    tunnels: Map<String, List<String>>,
    rates: Map<String, Int>
): List<Destination> {
    return buildList {
        val queue = PriorityQueue<Pair<String, Int>> { a, b -> a.second - b.second }
        queue.offer(start to 0)

        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val (name, time) = queue.poll()

            if (name in visited) {
                continue
            }
            visited.add(name)

            if (rates.getValue(name) > 0 && name != start) {
                add(Destination(name, time + 1)) //add 1 minute for opening
            }

            tunnels[name]?.forEach { next ->
                queue.offer(next to time + 1)
            }
        }
    }
}

private fun Map<String, Valve>.calcBitsMapper(): Map<String, Long> {
    var i = 1L
    return this.mapValues {
        val res = i
        i = i shl 1

        res
    }
}

private data class Valve(
    val rate: Int,
    val destinations: List<Destination>
)

private data class Destination(
    val name: String,
    val timeToReachAndOpen: Int
)