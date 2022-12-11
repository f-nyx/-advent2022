package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput
import be.rlab.aoc2022.support.Math.lcm

data class Turn(
    val item: Long,
    val targetMonkey: Int
)

data class Operation(val expression: String) {
    private val operand: Int by lazy {
        expression.substringAfter("*").substringAfter("+").trim().toInt()
    }

    fun exec(worryLevel: Long): Long {
        return when {
            expression.contains("old + old") -> worryLevel * 2
            expression.contains("old * old") -> worryLevel * worryLevel
            expression.contains("old *") -> worryLevel * operand
            expression.contains("old +") -> worryLevel + operand
            else -> throw RuntimeException("unknown expression: $expression")
        }
    }
}

data class Monkey(
    val items: MutableList<Long>,
    val operation: Operation,
    val testDivisor: Int,
    val trueTarget: Int,
    val falseTarget: Int,
    var inspectCount: Long = 0
) {
    fun give(item: Long) {
        items += item
    }

    fun throwNext(
        lcm: Int,
        reliefFactor: Int
    ): Turn {
        val item = items.removeFirst()
        val worryLevel: Long = (operation.exec(item) / reliefFactor.toLong()) % lcm
        inspectCount += 1

        return Turn(
            item = worryLevel,
            targetMonkey = if (worryLevel % testDivisor == 0L)
                trueTarget
            else
                falseTarget
        )
    }
}

data class MonkeyGame(
    val monkeys: List<Monkey>
) {
    private val lcm = monkeys
        .map { monkey -> monkey.testDivisor }
        .reduce { result, divisor ->
            lcm(result, divisor)
        }

    fun nextRound(reliefFactor: Int): MonkeyGame = apply {
        monkeys.forEach { monkey ->
            repeat(monkey.items.size) {
                val turn = monkey.throwNext(lcm, reliefFactor)
                monkeys[turn.targetMonkey].give(turn.item)
            }
        }
    }

    fun inspectionsScore(): Long {
        val inspections = monkeys.map { monkey -> monkey.inspectCount }.sortedDescending()
        return inspections[0] * inspections[1]
    }
}

fun parseMonkeys(input: String): List<Monkey> {
    return input.split("\n\n").map { monkey ->
        val options = monkey.split("\n")
        Monkey(
            items = options[1].substringAfter(":")
                .split(",")
                .map { item -> item.trim().toLong() }
                .toMutableList(),
            operation = Operation(options[2].substringAfter(":")),
            testDivisor = options[3].substringAfter("divisible by").trim().toInt(),
            trueTarget = options[4].substringAfter("throw to monkey").trim().toInt(),
            falseTarget = options[5].substringAfter("throw to monkey").trim().toInt(),
        )
    }
}

fun main() {
    val input: String = loadInput("11-monkey_in_the_middle.txt")

    val part1 = (0 until 20).fold(
        MonkeyGame(monkeys = parseMonkeys(input))
    ) { simulation, _ ->
        simulation.nextRound(reliefFactor = 3)
    }
    println("part 1 inspection score: ${part1.inspectionsScore()}")

    val part2 = (0 until 10000).fold(
        MonkeyGame(monkeys = parseMonkeys(input))
    ) { simulation, _ ->
        simulation.nextRound(reliefFactor = 1)
    }
    println("part 2 inspection score: ${part2.inspectionsScore()}")
}
