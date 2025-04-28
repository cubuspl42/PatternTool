package diy.lingerie.pattern_tool.layout

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.AngleSerializer
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.PointSerializer
import diy.lingerie.geometry.toSvgPath
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
import diy.lingerie.simple_dom.svg.SvgGroup
import kotlinx.serialization.Serializable

@Serializable
data class PatternPieceLayout(
    /**
     * Piece's position in the document's coordinates
     */
    @Serializable(with = PointSerializer::class) val position: Point,
    /**
     * Rotation angle relative to the local origin
     */
    @Serializable(with = AngleSerializer::class) val rotationAngle: Angle,
) {
    companion object {
        fun reconstruct(
            svgGroup: SvgGroup,
        ): Pair<PatternPieceId, PatternPieceLayout> {
            val pieceId = PatternPieceId.valueOf(
                svgGroup.id ?: throw IllegalArgumentException("Pattern piece layout must have an id"),
            )

            val transformation = svgGroup.transformation ?: throw IllegalArgumentException(
                "Pattern piece layout must have a transformation"
            )

            val combinedTransformation = transformation as? CombinedTransformation
                ?: throw IllegalArgumentException("Pattern piece layout must have a combined transformation")

            if (combinedTransformation.components.size != 2) {
                throw IllegalArgumentException("Pattern piece layout must have exactly two transformations")
            }

            val (firstTransformation, secondTransformation) = combinedTransformation.components

            val translation =
                firstTransformation as? PrimitiveTransformation.Translation ?: throw IllegalArgumentException(
                    "Pattern piece layout must have a translation transformation as the first component"
                )

            val rotation = secondTransformation as? PrimitiveTransformation.Rotation ?: throw IllegalArgumentException(
                "Pattern piece layout must have a rotation transformation as the second component"
            )

            return pieceId to PatternPieceLayout(
                position = Point(pointVector = -translation.translationVector),
                rotationAngle = rotation.angle,
            )
        }
    }

    val transformation: CombinedTransformation
        get() = PrimitiveTransformation.Companion.combine(
            PrimitiveTransformation.Rotation(angle = rotationAngle),
            PrimitiveTransformation.Translation(translationVector = position.pointVector),
        )

    fun layOut(
        pieceId: PatternPieceId,
        patternPiece: PatternPiece,
    ): SvgGroup = SvgGroup(
        id = pieceId.name,
        transformation = transformation,
        children = listOf(
            patternPiece.outline.innerSpline.toSvgPath()
        ),
    )
}
