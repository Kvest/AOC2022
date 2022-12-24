fun main() {
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long = solve(input, String::toLong, mixesCount = 1)
private fun part2(input: List<String>): Long = solve(input, String::part2Converter, mixesCount = 10)

private fun String.part2Converter(): Long = this.toLong() * 811589153L

private fun solve(input: List<String>, converter: String.() -> Long, mixesCount: Int): Long {
    val nodes = input.map { LinkedNode(it.converter()) }

    var zeroIndex = -1
    for (i in nodes.indices) {
        nodes[i].next = if (i < nodes.lastIndex) nodes[i + 1] else nodes.first()
        nodes[i].prev = if (i > 0) nodes[i - 1] else nodes.last()

        if (nodes[i].value == 0L) {
            zeroIndex = i
        }
    }

    repeat(mixesCount) {
        nodes.forEach { node ->
            val count = correctMovesCount(node.value, nodes.size)

            if (count > 0) {
                var target = node
                repeat(count) {
                    target = target.next
                }

                //delete node from chain
                node.prev.next = node.next
                node.next.prev = node.prev

                //insert node to the new position
                node.next = target.next
                target.next.prev = node
                target.next = node
                node.prev = target
            }
        }
    }

    var node = nodes[zeroIndex]
    var result = 0L
    repeat(3_001) {
        if (it == 1_000 || it == 2_000 || it == 3_000) {
            result += node.value
        }

        node = node.next
    }

    return result
}

private fun correctMovesCount(count: Long, size: Int): Int {
    return if (count > 0) {
        //reduce the full cycles
        (count % (size - 1)).toInt()
    } else {
        //(size + (count % (size - 1)) - 1) - this converts negative steps to positive steps
        // % (size - 1) - this reduce the full cycles when the steps count is more than the items in the chain
        ((size + (count % (size - 1)) - 1) % (size - 1)).toInt()
    }
}

private class LinkedNode(val value: Long) {
    lateinit var next: LinkedNode
    lateinit var prev: LinkedNode
}