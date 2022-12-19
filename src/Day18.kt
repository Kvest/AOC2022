import kotlin.math.max

fun main() {
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val cubes = input.to3DMatrix()

    return solve(input, cubes)
}

private fun part2(input: List<String>): Int {
    val cubes = input.to3DMatrix()
    val water = fillWithWater(cubes)

    return solve(input, water)
}

private fun solve(input: List<String>, emptyCubes: Boolean3DMatrix): Int {
    return input.sumOf {
        var cnt = 0

        val (xStr,yStr,zStr) = it.split(",")
        val x = xStr.toInt()
        val y = yStr.toInt()
        val z = zStr.toInt()

        if (emptyCubes.isEmpty(x + 1, y, z)) cnt++
        if (emptyCubes.isEmpty(x - 1, y, z)) cnt++
        if (emptyCubes.isEmpty(x, y + 1, z)) cnt++
        if (emptyCubes.isEmpty(x, y - 1, z)) cnt++
        if (emptyCubes.isEmpty(x, y, z + 1)) cnt++
        if (emptyCubes.isEmpty(x, y, z - 1)) cnt++

        cnt
    }
}

private fun List<String>.to3DMatrix(): Boolean3DMatrix {
    var xMax = 0
    var yMax = 0
    var zMax = 0

    this.forEach {
        val (x,y,z) = it.split(",")
        xMax = max(xMax, x.toInt())
        yMax = max(yMax, y.toInt())
        zMax = max(zMax, z.toInt())
    }

    val result = Array(xMax + 1) {
        Array(yMax + 1) {
            BooleanArray(zMax + 1)
        }
    }

    this.forEach {
        val (x, y, z) = it.split(",")
        result[x.toInt()][y.toInt()][z.toInt()] = true
    }

    return result
}

private fun fillWithWater(cubes: Boolean3DMatrix): Boolean3DMatrix {
    val result = Array(cubes.size) {
        Array(cubes[0].size) {
            BooleanArray(cubes[0][0].size) { true }
        }
    }

    val xLastIndex = cubes.lastIndex
    val yLastIndex = cubes[0].lastIndex
    val zLastIndex = cubes[0][0].lastIndex

    for (x in cubes.indices) {
        for (y in cubes[0].indices) {
            fill(cubes, result, x, y, 0)
            fill(cubes, result, x, y, zLastIndex)
        }
    }

    for (x in cubes.indices) {
        for (z in cubes[0][0].indices) {
            fill(cubes, result, x, 0, z)
            fill(cubes, result, x, yLastIndex, z)
        }
    }
    for (y in cubes[0].indices) {
        for (z in cubes[0][0].indices) {
            fill(cubes, result, 0, y, z)
            fill(cubes, result, xLastIndex, y, z)
        }
    }

    return result
}

private fun fill(cubes: Boolean3DMatrix, water: Boolean3DMatrix, x: Int, y: Int, z: Int) {
    if (!water.isXYZInside(x, y, z) || cubes[x][y][z] || !water[x][y][z]) {
        return
    }

    water[x][y][z] = false
    fill(cubes, water, x + 1, y, z)
    fill(cubes, water, x - 1, y, z)
    fill(cubes, water, x, y + 1, z)
    fill(cubes, water, x, y - 1, z)
    fill(cubes, water, x, y, z + 1)
    fill(cubes, water, x, y, z - 1)
}


private fun Boolean3DMatrix.isEmpty(x: Int, y: Int, z: Int): Boolean {
    return if (this.isXYZInside(x, y, z)) {
        !this[x][y][z]
    } else {
        true
    }
}

private fun Boolean3DMatrix.isXYZInside(x: Int, y: Int, z: Int): Boolean {
    return (x in this.indices && y in this[x].indices && z in this[x][y].indices)
}