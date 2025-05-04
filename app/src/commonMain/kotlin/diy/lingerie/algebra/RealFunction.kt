package diy.lingerie.algebra

import diy.lingerie.algebra.RealFunction.SamplingStrategy
import diy.lingerie.utils.iterable.linspace

interface RealFunction<out B> : Function<Double, B> {
    data class SamplingStrategy(
        val x0: Double = 0.0,
        val x1: Double = 1.0,
        val sampleCount: Int,
    ) {
        init {
            require(x1 >= x0)
        }
    }

    data class Sample<B>(
        val a: Double,
        val b: B,
    )
}

fun <V : Any> RealFunction<V?>.sampleValues(
    strategy: SamplingStrategy,
): List<V> = sample(strategy).map { it.b }

fun <V : Any> RealFunction<V?>.sample(
    strategy: SamplingStrategy,
): List<RealFunction.Sample<V>> = strategy.sample(this)

fun <V : Any> SamplingStrategy.sample(
    formula: RealFunction<V?>,
): List<RealFunction.Sample<V>> = linspace(
    x0 = x0,
    x1 = x1,
    n = sampleCount,
).mapNotNull { x ->
    formula.apply(x)?.let {
        RealFunction.Sample(
            a = x,
            b = it,
        )
    }
}.toList()
