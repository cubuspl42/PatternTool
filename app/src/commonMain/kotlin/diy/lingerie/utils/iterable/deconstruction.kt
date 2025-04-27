package diy.lingerie.utils.iterable

data class Uncons<T>(
    val firstElement: T,
    val trailingElement: List<T>,
)

fun <T> List<T>.uncons(): Uncons<T>? = firstOrNull()?.let { head ->
    Uncons(
        firstElement = head,
        trailingElement = drop(1),
    )
}

data class Untrail<T>(
    val leadingElements: List<T>,
    val lastElement: T,
)

fun <T> List<T>.untrail(): Untrail<T>? = lastOrNull()?.let { foot ->
    Untrail(
        leadingElements = dropLast(1),
        lastElement = foot,
    )
}
