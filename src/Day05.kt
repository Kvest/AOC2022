import java.util.*

fun main() {
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))

}

private fun part1(input: List<String>): String =
    solve(input) { stacks, cmd ->
        repeat(cmd.count) {
            stacks[cmd.to].push(stacks[cmd.from].pop())
        }
    }

private fun part2(input: List<String>): String {
    val stack = LinkedList<Char>()

    return solve(input) { stacks, cmd ->
        repeat(cmd.count) {
            stack.push(stacks[cmd.from].pop())
        }

        while (stack.isNotEmpty()) {
            stacks[cmd.to].push(stack.pop())
        }
    }
}

private fun solve(input: List<String>, reorderStrategy: (List<LinkedList<Char>>, Command) -> Unit): String {
    val dividerIndex = input.indexOfFirst { it.isEmpty() }
    val stacks = input
        .subList(0, dividerIndex)
        .toInitialStacks()

    input
        .subList(dividerIndex + 1, input.lastIndex + 1)
        .map(String::toCommand)
        .forEach { cmd ->
            reorderStrategy(stacks, cmd)
        }

    return stacks.joinToString(separator = "") {
        it.pop().toString()
    }
}

private fun List<String>.toInitialStacks(): List<LinkedList<Char>> {
    val count = this.last()
        .trim()
        .drop(
            this.last()
                .lastIndexOf(' ')
        )
        .toInt()
    val list = List(count) { LinkedList<Char>() }

    this.subList(0, this.lastIndex)
        .asReversed()
        .forEach { row ->
            var i = 1
            while (i <= row.lastIndex) {
                if (row[i].isLetter()) {
                    list[i / 4].push(row[i])
                }

                i += 4
            }
        }

    return list
}

private val COMMAND_FORMAT = Regex("move (\\d+) from (\\d+) to (\\d+)")

private fun String.toCommand(): Command {
    val match = COMMAND_FORMAT.find(this)
    val (move, from, to) = requireNotNull(match).destructured
    return Command(from = from.toInt() - 1, to = to.toInt() - 1, count = move.toInt())
}

private class Command(val from: Int, val to: Int, val count: Int)