package dev.toolkt.geometry

import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction3

class Ray3(
    val origin: Point3D,
    val direction: Direction3,
) {
    internal val parametricLineFunction = ParametricLineFunction3(
        p = origin.pointVector,
        v = direction.normalizedDirectionVector,
    )

    companion object {
        fun of(
            origin: Point3D,
            target: Point3D,
        ): Ray3 = Ray3(
            origin = origin,
            direction = origin.directionTo(target),
        )
    }
}
