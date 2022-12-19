import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs

typealias Matrix = Array<IntArray>
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
