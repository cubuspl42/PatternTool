package diy.lingerie.geometry.curves

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve.Coord
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class BezierCurveBenchmarks {
    val bezierCurve = BezierCurve(
        start = Point(530.4940814557458, 241.67258884541297),
        firstControl = Point(961.5421048139829, 329.8998894073666),
        secondControl = Point(236.94848087916398, 6.644895194057426),
        end = Point(476.141458980871, 199.36056001431643),
    )

    @Benchmark
    fun totalArcLength(): Double {
        return bezierCurve.calculateArcLength(
            coordRange = Coord.fullRange,
        )
    }

    @Benchmark
    fun primaryArcLengthApproximate(): Double {
        return bezierCurve.basisFunction.primaryArcLengthApproximate
    }
}
