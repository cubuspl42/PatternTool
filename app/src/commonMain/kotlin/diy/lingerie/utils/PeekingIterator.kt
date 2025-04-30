package diy.lingerie.utils

class PeekingIterator<T : Any>(
    private val iterator: Iterator<T>,
) : Iterator<T> {
    private var peekedElement: T? = null

    fun peekOrNull(): T? = when (val oldPeekedElement = peekedElement) {
        null -> {
            val newPeekedElement = iterator.nextOrNull()
            peekedElement = newPeekedElement
            newPeekedElement
        }

        else -> oldPeekedElement
    }

    fun peek(): T = peekOrNull() ?: throw NoSuchElementException()

    override fun next(): T = when (val oldPeekedElement = peekedElement) {
        null -> iterator.next()

        else -> {
            peekedElement = null
            oldPeekedElement
        }
    }

    override fun hasNext(): Boolean = when (peekedElement) {
        null -> iterator.hasNext()
        else -> true
    }
}

fun <T : Any> Iterator<T>.peeking(): PeekingIterator<T> = PeekingIterator(
    iterator = this,
)
