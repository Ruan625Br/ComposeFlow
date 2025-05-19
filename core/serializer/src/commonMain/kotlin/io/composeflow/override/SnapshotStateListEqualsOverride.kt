package io.composeflow.override

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.StateFactoryMarker
import androidx.compose.runtime.snapshots.StateObject
import androidx.compose.runtime.snapshots.StateRecord

/**
 * Customized [SnapshotStateList] where [equals] and [hashCode] are overridden so that the
 * equality of values inside the list is used for the comparison of equality instead of comparing
 * the equality by using the identify of the instance.
 * (SnapshotStateList compares the equality by using `instance === other`)
 */
class SnapshotStateListEqualsOverride<T> : StateObject, MutableList<T>, RandomAccess {
    private val delegate: SnapshotStateList<T> = SnapshotStateList()

    override val firstStateRecord: StateRecord
        get() = delegate.firstStateRecord

    override fun prependStateRecord(value: StateRecord) = delegate.prependStateRecord(value)

    override val size: Int
        get() = delegate.size

    override fun clear() {
        delegate.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean = delegate.addAll(elements)

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        delegate.addAll(index, elements)

    override fun add(index: Int, element: T) = delegate.add(index, element)

    override fun add(element: T): Boolean = delegate.add(element)

    override fun get(index: Int): T = delegate[index]

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun iterator(): MutableIterator<T> = delegate.iterator()

    override fun listIterator(): MutableListIterator<T> = delegate.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = delegate.listIterator()

    override fun removeAt(index: Int): T = delegate.removeAt(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        delegate.subList(fromIndex, toIndex)

    override fun set(index: Int, element: T): T = delegate.set(index, element)

    override fun retainAll(elements: Collection<T>): Boolean = delegate.retainAll(elements)

    override fun removeAll(elements: Collection<T>): Boolean = delegate.removeAll(elements)

    override fun remove(element: T): Boolean = delegate.remove(element)

    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun indexOf(element: T): Int = delegate.indexOf(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SnapshotStateListEqualsOverride<*>) return false
        return delegate.toList() == other.delegate.toList()
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }
}

@StateFactoryMarker
fun <T> mutableStateListEqualsOverrideOf(vararg elements: T) =
    SnapshotStateListEqualsOverride<T>().also { it.addAll(elements.toList()) }

/**
 * Create an instance of [MutableList]<T> from a collection that is observable and can be
 * snapshot.
 */
fun <T> Collection<T>.toMutableStateListEqualsOverride() =
    SnapshotStateListEqualsOverride<T>().also { it.addAll(this) }
