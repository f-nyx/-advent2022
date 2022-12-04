package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput

data class CampSection(
    val assignments: IntRange
) {
    fun contains(section: CampSection): Boolean {
        return section.assignments.all { assignments.contains(it) }
    }

    fun overlaps(section: CampSection): Boolean {
        return section.assignments.any { assignments.contains(it) }
    }
}

fun main() {
    val sections: List<Pair<CampSection, CampSection>> = loadInput("04-camp_cleanup.txt")
        .split("\n")
        .map { line ->
            // Extracts all numbers from the line.
            val rangesValues = line.split(",").flatMap { it.split("-") }.map { it.toInt() }
            CampSection(
                assignments = IntRange(rangesValues[0], rangesValues[1])
            ) to CampSection(
                assignments = IntRange(rangesValues[2], rangesValues[3])
            )
        }
    val containing = sections.filter { (section1, section2) ->
        section1.contains(section2) || section2.contains(section1)
    }
    val overlapping = sections.filter { (section1, section2) ->
        section1.overlaps(section2) || section2.overlaps(section1)
    }

    println("total containing: ${containing.size}")
    println("total overlapping: ${overlapping.size}")
}
