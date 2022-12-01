package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput

/** This challenge is about grouping numbers and applying rules on the sum of each group.
 *
 * It uses a list of lists to represent the different groups in the inventory. This data structure is
 * really simple, and we can use the collections API to sum and sort the groups of integers.
 *
 * @link https://adventofcode.com/2022/day/1
 */


fun main() {
    val inventory: List<List<Int>> = loadInput("01-calorie_counter.txt")
        .split("\n")
        .fold(mutableListOf(mutableListOf())) { result: MutableList<MutableList<Int>>, entry ->
            if (entry.isBlank()) {
                result += mutableListOf<Int>()
            } else {
                result.last() += entry.toInt()
            }
            result
        }

    val maxTotalCalories = inventory.maxOf { entry ->
        entry.sum()
    }
    val topThreeCalories = inventory
        .sortedByDescending { entry -> entry.sum() }
        .take(3)
        .sumOf { entry -> entry.sum() }
    println("Elf carrying most calories has: $maxTotalCalories")
    println("Top three Elves carrying most calories sums a total of: $topThreeCalories")
}
