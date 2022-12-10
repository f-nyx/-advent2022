/** Day 9: Rope Bridge
 *
 * This challenge simulates the classic [Snake game](https://en.wikipedia.org/wiki/Snake_(video_game_genre)),
 * but instead of a snake, it uses a rope. Both part 1 and part 2 require to count how many times the tail of the
 * rope visited a position at least once.
 *
 * This solution uses vectors and some basic linear algebra to calculate the rope movement. The rope is modeled
 * as a linked list of knots. Each knot is a vector, and it has a reference to the previous knot. The first knot
 * in the list is the head of the rope, and the last knot is the tail.
 *
 * On each step of the simulation, we move the head of the rope according to the instruction, and then the head
 * recursively moves all the knots until the tail. Each knot calculates the distance between the next knot and itself,
 * and if the distance is greater than 2, it moves towards the next knot in the same direction as the head.
 *
 * All data structures are immutable to avoid having unexpected side effects.
 *
 * Watch this video for a good introduction to game programming and vectors: https://www.youtube.com/watch?v=Xq2NfPS29oE
 */

package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.Point
import be.rlab.aoc2022.support.ResourceUtils.loadInput
import be.rlab.aoc2022.support.Vector2
import kotlin.math.floor

enum class MoveDirection(val code: String) {
    LEFT(code = "L"),
    RIGHT(code = "R"),
    UP(code = "U"),
    DOWN(code = "D");
    companion object {
        fun fromCode(code: String): MoveDirection {
            return values().first { value -> value.code == code }
        }
    }
}

data class Knot(
    val position: Vector2,
    val previous: Knot?,
    val history: List<Point> = listOf(position.toPoint())
) {
    fun move(direction: MoveDirection, steps: Int): Knot {
        val next = when(direction) {
            MoveDirection.LEFT -> position.copy(x = position.x - steps)
            MoveDirection.RIGHT -> position.copy(x = position.x + steps)
            MoveDirection.UP -> position.copy(y = position.y - steps)
            MoveDirection.DOWN -> position.copy(y = position.y + steps)
        }
        return copy(
            position = next,
            previous = previous?.follow(next)
        )
    }

    private fun follow(next: Vector2): Knot {
        val distance = floor(position.distanceTo(next))
        return if (distance >= 2.0) {
            val direction = next - position
            val nextPosition = Vector2(
                x = when {
                    direction.x > 0 -> position.x + 1
                    direction.x < 0 -> position.x - 1
                    else -> position.x
                },
                y = when {
                    direction.y > 0 -> position.y + 1
                    direction.y < 0 -> position.y - 1
                    else -> position.y
                }
            )
            copy(
                position = nextPosition,
                previous = previous?.follow(nextPosition),
                history = history + nextPosition.toPoint()
            )
        } else {
            this
        }
    }
}

data class Rope(val head: Knot) {
    companion object {
        fun new(numberOfKnots: Int): Rope {
            val start = Vector2(x = 0.0, y = 0.0)
            return Rope(
                head = (1 until numberOfKnots).fold(
                    Knot(position = start, previous = null)
                ) { previous, _ ->
                    Knot(position = start, previous = previous)
                }
            )
        }
    }

    fun move(direction: MoveDirection, steps: Int): Rope {
        return (0 until steps).fold(this) { rope, _ ->
            val nextHead = rope.head.move(direction, 1)
            rope.copy(head = nextHead)
        }
    }

    fun tail(): Knot {
        var tail = head
        while (tail.previous != null) {
            tail = tail.previous!!
        }
        return tail
    }
}

fun simulate(
    instructions: List<String>,
    numberOfKnots: Int
): Rope {
    return instructions.fold(Rope.new(numberOfKnots)) { rope, instruction ->
        val args = instruction.split(" ")
        rope.move(
            direction = MoveDirection.fromCode(args[0]),
            steps = args[1].toInt()
        )
    }
}

fun main() {
    val instructions = loadInput("09-rope_bridge.txt").split("\n")
    val firstRope = simulate(instructions, 2)
    println(firstRope.tail().history.distinct())

    val secondRope = simulate(instructions, 10)
    println(secondRope.tail().history.distinct())
}
