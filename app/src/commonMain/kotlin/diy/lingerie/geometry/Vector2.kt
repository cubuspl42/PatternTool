/*
 * A set of conventional utils that give the two-dimensional algebraic vector
 * a geometric interpretation, connecting the first component (a0) to the X axis
 * and the second component (a1) to the Y axis.
 */
package diy.lingerie.geometry

import diy.lingerie.math.algebra.linear.vectors.Vector2

fun Vector2(
    x: Double,
    y: Double,
): Vector2 = Vector2(
    a0 = x,
    a1 = y,
)

val Vector2.x
    get() = a0

val Vector2.y
    get() = a1
