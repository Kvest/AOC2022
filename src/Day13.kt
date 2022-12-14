fun main() {
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.windowed(size = 2, step = 3)
        .map { list -> list.map { it.parseList() } }
        .mapIndexed { index, (first, second) ->
            if (checkOrder(first.values, second.values) == Order.RIGHT) index + 1 else 0
        }
        .sum()
}

private fun part2(input: List<String>): Int {
    val dividerPackets = listOf(
        "[[2]]".parseList(),
        "[[6]]".parseList(),
    )

    val originalPackets = input
        .filter { it.isNotEmpty() }
        .map(String::parseList)

    return (originalPackets + dividerPackets)
        .sortedWith(ListValueComparator)
        .mapIndexed { index, listValue ->
            if (listValue in dividerPackets) index + 1 else 1
        }
        .reduce { acc, value -> acc * value }
}

private object ListValueComparator : Comparator<ListValue> {
    override fun compare(left: ListValue, right: ListValue): Int =
        checkOrder(left.values, right.values).value
}

private fun String.parseList(
    fromIndex: Int = 0,
    finishedIndex: IntArray = intArrayOf(fromIndex)
): ListValue {
    check(this[fromIndex] == '[') { "This is not start of the list" }
    var current = fromIndex + 1

    val result = mutableListOf<Value>()
    while (current <= this.lastIndex) {
        when (this[current]) {
            ',' -> ++current
            ']' -> break
            '[' -> {
                val nested = this.parseList(current, finishedIndex)
                result.add(nested)
                current = finishedIndex[0] + 1
            }
            else -> {
                val to = this.indexOfAny(chars = charArrayOf(',', ']'), startIndex = current)

                val intValue = this.substring(current, to).toInt()
                result.add(IntValue(intValue))

                current = if (this[to] == ',') {
                    to + 1
                } else {
                    to
                }
            }
        }
    }

    finishedIndex[0] = current
    return ListValue(result)
}

private fun checkOrder(left: List<Value>, right: List<Value>): Order {
    for (i in left.indices) {
        if (i > right.lastIndex) {
            return Order.WRONG
        }

        val leftItem = left[i]
        val rightItem = right[i]

        if (leftItem is IntValue && rightItem is IntValue) {
            if (leftItem.value < rightItem.value) return Order.RIGHT
            if (leftItem.value > rightItem.value) return Order.WRONG
        } else {
            val result = checkOrder(
                left = if (leftItem is ListValue) leftItem.values else listOf(leftItem),
                right = if (rightItem is ListValue) rightItem.values else listOf(rightItem)
            )
            if (result != Order.UNKNOWN) return result
        }
    }

    return if (left.size < right.size) Order.RIGHT else Order.UNKNOWN
}

private sealed interface Value
private class IntValue(val value: Int) : Value
private class ListValue(val values: List<Value>) : Value

private enum class Order(val value: Int) {
    UNKNOWN(0), RIGHT(-1), WRONG(1)
}