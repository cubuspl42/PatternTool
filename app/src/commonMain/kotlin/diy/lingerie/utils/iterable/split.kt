package diy.lingerie.utils.iterable

typealias Crack<T> = (T) -> Pair<T, T>

data class Split2<T>(
    val leadingElements: List<T>,
    val trailingElements: List<T>,
)


/**
 * Split the list before the [index].
 */
fun <T> List<T>.splitBefore(index: Int): Split2<T> = Split2(
    leadingElements = take(index),
    trailingElements = drop(index),
)

fun <T> List<T>.crackAt(
    index: Int,
    crack: Crack<T>,
) {
    if (index !in indices) {
        throw IndexOutOfBoundsException("index: $index, size: $size")
    }

    TODO()
}

data class Split3<T>(
    val leadingElements: List<T>,
    val innerElements: List<T>,
    val trailingElements: List<T>,
)

/**
 * Split the list before the [firstIndex] and before the [secondIndex].
 *
 * @return The three parts of the list after the split
 */
fun <T> List<T>.splitBefore(
    firstIndex: Int,
    secondIndex: Int,
): Split3<T> {
    if (firstIndex !in indices) {
        throw IndexOutOfBoundsException("firstIndex: $firstIndex, size: $size")
    }

    if (secondIndex !in indices) {
        throw IndexOutOfBoundsException("secondIndex: $secondIndex, size: $size")
    }

    if (firstIndex >= secondIndex) {
        throw IllegalArgumentException("firstIndex: $firstIndex > secondIndex: $secondIndex")
    }

    return Split3(
        leadingElements = take(firstIndex),
        innerElements = subList(firstIndex, secondIndex),
        trailingElements = drop(secondIndex),
    )
}
