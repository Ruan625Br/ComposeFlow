package io.composeflow.di

import kotlin.reflect.KClass

// TODO: Introduce DI Library like kotlin-inject
object ServiceLocator {
    val map = mutableMapOf<Any, Any>()

    inline fun <reified T : Any> get(key: KClass<T>): T = map[key] as T

    inline fun <reified T : Any> get(): T = map[T::class] as T

    inline fun <reified T : Any> getOrPut(factory: () -> T): T = map.getOrPut(T::class) { factory() } as T

    inline fun <reified T : Any> getOrPutWithKey(
        key: String,
        factory: () -> T,
    ): T = map.getOrPut(key + T::class) { factory() } as T

    inline fun <reified T : Any> put(value: T) {
        map[T::class] = value
    }

    inline fun <reified T : Any> putWithKey(
        key: String,
        value: T,
    ) {
        map[key + T::class] = value
    }

    fun clear() {
        map.clear()
    }

    const val KeyIoDispatcherCoroutineScope = "IoDispatcherCoroutineScope"
    const val KeyDeafultDispatcher = "DispatcherDefault"
    const val KeyIoDispatcher = "DispatcherIO"
}
