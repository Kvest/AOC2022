import java.util.*

fun main() {
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val monkeys = input.toMonkeys()
    val worryLevelAdjuster = WorryLevelAdjuster { worryLevel ->
        worryLevel / 3
    }

    return solve(monkeys, roundsCount = 20, worryLevelAdjuster)
}

private fun part2(input: List<String>): Long {
    val monkeys = input.toMonkeys()

    //Use divider in order to avoid overflow of the worryLevel's variable
    val divider = monkeys.fold(1L) { acc, monkey -> acc * monkey.test.testValue }
    val worryLevelAdjuster = WorryLevelAdjuster { worryLevel ->
        worryLevel % divider
    }

    return solve(monkeys, roundsCount = 10_000, worryLevelAdjuster)
}

private fun solve(
    monkeys: List<Monkey>,
    roundsCount: Int,
    worryLevelAdjuster: WorryLevelAdjuster
): Long {
    val counts = LongArray(monkeys.size) { 0 }

    val redirectListener = RedirectListener { monkeyNumber, worryLevel ->
        monkeys[monkeyNumber].items.addLast(worryLevel)
    }

    repeat(roundsCount) {
        monkeys.forEachIndexed { index, monkey ->
            //monkey will inspect "monkey.items.size" in this round
            counts[index] += monkey.items.size.toLong()

            monkey.round(worryLevelAdjuster, redirectListener)
        }
    }

    counts.sortDescending()
    return counts[0] * counts[1]
}

private fun List<String>.toMonkeys(): List<Monkey> = this.chunked(7).map(List<String>::toMonkey)

private fun List<String>.toMonkey(): Monkey {
    val initialItems = this[1]
        .substringAfter("Starting items: ")
        .split(", ")
        .map { it.toLong() }
    val operation = Operation.fromString(this[2])

    val testValue = this[3].substringAfter("Test: divisible by ").toLong()
    val trueDestination = this[4].substringAfter("If true: throw to monkey ").toInt()
    val falseDestination = this[5].substringAfter("If false: throw to monkey ").toInt()
    val test = Test(
        testValue = testValue,
        trueDestination = trueDestination,
        falseDestination = falseDestination
    )

    return Monkey(initialItems, operation, test)
}

private fun interface RedirectListener {
    fun redirect(monkeyNumber: Int, worryLevel: Long)
}

private fun interface WorryLevelAdjuster {
    fun adjust(worryLevel: Long): Long
}

private class Monkey(
    initialItems: List<Long>,
    val operation: Operation,
    val test: Test
) {
    val items = LinkedList(initialItems)

    fun round(worryLevelAdjuster: WorryLevelAdjuster, redirectListener: RedirectListener) {
        while (items.isNotEmpty()) {
            var worryLevel = items.pollFirst()
            worryLevel = operation.perform(worryLevel)
            worryLevel = worryLevelAdjuster.adjust(worryLevel)

            val monkeyNumber = test.test(worryLevel)
            redirectListener.redirect(monkeyNumber, worryLevel)
        }
    }
}

sealed interface Operation {
    fun perform(worryLevel: Long): Long

    companion object {
        fun fromString(operation: String): Operation {
            return when {
                operation.contains("Operation: new = old * old") -> SqrOperation
                operation.contains("Operation: new = old +") -> AdditionOperation(
                    incValue = operation.substringAfter("Operation: new = old + ").toLong()
                )
                operation.contains("Operation: new = old *") -> MultiplicationOperation(
                    factor = operation.substringAfter("Operation: new = old * ").toLong()
                )
                else -> error("Unknown operation $operation")
            }
        }
    }

    class AdditionOperation(private val incValue: Long) : Operation {
        override fun perform(worryLevel: Long): Long = worryLevel + incValue
    }

    class MultiplicationOperation(private val factor: Long) : Operation {
        override fun perform(worryLevel: Long): Long = worryLevel * factor
    }

    object SqrOperation : Operation {
        override fun perform(worryLevel: Long): Long = worryLevel * worryLevel
    }
}

private class Test(
    val testValue: Long,
    val trueDestination: Int,
    val falseDestination: Int,
) {
    fun test(worryLevel: Long): Int = if (worryLevel % testValue == 0L) trueDestination else falseDestination
}