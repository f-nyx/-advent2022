package be.rlab.aoc2022.support

object Math {
    fun lcm(a: Int, b: Int): Int {
        return (a * b) / gcd(a, b)
    }

    fun gcd(a: Int, b: Int): Int {
        if (a == 0) return b
        return gcd(b % a, a)
    }
}
