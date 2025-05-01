package diy.lingerie.utils.iterable

fun <T> Iterable<T>.clusterSimilar(
    predicate: (prev: T, next: T) -> Boolean,
): List<List<T>> {
    val groups = mutableListOf<MutableList<T>>()
    val iterator = this.iterator()

    if (!iterator.hasNext()) return groups

    var currentGroup = mutableListOf(iterator.next()).also(groups::add)

    while (iterator.hasNext()) {
        val next = iterator.next()
        if (predicate(currentGroup.last(), next)) {
            currentGroup.add(next)
        } else {
            currentGroup = mutableListOf(next)
            groups.add(currentGroup)
        }
    }

    return groups
}
