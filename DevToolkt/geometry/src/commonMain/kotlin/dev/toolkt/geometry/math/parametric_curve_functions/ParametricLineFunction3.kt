package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import dev.toolkt.geometry.math.NormalPlaneFunction
import dev.toolkt.math.algebra.Function
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class ParametricLineFunction3(
    val p: Vector3,
    val v: Vector3,
) : Function<Double, Vector3> {
    /**
     * Solve the intersection of this line with a plane defined by the
     *
     * @return The t-value for this line, or `null` if the line is parallel to the plane
     */
    fun solvePlaneIntersection(
        planeFunction: NormalPlaneFunction,
        tolerance: NumericTolerance.Absolute = NumericTolerance.Absolute.Default,
    ): Double? {
        val n = planeFunction.normal
        val d = planeFunction.d

        val det = n.dot(v)

        if (det.equalsZeroWithTolerance(tolerance = tolerance)) {
            return null
        }

        val t = (d - n.dot(p)) / det

        return t
    }

    override fun apply(a: Double): Vector3 {
        val t = a

        return p + v * t
    }
}
