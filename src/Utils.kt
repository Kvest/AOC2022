import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs

typealias Matrix = Array<IntArray>
typealias BooleanMatrix = Array<BooleanArray>
typealias Int3DMatrix = Array<Array<IntArray>>
typealias Boolean3DMatrix = Array<Array<BooleanArray>>

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

operator fun IntRange.contains(other: IntRange): Boolean = first <= other.first && other.last <= last
val IntRange.size
    get() = abs(this.last - this.first) + 1

data class XY(val x: Int, val y: Int)
data class MutableXY(var x: Int, var y: Int) {
    fun toXY() = XY(x = x, y = y)
}

data class IJ(val i: Int, val j: Int)
data class MutableIJ(var i: Int, var j: Int) {
    fun toIJ() = IJ(i = i, j = j)
}

fun BooleanMatrix.deepCopyOf(): BooleanMatrix = Array(this.size) { i -> this[i].copyOf() }

data class Item(val steps: Int, val ij: IJ) : Comparable<Item> {
    override fun compareTo(other: Item): Int {
        return this.steps - other.steps
    }
}

/**
 * Create all possible combinations from the list's elements
 */
fun <T> List<T>.allCombinations(): List<List<T>> {
    val results = mutableListOf<List<T>>()

    repeat(this.size + 1) { count ->
        combine(data = ArrayList(count), results, start = 0, index = 0, count = count)
    }

    return results
}

/**
 * Create all possible combinations with size "count " from the list's elements
 * @param count - count of the elements in each combination
 */
fun <T> List<T>.combinations(count: Int): List<List<T>> {
    val results = mutableListOf<List<T>>()

    combine(data = ArrayList(count), results, start = 0, index = 0, count = count)

    return results
}

private fun <T> List<T>.combine(
    data: ArrayList<T>,
    results: MutableList<List<T>>,
    start: Int,
    index: Int,
    count: Int,
) {
    // Current combination is ready to be added to the result
    if (index == count) {
        results.add(ArrayList(data))
        return
    }

    // replace index with all possible elements. The condition
    // "this.lastIndex - i + 1 >= (count - 1) - index" makes sure that including one element
    // at index will make a combination with remaining elements
    // at remaining positions
    var i = start
    while (i <= this.lastIndex && (this.lastIndex - i + 1 >= (count - 1) - index)) {
        if (data.lastIndex < index) {
            data.add(this[i])
        } else {
            data[index] = this[i]
        }

        combine(data, results, start = i + 1, index = index + 1, count = count)
        i++
    }
}
