import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("Day19_test")
    val testBlueprints = testInput.map(Blueprint.Companion::fromString)
    check(part1(testBlueprints) == 33)

    val input = readInput("Day19")
    val blueprints = input.map(Blueprint.Companion::fromString)
    println(part1(blueprints))
    println(part2(blueprints))
}

private fun part1(blueprints: List<Blueprint>): Int {
    return blueprints.sumOf {
        it.number * calculateMaxGeodes(it, 24)
    }
}

private fun part2(blueprints: List<Blueprint>): Int {
    return calculateMaxGeodes(blueprints[0], 32) *
            calculateMaxGeodes(blueprints[1], 32) *
            calculateMaxGeodes(blueprints[2], 32)
}

private fun calculateMaxGeodes(
    blueprint: Blueprint,
    minutesLeft: Int,
    memo: MutableMap<Long, Int> = mutableMapOf(),
    oreRobotsCount: Int = 1,
    clayRobotsCount: Int = 0,
    obsidianRobotsCount: Int = 0,
    geodesRobotsCount: Int = 0,
    oreCount: Int = 0,
    clayCount: Int = 0,
    obsidianCount: Int = 0,
    geodesCount: Int = 0,
): Int {
    if (minutesLeft == 0) {
        return geodesCount
    }

    //Throw out resources, which can't be potentially spend by the end of the minutesLeft.
    //This reduces a lot of recursive calls because it collapse overlapping states
    val oreForKey = min(oreCount, minutesLeft * blueprint.oreMax)
    val clayForKey = min(clayCount, minutesLeft * blueprint.clayMax)
    val obsidianForKey = min(obsidianCount, minutesLeft * blueprint.obsidianMax)

    val key = minutesLeft * 1_000_000_000_000_000_000L +
            oreRobotsCount * 10_000_000_000_000_000L +
            clayRobotsCount * 100_000_000_000_000L +
            obsidianRobotsCount * 1000_000_000_000L +
            geodesRobotsCount * 10_000_000_000L +
            oreForKey * 100_000_000L +
            clayForKey * 1_000_000L +
            obsidianForKey * 10_000L +
            geodesCount * 100L

    if (key in memo) {
        return memo.getValue(key)
    }

    var result = 0

    val canBuildOreRobot = oreCount >= blueprint.oreForOreRobot && oreRobotsCount < blueprint.oreMax
    val canBuildClayRobot = oreCount >= blueprint.oreForClayRobot && clayRobotsCount < blueprint.clayMax
    val canBuildObsidianRobot =
        oreCount >= blueprint.oreForObsidianRobot && clayCount >= blueprint.clayForObsidianRobot && obsidianCount <= blueprint.obsidianMax
    val canBuildGeodeRobot = oreCount >= blueprint.oreForGeodeRobot && obsidianCount >= blueprint.obsidianForGeodeRobot

    if (canBuildGeodeRobot) {
        result = max(
            result,
            calculateMaxGeodes(
                blueprint = blueprint,
                minutesLeft = minutesLeft - 1,
                memo = memo,
                oreRobotsCount = oreRobotsCount,
                clayRobotsCount = clayRobotsCount,
                obsidianRobotsCount = obsidianRobotsCount,
                geodesRobotsCount = geodesRobotsCount + 1,
                oreCount = oreCount + oreRobotsCount - blueprint.oreForGeodeRobot,
                clayCount = clayCount + clayRobotsCount,
                obsidianCount = obsidianCount + obsidianRobotsCount - blueprint.obsidianForGeodeRobot,
                geodesCount = geodesCount + geodesRobotsCount
            )
        )
    }

    if (canBuildObsidianRobot) {
        result = max(
            result,
            calculateMaxGeodes(
                blueprint = blueprint,
                minutesLeft = minutesLeft - 1,
                memo = memo,
                oreRobotsCount = oreRobotsCount,
                clayRobotsCount = clayRobotsCount,
                obsidianRobotsCount = obsidianRobotsCount + 1,
                geodesRobotsCount = geodesRobotsCount,
                oreCount = oreCount + oreRobotsCount - blueprint.oreForObsidianRobot,
                clayCount = clayCount + clayRobotsCount - blueprint.clayForObsidianRobot,
                obsidianCount = obsidianCount + obsidianRobotsCount,
                geodesCount = geodesCount + geodesRobotsCount
            )
        )
    }

    if (canBuildClayRobot) {
        result = max(
            result,
            calculateMaxGeodes(
                blueprint = blueprint,
                minutesLeft = minutesLeft - 1,
                memo = memo,
                oreRobotsCount = oreRobotsCount,
                clayRobotsCount = clayRobotsCount + 1,
                obsidianRobotsCount = obsidianRobotsCount,
                geodesRobotsCount = geodesRobotsCount,
                oreCount = oreCount + oreRobotsCount - blueprint.oreForClayRobot,
                clayCount = clayCount + clayRobotsCount,
                obsidianCount = obsidianCount + obsidianRobotsCount,
                geodesCount = geodesCount + geodesRobotsCount
            )
        )
    }

    if (canBuildOreRobot) {
        result = max(
            result,
            calculateMaxGeodes(
                blueprint = blueprint,
                minutesLeft = minutesLeft - 1,
                memo = memo,
                oreRobotsCount = oreRobotsCount + 1,
                clayRobotsCount = clayRobotsCount,
                obsidianRobotsCount = obsidianRobotsCount,
                geodesRobotsCount = geodesRobotsCount,
                oreCount = oreCount + oreRobotsCount - blueprint.oreForOreRobot,
                clayCount = clayCount + clayRobotsCount,
                obsidianCount = obsidianCount + obsidianRobotsCount,
                geodesCount = geodesCount + geodesRobotsCount
            )
        )
    }

    result = max(
        result,
        calculateMaxGeodes(
            blueprint = blueprint,
            minutesLeft = minutesLeft - 1,
            memo = memo,
            oreRobotsCount = oreRobotsCount,
            clayRobotsCount = clayRobotsCount,
            obsidianRobotsCount = obsidianRobotsCount,
            geodesRobotsCount = geodesRobotsCount,
            oreCount = oreCount + oreRobotsCount,
            clayCount = clayCount + clayRobotsCount,
            obsidianCount = obsidianCount + obsidianRobotsCount,
            geodesCount = geodesCount + geodesRobotsCount
        )
    )

    memo[key] = result

    return result
}

