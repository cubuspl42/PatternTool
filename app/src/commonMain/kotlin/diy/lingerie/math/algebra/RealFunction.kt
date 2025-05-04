package diy.lingerie.math.algebra

import diy.lingerie.utils.iterable.LinSpace

interface RealFunction<out B> : Function<Double, B> {
    data class Sample<out B>(
        val a: Double,
        val b: B,
    )
}

fun <B> RealFunction<B>.sample(
    linSpace: LinSpace,
): List<RealFunction.Sample<B>> = linSpace.generate().map { a ->
    val b = apply(a)

    RealFunction.Sample(
        a = a,
        b = b,
    )
}.toList()
