package diy.lingerie.utils.iterable

fun <E> MutableList<E>.removeRange(indexRange: IntRange) {
    val startIndex = indexRange.first
    val endIndex = indexRange.last

    if (startIndex < 0 || endIndex >= size) {
        throw IndexOutOfBoundsException("Index range $indexRange is out of bounds for list of size $size")
    }

    for (index in endIndex downTo startIndex) {
        removeAt(index)
    }
}

fun <E> MutableList<E>.updateRange(
    indexRange: IntRange,
    elements: Collection<E>,
) {
    if (!indexRange.isEmpty()) {
        removeRange(indexRange = indexRange)
    }

    addAll(indexRange.start, elements)
}
