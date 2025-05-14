package diy.lingerie.utils

fun avgOf(
    a: Double,
    b: Double,
): Double = (a + b) / 2.0

fun Double.split(): Pair<Int, Double> {
    val integerPart = toInt()
    val fractionalPart = this - integerPart
    return Pair(integerPart, fractionalPart)
}

inline val Double.sq: Double
    get() = this * this
