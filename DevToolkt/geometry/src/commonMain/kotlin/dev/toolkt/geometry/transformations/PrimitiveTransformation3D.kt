package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.geometry.rotateZ
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.div

sealed class PrimitiveTransformation3D : StandaloneTransformation3D() {
    data class Translation(
        val translationVector: Vector3,
    ) : PrimitiveTransformation3D() {
        companion object {
            val None = Translation(
                translationVector = Vector3.Companion.Zero,
            )
        }

        constructor(
            tx: Double,
            ty: Double,
            tz: Double,
        ) : this(
            translationVector = dev.toolkt.geometry.Vector3(
                x = tx,
                y = ty,
                z = tz,
            ),
        )

        val tx: Double
            get() = translationVector.x

        val ty: Double
            get() = translationVector.y


        val tz: Double
            get() = translationVector.z

        override fun transform(point: Point3D): Point3D = Point3D(
            pointVector = point.pointVector + translationVector,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }


        override fun invert(): Translation = Translation(
            translationVector = -translationVector,
        )
    }

    data class Scaling(
        val scaleVector: Vector3,
    ) : PrimitiveTransformation3D() {
        constructor(
            sx: Double,
            sy: Double,
            sz: Double,
        ) : this(
            scaleVector = dev.toolkt.geometry.Vector3(
                x = sx,
                y = sy,
                z = sz,
            ),
        )

        val sx: Double
            get() = scaleVector.x

        val sy: Double
            get() = scaleVector.y

        val sz: Double
            get() = scaleVector.z

        init {
            require(!scaleVector.equalsWithTolerance(Vector3.Companion.Zero))
        }

        override fun transform(point: Point3D): Point3D = Point3D(
            pointVector = point.pointVector * scaleVector,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun invert(): Scaling = Scaling(
            scaleVector = 1.0 / scaleVector,
        )
    }

    data class RotationZ private constructor(
        val angle: RelativeAngle,
    ) : PrimitiveTransformation3D() {
        companion object {
            fun relative(
                angle: RelativeAngle,
            ): RotationZ = RotationZ(
                angle = angle,
            )
        }

        override fun transform(
            point: Point3D,
        ): Point3D = Point3D(
            pointVector = point.pointVector.rotateZ(
                angle = angle,
            ),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }


        override fun invert(): RotationZ = RotationZ(
            angle = -angle,
        )
    }

    abstract override fun invert(): PrimitiveTransformation3D

}
