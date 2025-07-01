package dev.toolkt.core.collections

class StableIteratorImpl<E>(
    private val mutableCollection: MutableStableCollection<E>,
    private var currentHandle: StableCollection.Handle<E>,
) : MutableStableIterator<E> {
    override fun remove() {
        val removedElement = mutableCollection.removeVia(handle = currentHandle)

        if (removedElement == null) {
            throw IllegalStateException("The iterator is invalid")
        }
    }

    override fun get(): E = mutableCollection.getVia(
        handle = currentHandle,
    ) ?: throw IllegalStateException("The iterator is invalid")

    override fun next(): StableIterator<E>? {
        TODO()
    }
}
