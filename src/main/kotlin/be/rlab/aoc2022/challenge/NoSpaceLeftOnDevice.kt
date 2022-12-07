package be.rlab.aoc2022.challenge

import be.rlab.aoc2022.support.ResourceUtils.loadInput
import java.util.*

data class FileNode(
    val name: String,
    val size: Long,
) {
    companion object {
        private val parser: Regex = Regex("(\\d+) (.*)")

        fun isFile(line: String): Boolean {
            return parser.matches(line)
        }

        fun parse(line: String): FileNode {
            val values = parser.find(line)?.groupValues ?: throw RuntimeException("invalid file line: $line")
            return FileNode(size = values[1].toLong(), name = values[2])
        }
    }
}

class Shell(var workingDir: String) {
    companion object {
        fun isCommand(line: String): Boolean {
            return line.startsWith("$")
        }
    }

    val fileSystem: SortedMap<String, MutableList<FileNode>> = sortedMapOf(normalizePath(workingDir) to mutableListOf())

    fun exec(commandLine: String): Shell = apply {
        val args = commandLine.trim().split(" ")

        when (args[1]) {
            "cd" -> {
                println("changing dir to: ${args[2]}")
                changeDir(args[2])
            }
            "ls" -> println("listing current directory: $workingDir")
            else -> throw RuntimeException("unknown command: $commandLine")
        }
    }

    fun addFile(file: FileNode): Shell = apply {
        println("adding file: ${file.name}")
        fileSystem[workingDir]?.add(file)
    }

    fun sizeOf(path: String): Long {
        val normalizedPath = normalizePath(path)
        return fileSystem.filterKeys { currentPath ->
            currentPath.startsWith(normalizedPath)
        }.values.sumOf { files ->
            files.sumOf { file -> file.size }
        }
    }

    private fun changeDir(path: String) {
        workingDir = when {
            path.startsWith("/") -> addDirIfNotExists(path)
            path == ".." -> addDirIfNotExists(workingDir.substringBeforeLast("/"))
            else -> addDirIfNotExists("${workingDir.removeSuffix("/")}/$path")
        }
        println("current dir: $workingDir")
    }

    private fun addDirIfNotExists(path: String): String {
        val normalizedPath = normalizePath(path)
        if (!fileSystem.containsKey(normalizedPath)) {
            fileSystem += normalizedPath to mutableListOf()
        }
        return normalizedPath
    }

    private fun normalizePath(path: String): String {
        return when {
            path.isBlank() -> "/"
            path != "/" -> path.removeSuffix("/").trim()
            else -> path
        }
    }
}

fun main() {
    val shell = loadInput("07-no_space_left_on_device.txt")
        .split("\n")
        .fold(Shell(workingDir = "/")) { shell, line ->
            when {
                Shell.isCommand(line) -> shell.exec(line)
                FileNode.isFile(line) -> shell.addFile(FileNode.parse(line))
                else -> shell
            }
        }

    val part1 = shell.fileSystem
        .filterKeys { path -> shell.sizeOf(path) <= 100000 }
        .keys.sumOf { path -> shell.sizeOf(path) }

    val totalSize = shell.sizeOf("/")
    val part2 = shell.fileSystem
        .mapValues { (path) -> shell.sizeOf(path) }
        .entries.sortedBy { (_, size) -> size }
        .find { (_, size) ->
            70000000 - totalSize + size >= 30000000
        } ?: throw RuntimeException("candidate directory not found")

    println("sum of directories with at least 100000 bytes: $part1")
    println("we need to delete ${part2.key} to free up ${part2.value} bytes")
}
