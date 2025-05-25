@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package diy.lingerie.frp

import diy.lingerie.utils.nextOrNull

actual class PlatformWeakSet<T : Any> : AbstractMutableSet<T>() {
    private val weakRefSet = mutableSetOf<WeakRef<T>>()

    actual override fun add(element: T): Boolean = when {
        asSequence().contains(element) -> {
            // It's likely not possible to check if the weakly-referenced element
            // is already in the weak set without iterating through it
            false
        }

        else -> {
            weakRefSet.add(WeakRef(element))

            true
        }
    }

    actual override fun iterator(): MutableIterator<T> = PurgingWeakSetIterator(
        weakRefIterator = weakRefSet.iterator(),
    )

    actual override val size: Int
        get() = asSequence().count()
}

/**
 * An iterator for [PlatformWeakSet] that purges unreachable elements as it
 * iterates.
 */
private class PurgingWeakSetIterator<T : Any>(
    private val weakRefIterator: MutableIterator<WeakRef<T>>,
) : MutableIterator<T> {
    private var peekedElement: T? = null

    override fun next(): T = when (val peekedElement = this.peekedElement) {
        // A blind call to `next()` is unsupported
        null -> throw UnsupportedOperationException()

        else -> {
            this.peekedElement = null
            peekedElement
        }
    }

    override fun hasNext(): Boolean {
        when (peekedElement) {
            null -> {
                val peekedElement = nextReachable() ?: return false

                this.peekedElement = peekedElement

                return true
            }

            else -> {
                // A repetitive call to `hasNext` is not a primary use case, but
                // it's easy to support
                return true
            }
        }
    }

    override fun remove() {
        // Actual mutation during iteration is not an important use case, and
        // it's tricky to support
        throw UnsupportedOperationException()
    }

    /**
     * Finds the next reachable element in the weak set.
     *
     * @return the next reachable element, or `null` if there are no more
     * reachable elements.
     */
    private tailrec fun nextReachable(): T? {
        val nextWeakRef = weakRefIterator.nextOrNull() ?: return null

        return when (val reachableNextElement = nextWeakRef.deref()) {
            null -> {
                // The element is not reachable anymore, let's purge it
                weakRefIterator.remove()

                // Let's pretend it was never there and move on
                nextReachable()
            }

            // The element is reachable, let's expose it
            else -> reachableNextElement
        }
    }
}
