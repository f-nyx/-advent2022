package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput

typealias StopCondition = (Int) -> Boolean

data class Tree(
    val x: Int,
    val y: Int,
    val height: Int
)

data class ForestMap(
    val trees: List<Tree>,
    val width: Int,
    val height: Int = trees.size / width
) {

    fun isVisibleFromEdge(tree: Tree): Boolean {
        val stopConditionY: StopCondition = { y -> treeAt(tree.x, y).height < tree.height }
        val stopConditionX: StopCondition = { x -> treeAt(x, tree.y).height < tree.height }

        return (0 until tree.y).all(stopConditionY)          // Checks visibility from top
            || (tree.y + 1 until height).all(stopConditionY) // Checks visibility down to bottom
            || (0 until tree.x).all(stopConditionX)          // Checks visibility from left
            || (tree.x + 1 until width).all(stopConditionX)  // Checks visibility to right
    }

    fun scenicScore(tree: Tree): Int {
        val stopConditionY: StopCondition = { y -> treeAt(tree.x, y).height >= tree.height }
        val stopConditionX: StopCondition = { x -> treeAt(x, tree.y).height >= tree.height }
        val scoreConditions: List<Pair<IntProgression, StopCondition>> = listOf(
            (tree.y - 1 downTo 0) to stopConditionY, // Count up to top
            (tree.y + 1 until height) to stopConditionY, // Count down to bottom
            (tree.x - 1 downTo 0) to stopConditionX, // Count to left
            (tree.x + 1 until width) to stopConditionX // Count to right
        )
        return scoreConditions.fold(1) { totalScore, (progression, condition) ->
            totalScore * countUntil(progression, condition)
        }
    }

    fun subMap(
        width: Int,
        height: Int
    ): ForestMap {
        val treeCount = width * height
        val edgeSize = (this.width - width) / 2
        val trees = (0 until treeCount).map { index ->
            treeAt(
                x = (index % width) + edgeSize,
                y = index / width + edgeSize
            )
        }
        return ForestMap(trees = trees, width = width)
    }

    private fun treeAt(x: Int, y: Int): Tree {
        return trees[y * width + x]
    }

    private fun countUntil(
        progression: IntProgression,
        stopCondition: StopCondition
    ): Int {
        var total = 0
        for (index in progression) {
            if (stopCondition(index)) {
                return total + 1
            } else {
                total += 1
            }
        }
        return total
    }
}

fun main() {
    val input = loadInput("08-treetop_tree_house.txt")
    val width: Int = input.split("\n").first().length
    val trees = input
        .replace("\n", "")
        .toCharArray()
        .map { it.digitToInt() }
        .mapIndexed { index, height ->
            Tree(
                x = index % width,
                y = index / width,
                height = height
            )
        }
    val forestMap = ForestMap(trees = trees, width = width)
    val innerMap = forestMap.subMap(forestMap.width - 2, forestMap.height - 2)

    // Part 1
    val outerTotal = forestMap.trees.size - innerMap.trees.size
    val visible = innerMap.trees.count { tree -> forestMap.isVisibleFromEdge(tree) }
    println("total visible outside the map: ${outerTotal + visible}")

    // Part 2
    val scenicScores = innerMap.trees.map { tree -> forestMap.scenicScore(tree) }.sortedDescending()
    println("best scenic score: ${scenicScores.first()}")
}
