package io.composeflow

sealed interface VersionAskedToUpdate {

    /**
     * The state where the version isn't not read from the local disk
     */
    data object NotReady : VersionAskedToUpdate

    /**
     * The state where the version is read from the local disk.
     * The version may be null, in that case no version isn't stored in the disk
     */
    data class Ready(val version: String?) : VersionAskedToUpdate
}