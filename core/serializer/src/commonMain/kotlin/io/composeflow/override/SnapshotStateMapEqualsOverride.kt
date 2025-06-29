package io.composeflow.override

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.StateFactoryMarker
import androidx.compose.runtime.snapshots.StateObject
import androidx.compose.runtime.snapshots.StateRecord

/**
 * Customized [SnapshotStateMap] where [equals] and [hashCode] are overridden so that the
 * equality of values inside the map is used for the comparison of equality instead of comparing
 * the equality by using the identify of the instance.
 * (SnapshotStateMap compares the equality by using `instance === other`)
 */
class SnapshotStateMapEqualsOverride<K, V> :
    StateObject,
    MutableMap<K, V> {
    private val delegate: SnapshotStateMap<K, V> = SnapshotStateMap()

    override val firstStateRecord: StateRecord
        get() = delegate.firstStateRecord

    override fun prependStateRecord(value: StateRecord) = delegate.prependStateRecord(value)

    override val size: Int
        get() = delegate.size

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = delegate.entries

    override val keys: MutableSet<K>
        get() = delegate.keys

    override val values: MutableCollection<V>
        get() = delegate.values

    override fun clear() = delegate.clear()

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun remove(key: K): V? = delegate.remove(key)

    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override fun put(
        key: K,
        value: V,
    ): V? = delegate.put(key, value)

    override fun get(key: K): V? = delegate[key]

    override fun containsValue(value: V): Boolean = delegate.containsValue(value)

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SnapshotStateMapEqualsOverride<*, *>) return false
        return delegate.toMap() == other.delegate.toMap()
    }

    override fun hashCode(): Int = delegate.toMap().hashCode()
}

@StateFactoryMarker
fun <K, V> mutableStateMapEqualsOverrideOf(vararg pairs: Pair<K, V>) =
    SnapshotStateMapEqualsOverride<K, V>().also { it.putAll(pairs.toMap()) }

/**
 * Create an instance of [MutableMap]<K, V> from a map that is observable and can be
 * snapshot.
 */
fun <K, V> Map<K, V>.toMutableStateMapEqualsOverride() = SnapshotStateMapEqualsOverride<K, V>().also { it.putAll(this) }