private val BLUEPRINT_FORMAT =
    Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")

private class Blueprint(
    val number: Int,
    val oreForOreRobot: Int,
    val oreForClayRobot: Int,
    val oreForObsidianRobot: Int,
    val clayForObsidianRobot: Int,
    val oreForGeodeRobot: Int,
    val obsidianForGeodeRobot: Int,
) {
    val oreMax: Int by lazy { maxOf(oreForOreRobot, oreForClayRobot, oreForObsidianRobot, oreForGeodeRobot) }
    val clayMax: Int
        get() = clayForObsidianRobot
    val obsidianMax: Int
        get() = obsidianForGeodeRobot

    companion object {
        fun fromString(str: String): Blueprint {
            val match = BLUEPRINT_FORMAT.find(str)
            val (number, oreForOreRobot, oreForClayRobot, oreForObsidianRobot, clayForObsidianRobot, oreForGeodeRobot, obsidianForGeodeRobot) = requireNotNull(
                match
            ).destructured

            return Blueprint(
                number.toInt(),
                oreForOreRobot.toInt(),
                oreForClayRobot.toInt(),
                oreForObsidianRobot.toInt(),
                clayForObsidianRobot.toInt(),
                oreForGeodeRobot.toInt(),
                obsidianForGeodeRobot.toInt()
            )
        }
    }
}