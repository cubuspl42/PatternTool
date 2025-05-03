package diy.lingerie.algebra

import diy.lingerie.algebra.RealFunction.SamplingStrategy
import diy.lingerie.utils.iterable.linspace

interface RealFunction<out V> {
    data class SamplingStrategy(
        val x0: Double = 0.0,
        val x1: Double = 1.0,
        val sampleCount: Int,
    ) {
        init {
            require(x1 >= x0)
        }
    }

    data class Sample<V>(
        val x: Double,
        val value: V,
    )

    fun apply(x: Double): V
}

fun <V : Any> RealFunction<V?>.sampleValues(
    strategy: SamplingStrategy,
): List<V> = sample(strategy).map { it.value }

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
    formula.apply(x = x)?.let {
        RealFunction.Sample(
            x = x,
            value = it,
        )
    }
}.toList()
