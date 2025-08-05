package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D

class ZPreservingTransformation3D<FlatTransformationT : Transformation>(
    val flatTransformation: FlatTransformationT,
) : Transformation3D() {
    override fun transform(point: Point3D): Point3D {
        val (pointXy, z) = point.split()
        val transformedPointXy = flatTransformation.transform(pointXy)
        return transformedPointXy.toPoint3D(z = z)
    }

    override fun invert(): ZPreservingTransformation3D<*> = ZPreservingTransformation3D(
        flatTransformation = flatTransformation.invert(),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}

fun <BaseTransformationT : Transformation> BaseTransformationT.zPreserving(): ZPreservingTransformation3D<BaseTransformationT> =
    ZPreservingTransformation3D(
        flatTransformation = this,
    )
