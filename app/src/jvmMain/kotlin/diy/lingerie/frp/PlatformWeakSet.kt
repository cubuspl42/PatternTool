@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package diy.lingerie.frp

import diy.lingerie.utils.collection.CollectionUtils

actual class PlatformWeakSet<T : Any> : AbstractMutableSet<T>() {
    private val weakHashSet: MutableSet<T> = CollectionUtils.newWeakSet()

    actual override fun add(
        element: T,
    ): Boolean = weakHashSet.add(element)

    actual override fun iterator(): MutableIterator<T> = weakHashSet.iterator()

    actual override val size: Int
        get() = weakHashSet.size
}
