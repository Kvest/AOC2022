fun main() {
    val testInput = readInput("Day22_test")
    val testMap = testInput.subList(0, testInput.size - 2).toMap()
    val testPath = testInput.last().parsePath()
    check(part1(testMap, testPath) == 6032)
    check(part2(testMap, testMap.buildTestCubeAdjacentMap(), testPath) == 5031)

    val input = readInput("Day22")
    val map = input.subList(0, input.size - 2).toMap()
    val path = input.last().parsePath()
    println(part1(map, path))
    println(part2(map, map.buildCubeAdjacentMap(), path))
}

private const val VOID = 0
private const val OPEN = 1
private const val WALL = 2

private const val RIGHT = 0
private const val DOWN = 1
private const val LEFT = 2
private const val UP = 3

private fun part1(map: Matrix, path: List<PathItem>): Int {
    val adjacentMap = map.buildAdjacentMap()
    return solve(map, adjacentMap, path)
}

private fun part2(map: Matrix, adjacentMap: Int3DMatrix, path: List<PathItem>): Int = solve(map, adjacentMap, path)

private fun solve(map: Matrix, adjacentMap: Int3DMatrix, path: List<PathItem>): Int {
    // "You begin the path in the leftmost open tile of the top row of tiles."
    val position = MutableIJ(
        i = 0,
        j = map[0].indexOfFirst { it == OPEN }
    )
    var rotation = RIGHT

    path.forEach { pathItem ->
        when (pathItem) {
            is PathItem.Move -> {
                for (k in 1..pathItem.stepsCount) {
                    val i = adjacentMap[position.i][position.j][rotation * 3]
                    val j = adjacentMap[position.i][position.j][rotation * 3 + 1]

                    if (map[i][j] == WALL) {
                        break
                    }

                    rotation = adjacentMap[position.i][position.j][rotation * 3 + 2]
                    position.i = i
                    position.j = j
                }
            }
            PathItem.RotationLeft -> rotation = if (rotation == RIGHT) UP else rotation - 1
            PathItem.RotationRight -> rotation = if (rotation == UP) RIGHT else rotation + 1
        }
    }

    return 1_000 * (position.i + 1) + 4 * (position.j + 1) + rotation
}

private fun List<String>.toMap(): Matrix {
    val iSize = this.size
    val jSize = this.maxOf { it.length }

    val result = Array(iSize) { i ->
        IntArray(jSize) { j ->
            when (this[i].getOrNull(j)) {
                '.' -> OPEN
                '#' -> WALL
                else -> VOID
            }
        }
    }

    return result
}

private fun Matrix.buildAdjacentMap(): Int3DMatrix {
    return Array(this.size) { i ->
        Array(this[i].size) { j ->
            if (this[i][j] != VOID) {
                var jRight = (j + 1) % this[i].size
                while (this[i][jRight] == VOID) {
                    jRight = (jRight + 1) % this[i].size
                }

                var iDown = (i + 1) % this.size
                while (this[iDown][j] == VOID) {
                    iDown = (iDown + 1) % this.size
                }


                var jLeft = if (j > 0) (j - 1) else this[i].lastIndex
                while (this[i][jLeft] == VOID) {
                    jLeft = if (jLeft > 0) (jLeft - 1) else this[i].lastIndex
                }

                var iUp = if (i > 0) (i - 1) else this.lastIndex
                while (this[iUp][j] == VOID) {
                    iUp = if (iUp > 0) (iUp - 1) else this.lastIndex
                }

                intArrayOf(
                    i, jRight, RIGHT,
                    iDown, j, DOWN,
                    i, jLeft, LEFT,
                    iUp, j, UP
                )
            } else {
                intArrayOf()
            }
        }
    }
}

