package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.math.algebra.linear.vectors.Vector2
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class QuadraticBezierBinomialBenchmarks {
    val quadraticBezierBinomial = QuadraticBezierBinomial(
        point0 = Vector2(0.0, 0.0),
        point1 = Vector2(100.0, 200.0),
        point2 = Vector2(200.0, 0.0),
    )

    @Benchmark
    fun primaryArcLength(): Double {
        return quadraticBezierBinomial.primaryArcLength
    }

    @Benchmark
    fun primaryArcLengthApproximate(): Double {
        return quadraticBezierBinomial.primaryArcLengthApproximate
    }

    @Benchmark
    fun primaryArcLengthGauss(): Double {
        return quadraticBezierBinomial.primaryArcLengthGauss
    }
}
