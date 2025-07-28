package dev.toolkt.geometry

import dev.toolkt.geometry.math.NormalPlaneFunction

class Plane(
    internal val normalPlaneFunction: NormalPlaneFunction,
) {
    companion object {
        fun of(
            origin: Point3D,
            normalDirection: Direction3,
        ): Plane = Plane(
            normalPlaneFunction = NormalPlaneFunction(
                origin = origin.pointVector,
                normal = normalDirection.normalizedDirectionVector,
            ),
        )
    }

    fun findIntersection(
        ray3: Ray3,
    ): Point3D? {
        val rayLineFunction = ray3.parametricLineFunction

        val t = rayLineFunction.solvePlaneIntersection(
            planeFunction = normalPlaneFunction,
        ) ?: return null

        if (t < 0) {
            return null
        }

        return Point3D(
            rayLineFunction.apply(t),
        )
    }
}