private fun Matrix.buildCubeAdjacentMap(): Int3DMatrix {
    val result = Array(this.size) { i ->
        Array(this[i].size) { j ->
            if (this[i][j] != VOID) {
                intArrayOf(
                    i, j + 1, RIGHT,
                    i + 1, j, DOWN,
                    i, j - 1, LEFT,
                    i - 1, j, UP
                )
            } else {
                intArrayOf()
            }
        }
    }

    val cubeSize = this.size / 4

    //Link cube edges
    /*
    22221111
    22221111
    22221111
    22221111
    3333
    3333
    3333
    3333
55554444
55554444
55554444
55554444
6666
6666
6666
6666
     */

    //Link 1 <-> 3 edges
    repeat(cubeSize) { delta ->
        result[cubeSize - 1][2 * cubeSize + delta][3 * DOWN] = cubeSize + delta
        result[cubeSize - 1][2 * cubeSize + delta][3 * DOWN + 1] = 2 * cubeSize - 1
        result[cubeSize - 1][2 * cubeSize + delta][3 * DOWN + 2] = LEFT

        result[cubeSize + delta][2 * cubeSize - 1][3 * RIGHT] = cubeSize - 1
        result[cubeSize + delta][2 * cubeSize - 1][3 * RIGHT + 1] = 2 * cubeSize + delta
        result[cubeSize + delta][2 * cubeSize - 1][3 * RIGHT + 2] = UP
    }

    //Link  1 <-> 4 edges
    repeat(cubeSize) { delta ->
        result[cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT] = 2 * cubeSize + delta
        result[cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT + 1] = 2 * cubeSize - 1
        result[cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT + 2] = LEFT

        result[2 * cubeSize + delta][2 * cubeSize - 1][3 * RIGHT] = cubeSize - delta - 1
        result[2 * cubeSize + delta][2 * cubeSize - 1][3 * RIGHT + 1] = 3 * cubeSize - 1
        result[2 * cubeSize + delta][2 * cubeSize - 1][3 * RIGHT + 2] = LEFT
    }

    //Link 1 <-> 6 edges
    repeat(cubeSize) { delta ->
        result[0][2 * cubeSize + delta][3 * UP] = 4 * cubeSize - 1
        result[0][2 * cubeSize + delta][3 * UP + 1] = delta
        result[0][2 * cubeSize + delta][3 * UP + 2] = UP

        result[4 * cubeSize - 1][delta][3 * DOWN] = 0
        result[4 * cubeSize - 1][delta][3 * DOWN + 1] = 2 * cubeSize + delta
        result[4 * cubeSize - 1][delta][3 * DOWN + 2] = DOWN
    }

    //Link 2 <-> 5 edges
    repeat(cubeSize) { delta ->
        result[cubeSize - delta - 1][cubeSize][3 * LEFT] = 2 * cubeSize + delta
        result[cubeSize - delta - 1][cubeSize][3 * LEFT + 1] = 0
        result[cubeSize - delta - 1][cubeSize][3 * LEFT + 2] = RIGHT

        result[2 * cubeSize + delta][0][3 * LEFT] = cubeSize - delta - 1
        result[2 * cubeSize + delta][0][3 * LEFT + 1] = cubeSize
        result[2 * cubeSize + delta][0][3 * LEFT + 2] = RIGHT
    }

    //Link 2 <-> 6 edges
    repeat(cubeSize) { delta ->
        result[0][cubeSize + delta][3 * UP] = 3 * cubeSize + delta
        result[0][cubeSize + delta][3 * UP + 1] = 0
        result[0][cubeSize + delta][3 * UP + 2] = RIGHT

        result[3 * cubeSize + delta][0][3 * LEFT] = 0
        result[3 * cubeSize + delta][0][3 * LEFT + 1] = cubeSize + delta
        result[3 * cubeSize + delta][0][3 * LEFT + 2] = DOWN
    }

    //Link 3 <-> 5 edges
    repeat(cubeSize) { delta ->
        result[2 * cubeSize - delta - 1][cubeSize][3 * LEFT] = 2 * cubeSize
        result[2 * cubeSize - delta - 1][cubeSize][3 * LEFT + 1] = cubeSize - delta - 1
        result[2 * cubeSize - delta - 1][cubeSize][3 * LEFT + 2] = DOWN

        result[2 * cubeSize][cubeSize - delta - 1][3 * UP] = 2 * cubeSize - delta - 1
        result[2 * cubeSize][cubeSize - delta - 1][3 * UP + 1] = cubeSize
        result[2 * cubeSize][cubeSize - delta - 1][3 * UP + 2] = RIGHT
    }

    //Link 4 <-> 6 edges
    repeat(cubeSize) { delta ->
        result[3 * cubeSize - 1][cubeSize + delta][3 * DOWN] = 3 * cubeSize + delta
        result[3 * cubeSize - 1][cubeSize + delta][3 * DOWN + 1] = cubeSize - 1
        result[3 * cubeSize - 1][cubeSize + delta][3 * DOWN + 2] = LEFT

        result[3 * cubeSize + delta][cubeSize - 1][3 * RIGHT] = 3 * cubeSize - 1
        result[3 * cubeSize + delta][cubeSize - 1][3 * RIGHT + 1] = cubeSize + delta
        result[3 * cubeSize + delta][cubeSize - 1][3 * RIGHT + 2] = UP
    }

    return result
}

