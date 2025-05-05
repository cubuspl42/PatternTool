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

fun <B, C> RealFunction<B>.map(
    transform: (B) -> C,
): RealFunction<C> = MappedRealFunction(
    function = this,
    transform = transform,
)

private class MappedRealFunction<B, out C>(
    private val function: RealFunction<B>,
    private val transform: (B) -> C,
) : RealFunction<C> {
    override fun apply(a: Double): C = transform(function.apply(a))
}
