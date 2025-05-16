package diy.lingerie.utils.iterable

data class LinSpace(
    val range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    val sampleCount: Int,
) {
    init {
        require(sampleCount >= 2) { "n must be at least 2 to include both boundaries" }
    }

    val x0: Double
        get() = range.start

    val x1: Double
        get() = range.endInclusive

    fun generate(): Sequence<Double> {
        val step = (x1 - x0) / (sampleCount - 1)
        return generateSequence(0) { it + 1 }.take(sampleCount).map { i -> x0 + i * step }
    }
}