private fun Matrix.buildTestCubeAdjacentMap(): Int3DMatrix {
    val result = Array(this.size) { i ->
        Array(this[i].size) { j ->
            if (this[i][j] != VOID) {
                intArrayOf(
                    i, j + 1, RIGHT,
                    i + 1, j, DOWN,
                    i, j - 1, LEFT,
                    i - 1, j, UP
                )
            } else {
                intArrayOf()
            }
        }
    }

    val cubeSize = this.size / 3
    val leftTop = cubeSize * 2


    //Link cube edges
    /*
        1111
        1111
        1111
        1111
222233334444
222233334444
222233334444
222233334444
        55556666
        55556666
        55556666
        55556666
     */

    //Link 1 <-> 2
    repeat(cubeSize) { delta ->
        result[0][leftTop + delta][3 * UP] = cubeSize
        result[0][leftTop + delta][3 * UP + 1] = cubeSize - delta - 1
        result[0][leftTop + delta][3 * UP + 2] = DOWN

        result[cubeSize][cubeSize - delta - 1][3 * UP] = 0
        result[cubeSize][cubeSize - delta - 1][3 * UP + 1] = leftTop + delta
        result[cubeSize][cubeSize - delta - 1][3 * UP + 2] = DOWN
    }
    //Link 1 <-> 3
    repeat(cubeSize) { delta ->
        result[delta][leftTop][3 * LEFT] = cubeSize
        result[delta][leftTop][3 * LEFT + 1] = cubeSize + delta
        result[delta][leftTop][3 * LEFT + 2] = DOWN

        result[cubeSize][cubeSize + delta][3 * UP] = delta
        result[cubeSize][cubeSize + delta][3 * UP + 1] = leftTop
        result[cubeSize][cubeSize + delta][3 * UP + 2] = RIGHT
    }
    //Link 1 <-> 6
    repeat(cubeSize) { delta ->
        result[delta][3 * cubeSize - 1][3 * RIGHT] = 3 * cubeSize - delta - 1
        result[delta][3 * cubeSize - 1][3 * RIGHT + 1] = 4 * cubeSize - 1
        result[delta][3 * cubeSize - 1][3 * RIGHT + 2] = LEFT

        result[3 * cubeSize - delta - 1][4 * cubeSize - 1][3 * RIGHT] = delta
        result[3 * cubeSize - delta - 1][4 * cubeSize - 1][3 * RIGHT + 1] = 3 * cubeSize - 1
        result[3 * cubeSize - delta - 1][4 * cubeSize - 1][3 * RIGHT + 2] = LEFT
    }

    //Link 2 <-> 5
    repeat(cubeSize) { delta ->
        result[2 * cubeSize - 1][delta][3 * DOWN] = 3 * cubeSize - 1
        result[2 * cubeSize - 1][delta][3 * DOWN + 1] = 3 * cubeSize - delta - 1
        result[2 * cubeSize - 1][delta][3 * DOWN + 2] = UP

        result[3 * cubeSize - 1][3 * cubeSize - delta - 1][3 * DOWN] = 2 * cubeSize - 1
        result[3 * cubeSize - 1][3 * cubeSize - delta - 1][3 * DOWN + 1] = delta
        result[3 * cubeSize - 1][3 * cubeSize - delta - 1][3 * DOWN + 2] = UP
    }
    //Link 2 <-> 6
    repeat(cubeSize) { delta ->
        result[cubeSize + delta][0][3 * LEFT] = 3 * cubeSize - 1
        result[cubeSize + delta][0][3 * LEFT + 1] = 4 * cubeSize - delta - 1
        result[cubeSize + delta][0][3 * LEFT + 2] = UP

        result[3 * cubeSize - 1][4 * cubeSize - delta - 1][3 * DOWN] = cubeSize + delta
        result[3 * cubeSize - 1][4 * cubeSize - delta - 1][3 * DOWN + 1] = 0
        result[3 * cubeSize - 1][4 * cubeSize - delta - 1][3 * DOWN + 2] = RIGHT
    }

    //Link 3 <-> 5
    repeat(cubeSize) { delta ->
        result[2 * cubeSize - 1][2 * cubeSize - delta - 1][3 * DOWN] = 2 * cubeSize + delta
        result[2 * cubeSize - 1][2 * cubeSize - delta - 1][3 * DOWN + 1] = 2 * cubeSize
        result[2 * cubeSize - 1][2 * cubeSize - delta - 1][3 * DOWN + 2] = RIGHT

        result[2 * cubeSize + delta][2 * cubeSize][3 * LEFT] = 2 * cubeSize - 1
        result[2 * cubeSize + delta][2 * cubeSize][3 * LEFT + 1] = 2 * cubeSize - delta - 1
        result[2 * cubeSize + delta][2 * cubeSize][3 * LEFT + 2] = UP
    }

    //Link 4 <-> 6
    repeat(cubeSize) { delta ->
        result[2 * cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT] = 2 * cubeSize
        result[2 * cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT + 1] = 3 * cubeSize + delta
        result[2 * cubeSize - delta - 1][3 * cubeSize - 1][3 * RIGHT + 2] = DOWN

        result[2 * cubeSize][3 * cubeSize + delta][3 * UP] = 2 * cubeSize - delta - 1
        result[2 * cubeSize][3 * cubeSize + delta][3 * UP + 1] = 3 * cubeSize - 1
        result[2 * cubeSize][3 * cubeSize + delta][3 * UP + 2] = LEFT
    }

    return result
}

private val PATH_FORMAT = Regex("(R|L|\\d+)")
private fun String.parsePath(): List<PathItem> {
    return PATH_FORMAT.findAll(this)
        .map {
            when (it.value) {
                "R" -> PathItem.RotationRight
                "L" -> PathItem.RotationLeft
                else -> PathItem.Move(it.value.toInt())
            }
        }
        .toList()
}

private sealed interface PathItem {
    class Move(val stepsCount: Int) : PathItem
    object RotationRight : PathItem
    object RotationLeft : PathItem
}