package be.rlab.aoc2022.challenge

/** This is the classic Rock-Paper-Scissors game with some specific rules.
 *
 * I think the key part of the challenge is how to model the problem in a way it can be used
 * for the two parts. I used an enum to model the Shapes (rock, paper and scissor), and another
 * enum to model the Result (lose, draw and win).
 *
 * Each Shape has a weight that represents which one wins against the other. At the same time, each Result know what's
 * the outcome of a match between two Shapes. A match result is the difference between Shape's weights, and all the
 * combinations are cached/stored in the matchResults field. So, in order to resolve a Result, it just needs to
 * subtract weights and search for the enum element containing the value in the matchResults field.
 */

import be.rlab.aoc2022.support.ResourceUtils.loadInput

enum class Shape(
    private val codes: List<String>,
    val weight: Int
) {
    ROCK(codes = listOf("A", "X"), weight = 1),
    PAPER(codes = listOf("B", "Y"), weight = 2),
    SCISSORS(codes = listOf("C", "Z"), weight = 3);

    companion object {
        fun fromCode(code: String): Shape {
            return values().find { move ->
                move.codes.contains(code)
            }!!
        }
    }
}

/**
 * Match results are a reduction of this table:
 * rock - rock = 0            DRAW
 * rock - paper = -1          LOSE
 * rock - scissors = -2       WIN
 * paper - rock = 1           WIN
 * paper - paper = 0          DRAW
 * paper - scissors = -1      LOSE
 * scissors - rock = 2        LOSE
 * scissors - paper = 1       WIN
 * scissors - scissors = 0    DRAW
 */
enum class Result(
    private val matchResults: List<Int>,
    val code: String,
    val score: Int
) {
    LOSE(matchResults = listOf(-1, 2), code = "X", score = 0),
    DRAW(matchResults = listOf(0), code = "Y", score = 3),
    WIN(matchResults = listOf(1, -2), code = "Z", score = 6);

    companion object {
        fun fromCode(code: String): Result {
            return Result.values().find { move ->
                move.code.contains(code)
            }!!
        }

        fun match(shape: Shape, other: Shape): Result {
            val value = other.weight - shape.weight
            return values().find { result ->
                result.matchResults.contains(value)
            }!!
        }
    }
}

fun matchPart1(input: List<Pair<Shape, String>>): Int {
    val totalScore: Int = input.sumOf { match ->
        val human = Shape.fromCode(match.second)
        val score: Int = Result.match(match.first, human).score
        human.weight + score
    }

    return totalScore
}

fun matchPart2(input: List<Pair<Shape, String>>): Int {
    val totalScore: Int = input.sumOf { match ->
        val expectedResult = Result.fromCode(match.second)
        val human = Shape.values().find { move ->
            Result.match(match.first, move) == expectedResult
        }!!

        human.weight + expectedResult.score
    }

    return totalScore
}

fun main() {
    val input: List<Pair<Shape, String>> = loadInput("01-rock_paper_scissors.txt")
        .split("\n")
        .filter { line -> line.isNotBlank() }
        .map { match ->
            val moves = match.split(" ")
            Pair(Shape.fromCode(moves.first().trim()), moves.last().trim())
        }
    println("Your total score (part1): ${matchPart1(input)}")
    println("Your total score (part2): ${matchPart2(input)}")
}
