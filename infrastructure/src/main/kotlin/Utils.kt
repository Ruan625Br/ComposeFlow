package io.composeflow

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
fun List<String>.toFilesHexString(): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val ret = this
        .asSequence()
        .map { it ->
            val part = it.split("*")
            if (part.size >= 2) {
                val path = Paths.get(part[0])
                val glob = "glob:${it.replaceFirst(part[0], "")}"
                val matcher = FileSystems.getDefault().getPathMatcher(glob)
                Files.walk(path)
                    .filter { matcher.matches(it) }
                    .toList()
            } else {
                listOf(Paths.get(part[0]))
            }
        }
        .flatten()
        .map { it.toFile() }
        .filter { it.isFile }
        .map {it.readBytes()}
        .reduce { acc, s ->
        digest.update(acc + s)
        digest.digest()
    }.toHexString()
    return ret
}