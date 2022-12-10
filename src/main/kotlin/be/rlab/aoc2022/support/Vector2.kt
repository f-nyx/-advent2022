package be.rlab.aoc2022.support

import kotlin.math.sqrt

/** Represents a 2D vector in a plane.
 */
data class Vector2(
    val x: Double,
    val y: Double
) {
    /** Vector length, calculated using pythagoras. */
    val length: Double = sqrt(x * x + y * y)

    /** Returns the point representation of this vector.
     */
    fun toPoint(): Point {
        return Point(x, y)
    }

    /** Returns the distance between this vector and another vector.
     * @param other Target vector.
     */
    fun distanceTo(other: Vector2): Double {
        return (other - this).length
    }

    operator fun plus(vector: Vector2): Vector2 {
        return copy(
            x = x + vector.x,
            y = y + vector.y
        )
    }

    operator fun minus(vector: Vector2): Vector2 {
        return copy(
            x = x - vector.x,
            y = y - vector.y
        )
    }
}
