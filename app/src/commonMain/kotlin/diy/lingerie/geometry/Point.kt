package diy.lingerie.geometry

import diy.lingerie.geometry.transformations.Transformation

data class Point(
    val x: Double,
    val y: Double,
) {
    fun transformBy(
        transformation: Transformation,
    ): Point = transformation.transform(point = this)
}
