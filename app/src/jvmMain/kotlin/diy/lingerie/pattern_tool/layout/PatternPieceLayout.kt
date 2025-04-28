package diy.lingerie.pattern_tool.layout

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.AngleSerializer
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.PointSerializer
import diy.lingerie.geometry.toSvgPath
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.simple_dom.svg.SvgGroup
import kotlinx.serialization.Serializable

@Serializable
data class PatternPieceLayout(
    /**
     * Piece's position in the document's coordinates
     */
    @Serializable(with = PointSerializer::class) // Use custom serializer for Point
    val position: Point,
    /**
     * Rotation angle relative to the local origin
     */
    @Serializable(with = AngleSerializer::class) // Use custom serializer for Angle
    val rotationAngle: Angle,
) {
    val transformation: CombinedTransformation
        get() = PrimitiveTransformation.Companion.combine(
            PrimitiveTransformation.Rotation(angle = rotationAngle),
            PrimitiveTransformation.Translation(translationVector = position.pointVector),
        )

    fun layOut(
        patternPiece: PatternPiece,
    ): SvgGroup = SvgGroup(
        transformation = transformation,
        children = listOf(
            patternPiece.outline.innerSpline.toSvgPath()
        ),
    )
}
