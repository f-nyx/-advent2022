package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput

data class Stack(
    val crates: ArrayDeque<String>
) {
    fun moveByOneTo(target: Stack, count: Int) {
        repeat(count) {
            target.crates.addFirst(crates.removeFirst())
        }
    }

    fun moveGroupTo(target: Stack, count: Int) {
        (1..count).map { crates.removeFirst() }.reversed().forEach { crate ->
            target.crates.addFirst(crate)
        }
    }
}

data class MoveInstruction(
    val count: Int,
    val from: Int,
    val to: Int
) {
    fun execute9000(stacks: List<Stack>) {
        stacks[from].moveByOneTo(stacks[to], count)
    }

    fun execute9001(stacks: List<Stack>) {
        stacks[from].moveGroupTo(stacks[to], count)
    }
}

fun parseStacks(input: String): List<Stack> {
    val stacksInput: List<String> = input.split("\n")
    val numberOfStacks = stacksInput.last().split(" ").filter { it.isNotBlank() }.size
    val stacks: List<MutableList<String>> = stacksInput.dropLast(1).fold(
        (1..numberOfStacks).map { mutableListOf() }
    ) { stacks, line ->
        line.chunked(4).forEachIndexed { stackIndex, crate ->
            if (crate.isNotBlank()) {
                stacks[stackIndex] += crate.replace(Regex("[\\[\\]]"), "").trim()
            }
        }
        stacks
    }
    return stacks.map { crates -> Stack(crates = ArrayDeque(crates)) }
}

fun parseInstructions(instructionsInput: String): List<MoveInstruction> {
    val parser = Regex("move (\\d+) from (\\d+) to (\\d+)")
    return instructionsInput.split("\n").map { line ->
        val values = parser.find(line)?.groupValues?.toList()?.drop(1)?.map { it.toInt() }
            ?: throw RuntimeException("invalid instruction line: $line")
        MoveInstruction(count = values[0], from = values[1] - 1, to = values[2] - 1)
    }
}

fun part1(stacks: List<Stack>, instructions: List<MoveInstruction>): String {
    instructions.forEach { instruction -> instruction.execute9000(stacks) }
    return stacks.joinToString("") { stack -> stack.crates.first() }
}

fun part2(stacks: List<Stack>, instructions: List<MoveInstruction>): String {
    instructions.forEach { instruction -> instruction.execute9001(stacks) }
    return stacks.joinToString("") { stack -> stack.crates.first() }
}

fun main() {
    val input: List<String> = loadInput("05-supply_stacks.txt").split("\n\n")
    val instructions: List<MoveInstruction> = parseInstructions(input.last())
    val headCreates1: String = part1(parseStacks(input.first()), instructions)
    val headCreates2: String = part2(parseStacks(input.first()), instructions)
    println("head creates (part 1): $headCreates1")
    println("head creates (part 2): $headCreates2")
}
