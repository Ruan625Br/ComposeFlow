package io.composeflow.cloud.storage

import org.http4k.urlEncoded

class Location(
    val bucket: String,
    val path: String
) {
    fun isRoot (): Boolean {
        return path.isEmpty()
    }

    fun bucketOnlyServerUrl(): String {
        return "b/${bucket.urlEncoded()}/o"
    }

    fun filename(): String {
        val split = path.split("/")
        return split.last()
    }

    fun folderName(): String {
        val split = path.split("/")
        return if (split.size >= 2) split[split.size - 2] else ""
    }
}