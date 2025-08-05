package dev.toolkt.geometry.transformations

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.Direction3
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.rotateZ
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Translation
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.div

sealed class PrimitiveTransformation3D : DescriptiveTransformation3D() {
    data class Translation(
        val translationVector: Vector3,
    ) : PrimitiveTransformation3D() {
        companion object {
            val None = Translation(
                translationVector = Vector3.Companion.Zero,
            )

            fun inDirection(
                direction: Direction3,
                distance: Span,
            ): PrimitiveTransformation3D.Translation = Translation(
                translationVector = direction.normalizedDirectionVector * distance.value,
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

        override fun toMatrix(): Matrix4x4 {
            TODO("Standard primitive matrix")
        }
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

        override fun toMatrix(): Matrix4x4 {
            TODO("Standard primitive matrix")
        }
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

        override fun toMatrix(): Matrix4x4 {
            TODO("Standard primitive matrix")
        }
    }

    abstract override fun invert(): PrimitiveTransformation3D
}
