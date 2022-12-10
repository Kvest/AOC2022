fun main() {
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)


    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}

private val TARGET_CYCLES = setOf(20, 60, 100, 140, 180, 220)
private fun part1(input: List<String>): Int {
    var result = 0

    val communicationSystem = CommunicationSystem(input)
    communicationSystem.runProgram { cycle, x ->
        if (cycle in TARGET_CYCLES) {
            result += cycle * x
        }
    }

    return result
}

private fun part2(input: List<String>) {
    val communicationSystem = CommunicationSystem(input)
    communicationSystem.runProgram { cycle, x ->
        if ((cycle % 40) in x..(x + 2)) {
            print('#')
        } else {
            print('.')
        }

        if (cycle % 40 == 0) {
            println()
        }
    }
}

private typealias CycleListener = (Int, Int) -> Unit

private class CommunicationSystem(
    private val instructions: List<String>
) {
    private var cycle = 1
    private var x = 1

    fun reset() {
        cycle = 1
        x = 1
    }

    fun runProgram(cycleListener: CycleListener) {
        instructions.forEach { instruction ->
            when {
                instruction == "noop" -> noop(cycleListener)
                instruction.startsWith("addx") -> addx(instruction.substringAfter(" ").toInt(), cycleListener)
            }
        }
    }

    private fun noop(cycleListener: CycleListener) {
        cycleListener(cycle, x)
        ++cycle
    }

    private fun addx(input: Int, cycleListener: CycleListener) {
        cycleListener(cycle, x)

        ++cycle

        cycleListener(cycle, x)

        ++cycle
        x += input
    }
}

