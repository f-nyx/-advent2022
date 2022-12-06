package be.rlab.aoc2022.challenge

/** This challenge is about finding a "marker" character in a data stream.
 *
 * This implementation uses a window to read the input as a stream of data. The window
 * will move from left to right, and according to the challenge rule, when the data within
 * the window contains all unique characters, the next character represents a marker.
 *
 * The challenge result is the marker position in the input. This solution just looks for the index of
 * the first occurrence of the matching window in the input, and then it sums the window size since the
 * marker is the first character after the window.
 */

import be.rlab.aoc2022.support.ResourceUtils.loadInput

fun findDataStreamMarker(
    input: String,
    windowSize: Int
): Int {
    val marker = input.windowed(windowSize).indexOfFirst { packet -> packet.toSet().size == windowSize }
    return marker + windowSize
}

fun main() {
    val input = loadInput("06-tuning_trouble.txt")
    val startOfPacketMarker = findDataStreamMarker(input, 4)
    val startOfMessageMarker = findDataStreamMarker(input, 14)
    println("start of packet marker (part 1): $startOfPacketMarker")
    println("start of message marker (part 2): $startOfMessageMarker")
}
