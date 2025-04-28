package diy.lingerie.pattern_tool

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation

data class PatternPiece(
    /**
     * Rotation angle relative to the local origin
     */
    val rotationAngle: Angle,
    /**
     * Piece's position in the document's coordinates
     */
    val position: Point,
    /**
     * Outline in its local coordinates
     */
    val outline: Outline,
) {
    val outlineInnerSplineGlobal: ClosedSpline
        get() = outline.innerSpline.transformBy(
            transformation = PrimitiveTransformation.combine(
                PrimitiveTransformation.Rotation(angle = rotationAngle),
                PrimitiveTransformation.Translation(translationVector = position.pointVector),
            ),
        )
}
