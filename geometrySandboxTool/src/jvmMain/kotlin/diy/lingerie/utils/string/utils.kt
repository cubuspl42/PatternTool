package diy.lingerie.utils.string



fun String.indexOfFirstOrNull(
    predicate: (Char) -> Boolean,
): Int? {
    val index = indexOfFirst(predicate)
    return if (index == -1) null else index
}



fun String.splitBefore(index: Int): Pair<String, String> {
    require(index in 0..length) { "Index out of bounds" }
    return substring(0, index) to substring(index)
}
