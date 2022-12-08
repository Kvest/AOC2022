fun main() {
    val testInput = readInput("Day08_test").toMatrix()
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08").toMatrix()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: Matrix): Int {
    val width = input[0].size
    val height = input.size
    var result = height * 2 + width * 2 - 4
    val used = Array(height) { BooleanArray(width) { false } }
    val row = IntArray(width) { input[0][it] }
    val column = IntArray(height) { input[it][0] }

    fun updateResult(i: Int, j: Int) {
        if (!used[i][j]) {
            ++result
            used[i][j] = true
        }
    }

    fun checkItem(i: Int, j: Int) {
        if (input[i][j] > column[i]) {
            updateResult(i, j)
            column[i] = input[i][j]
        }

        if (input[i][j] > row[j]) {
            updateResult(i, j)
            row[j] = input[i][j]
        }
    }

    //top-to-bottom, left-to-right pass
    for (i in 1..(height - 2)) {
        for (j in 1..(width - 2)) {
            checkItem(i, j)
        }
    }

    for (i in 1..(height - 2)) {
        column[i] = input[i][width - 1]
    }
    for (j in 1..(width - 2)) {
        row[j] = input[height - 1][j]
    }

    //bottom-to-top, right-to-left pass
    for (i in (height - 2) downTo 1) {
        for (j in (width - 2) downTo 1) {
            checkItem(i, j)
        }
    }

    return result
}

private fun part2(input: Matrix): Int {
    var result = 0
    val width = input[0].size
    val height = input.size

    for (i in 1..(height - 2)) {
        for (j in 1..(width - 2)) {
            val digit = input[i][j]

            var top = i - 1
            while (top > 0 && digit > input[top][j]) {
                --top
            }

            var left = j - 1
            while (left > 0 && digit > input[i][left]) {
                --left
            }

            var right = j + 1
            while (right < width - 1 && digit > input[i][right]) {
                ++right
            }

            var bottom = i + 1
            while (bottom < height - 1 && digit > input[bottom][j]) {
                ++bottom
            }

            val mul = (i - top) * (j - left) * (right - j) * (bottom - i)

            if (mul > result) {
                result = mul
            }
        }
    }

    return result
}

private fun List<String>.toMatrix(): Matrix {
    return Array(this.size) { i ->
        IntArray(this[i].length) { j ->
            this[i][j].digitToInt()
        }
    }
}