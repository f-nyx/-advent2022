package be.rlab.aoc2022.support

import java.io.InputStream

object ResourceUtils {
    fun loadInput(name: String): String {
        return Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("input/$name")?.bufferedReader()?.readText()
            ?: throw RuntimeException("Cannot load input: $name")
    }

    fun loadAsStream(name: String): InputStream {
        return Thread.currentThread().contextClassLoader.getResourceAsStream("input/$name")
                ?: throw RuntimeException("Cannot load input: $name")
    }
}
