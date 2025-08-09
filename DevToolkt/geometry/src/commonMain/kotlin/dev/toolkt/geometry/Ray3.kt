package dev.toolkt.geometry

import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction3
import dev.toolkt.geometry.transformations.Transformation3D

class Ray3(
    val origin: Point3D,
    val direction: Direction3,
) {
    internal val parametricLineFunction = ParametricLineFunction3(
        p = origin.pointVector,
        v = direction.normalizedDirectionVector,
    )

    val target: Point3D
        get() = origin.translateByDistance(
            direction = direction,
            distance = Span.One,
        )

    companion object {
        fun between(
            origin: Point3D,
            target: Point3D,
        ): Ray3 = Ray3(
            origin = origin,
            direction = origin.directionTo(target),
        )
    }

    fun transformBy(
        transformation: Transformation3D,
    ): Ray3 = between(
        origin = transformation.transform(origin),
        target = transformation.transform(target),
    )

    fun findPoint(distance: Span): Point3D {
        TODO("Not yet implemented")
    }

    val opposite: Ray3
        get() = Ray3(
            origin = origin,
            direction = direction.opposite,
        )
}
