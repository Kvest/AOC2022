fun main() {
    val testInput = readInput("Day21_test")
    val testOperations = testInput.toOperations()
    check(part1(testOperations) == 152L)
    check(part2(testOperations) == 301L)

    val input = readInput("Day21")
    val operations = input.toOperations()
    println(part1(operations))
    println(part2(operations))
}

private fun part1(operations: Map<String, MathOperation>): Long = getValue("root", operations)

private const val TARGET_OPERATION = "humn"
private fun part2(operations: Map<String, MathOperation>): Long {
    val rootOp = operations.getValue("root")
    val operand1Depends = dependsOn(rootOp.operand1, TARGET_OPERATION, operations)

    val targetValue = getValue(
        name = if (operand1Depends) rootOp.operand2 else rootOp.operand1,
        operations = operations
    )

    return correctToValue(
        name = if (operand1Depends) rootOp.operand1 else rootOp.operand2,
        targetValue = targetValue,
        targetOperationName = TARGET_OPERATION,
        operations = operations
    )
}

private fun correctToValue(
    name: String,
    targetValue: Long,
    targetOperationName: String,
    operations: Map<String, MathOperation>
): Long {
    if (name == targetOperationName) {
        return targetValue
    }

    val op = operations.getValue(name)
    val operand1Depends = dependsOn(op.operand1, TARGET_OPERATION, operations)
    val anotherOperandValue = getValue(
        name = if (operand1Depends) op.operand2 else op.operand1,
        operations = operations
    )
    val nextName = if (operand1Depends) op.operand1 else op.operand2

    val nextTargetValue = when (op) {
        is MathOperation.Divide ->
            if (operand1Depends) {
                targetValue * anotherOperandValue
            } else {
                anotherOperandValue / targetValue
            }
        is MathOperation.Minus ->
            if (operand1Depends) {
                targetValue + anotherOperandValue
            } else {
                anotherOperandValue - targetValue
            }
        is MathOperation.Multiply -> targetValue / anotherOperandValue
        is MathOperation.Plus -> targetValue - anotherOperandValue
        is MathOperation.Value -> error("Should not be here")
    }

    return correctToValue(nextName, nextTargetValue, targetOperationName, operations)
}

private fun dependsOn(
    operationName: String,
    targetDependency: String,
    operations: Map<String, MathOperation>
): Boolean {
    if (operationName == targetDependency) {
        return true
    }

    return when (val op = operations.getValue(operationName)) {
        is MathOperation.Value -> false
        else ->
            dependsOn(op.operand1, targetDependency, operations) || dependsOn(op.operand2, targetDependency, operations)
    }
}

private fun getValue(name: String, operations: Map<String, MathOperation>): Long {
    return when (val op = operations.getValue(name)) {
        is MathOperation.Divide -> getValue(op.operand1, operations) / getValue(op.operand2, operations)
        is MathOperation.Minus -> getValue(op.operand1, operations) - getValue(op.operand2, operations)
        is MathOperation.Multiply -> getValue(op.operand1, operations) * getValue(op.operand2, operations)
        is MathOperation.Plus -> getValue(op.operand1, operations) + getValue(op.operand2, operations)
        is MathOperation.Value -> op.value
    }
}

private fun List<String>.toOperations(): Map<String, MathOperation> = this.associate { row ->
    val name = row.substringBefore(":")
    val operationStr = row.substringAfter(": ")
    val operation = when {
        operationStr.contains('+') ->
            MathOperation.Plus(
                operand1 = operationStr.substringBefore(" + "),
                operand2 = operationStr.substringAfter(" + ")
            )
        operationStr.contains('-') ->
            MathOperation.Minus(
                operand1 = operationStr.substringBefore(" - "),
                operand2 = operationStr.substringAfter(" - ")
            )
        operationStr.contains('/') ->
            MathOperation.Divide(
                operand1 = operationStr.substringBefore(" / "),
                operand2 = operationStr.substringAfter(" / ")
            )
        operationStr.contains('*') ->
            MathOperation.Multiply(
                operand1 = operationStr.substringBefore(" * "),
                operand2 = operationStr.substringAfter(" * ")
            )
        else -> MathOperation.Value(operationStr.toLong())
    }

    name to operation
}

private sealed interface MathOperation {
    class Value(val value: Long) : MathOperation
    class Plus(val operand1: String, val operand2: String) : MathOperation
    class Minus(val operand1: String, val operand2: String) : MathOperation
    class Multiply(val operand1: String, val operand2: String) : MathOperation
    class Divide(val operand1: String, val operand2: String) : MathOperation
}

private val MathOperation.operand1: String
    get() = when (this) {
        is MathOperation.Divide -> this.operand1
        is MathOperation.Minus -> this.operand1
        is MathOperation.Multiply -> this.operand1
        is MathOperation.Plus -> this.operand1
        is MathOperation.Value -> error("MathOperation.Value doesn't have operands")
    }

private val MathOperation.operand2: String
    get() = when (this) {
        is MathOperation.Divide -> this.operand2
        is MathOperation.Minus -> this.operand2
        is MathOperation.Multiply -> this.operand2
        is MathOperation.Plus -> this.operand2
        is MathOperation.Value -> error("MathOperation.Value doesn't have operands")
    }