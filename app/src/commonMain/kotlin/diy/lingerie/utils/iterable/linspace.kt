package diy.lingerie.utils.iterable

fun linspace(x0: Double, x1: Double, n: Int): Sequence<Double> {
    if (n < 2) throw IllegalArgumentException("n must be at least 2 to include both boundaries")
    val step = (x1 - x0) / (n - 1)
    return generateSequence(0) { it + 1 }.take(n).map { i -> x0 + i * step }
}
