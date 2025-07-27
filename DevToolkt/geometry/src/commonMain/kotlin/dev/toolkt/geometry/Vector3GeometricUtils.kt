/*
 * A set of conventional utils that give the three-dimensional algebraic vector
 * a geometric interpretation, connecting the first component (a0) to the X axis,
 * the second component (a1) to the Y axis and the third component (a2) to the Z axis.
 */
package dev.toolkt.geometry

import dev.toolkt.math.algebra.linear.vectors.Vector3

fun Vector3(
    x: Double,
    y: Double,
    z: Double,
): Vector3 = Vector3(
    a0 = x,
    a1 = y,
    a2 = z,
)

val Vector3.x
    get() = a0

val Vector3.y
    get() = a1

val Vector3.z
    get() = a2

val Vector3.xy
    get() = Vector2(
        x = x,
        y = y,
    )

/**
 * Rotate this vector around the Z axis by the given angle.
 * Only x and y are affected, z remains unchanged.
 */
fun Vector3.rotateZ(
    angle: RelativeAngle,
): Vector3 = Vector3(
    x = x * angle.cosFi - y * angle.sinFi,
    y = x * angle.sinFi + y * angle.cosFi,
    z = z
)
