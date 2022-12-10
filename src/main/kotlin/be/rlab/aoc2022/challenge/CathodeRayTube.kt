package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.challenge.Processor.Companion.SCREEN_WIDTH
import be.rlab.aoc2022.support.ResourceUtils.loadInput

abstract class Op(
    val cycles: Int,
    val args: List<String>
) {
    abstract fun execute(processor: Processor): Processor

    companion object {
        fun parse(line: String): Op {
            val op = line.split(" ")
            val name = op.first()
            val args = op.drop(1)
            return when (name) {
                "noop" -> Noop()
                "addx" -> AddX(args)
                else -> throw RuntimeException("unknown instruction: $line")
            }
        }
    }
}

class AddX(args: List<String>): Op(cycles = 2, args = args) {
    override fun execute(processor: Processor): Processor {
        val operand = args.first().toInt()
        println("addx $operand")
        return processor.writeX(processor.x + operand)
    }
}

class Noop: Op(cycles = 1, args = emptyList()) {
    override fun execute(processor: Processor): Processor = processor.apply {
        println("noop")
    }
}

data class Processor(
    private val snapshotCycles: List<Int>
) {
    companion object {
        const val SCREEN_WIDTH = 40
    }

    var x: Int = 1
        private set
    private var cycle: Int = 0
    val snapshots: MutableMap<Int, Int> = mutableMapOf()
    val screen: Array<Char> = Array(240) { '.' }

    fun writeX(value: Int): Processor = apply {
        x = value
    }

    fun execute(ops: List<Op>): Processor = apply {
        val queue = ops.toMutableList()

        while (queue.isNotEmpty()) {
            val op = queue.removeFirst()
            repeat(op.cycles) {
                val horizontalPosition = (cycle % SCREEN_WIDTH)
                val sprite = (x - 1 .. x + 1)
                if (horizontalPosition in sprite) {
                    screen[cycle] = '#'
                }
                cycle += 1
                if (cycle in snapshotCycles) {
                    snapshots[cycle] = cycle * x
                }
            }
            op.execute(this)
        }
    }
}

fun main() {
    val ops = loadInput("10-cathode_ray_tube.txt").split("\n").map { line -> Op.parse(line) }
    val processor = Processor(snapshotCycles = listOf(20, 60, 100, 140, 180, 220)).execute(ops)
    println("Sum of snapshots: ${processor.snapshots.values.sum()}")

    println("CODE:")
    println(
        processor.screen.toList()
            .chunked(SCREEN_WIDTH)
            .joinToString("\n") { horizontalLine ->
                horizontalLine.joinToString("")
            }
    )
}
