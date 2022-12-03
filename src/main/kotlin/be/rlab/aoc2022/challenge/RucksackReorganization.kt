package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput

data class Item(private val id: Char) {
    companion object {
        fun sharedItems(items: List<Rucksack>): Set<Item> {
            return items.fold(items.first().items.toSet()) { shared, rucksack ->
                rucksack.items.intersect(shared)
            }
        }
    }

    fun priority(): Int {
        return when (id.code) {
            in 97..122 -> id.code - 96
            else -> id.code - 38
        }
    }
}

data class Rucksack(val items: List<Item>) {
    val sharedItems: Set<Item> by lazy {
        val firstCompartment = Rucksack(items.take(items.size / 2))
        val secondCompartment = Rucksack(items.drop(items.size / 2))
        Item.sharedItems(listOf(firstCompartment, secondCompartment))
    }
}

fun findRucksackErrors(rucksacks: List<Rucksack>): Int {
    return rucksacks.fold(0) { result, rucksack ->
        result + rucksack.sharedItems.first().priority()
    }
}

fun findBadges(rucksacks: List<Rucksack>): Int {
    return rucksacks.chunked(3).fold(0) { result, elvesGroupRucksacks ->
        val sharedItems: Set<Item> = Item.sharedItems(elvesGroupRucksacks)
        result + sharedItems.first().priority()
    }
}

fun main() {
    val rucksacks: List<Rucksack> = loadInput("03-rucksack_reorganization.txt")
        .split("\n")
        .map { line ->
            Rucksack(items = line.toCharArray().map { item -> Item(item) })
        }
    println("Result of finding errors (part1): ${findRucksackErrors(rucksacks)}")
    println("Result of finding badges (part2): ${findBadges(rucksacks)}")
}
