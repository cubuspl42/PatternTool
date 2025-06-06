package diy.lingerie.pattern_tool.layout

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.svg.PureSvgGroup
import dev.toolkt.dom.pure.svg.PureSvgShape
import dev.toolkt.geometry.AngleSerializer
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.PointSerializer
import dev.toolkt.geometry.RelativeAngle
import diy.lingerie.geometry.svg_utils.toSvgPath
import dev.toolkt.geometry.transformations.CombinedTransformation
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.pattern_tool.PatternPieceId
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
    @Serializable(with = AngleSerializer::class) val rotationAngle: RelativeAngle,
) {
    companion object {
        fun reconstruct(
            svgGroup: PureSvgGroup,
        ): Pair<PatternPieceId, PatternPieceLayout> {
            val pieceId = PatternPieceId.valueOf(
                svgGroup.id ?: throw IllegalArgumentException("Pattern piece layout must have an id"),
            )

            val transformation = svgGroup.transformation ?: throw IllegalArgumentException(
                "Pattern piece must have a transformation"
            )

            val combinedTransformation = transformation as? CombinedTransformation
                ?: throw IllegalArgumentException("Pattern piece must have a combined transformation")

            if (combinedTransformation.standaloneTransformations.size != 2) {
                throw IllegalArgumentException("Pattern piece must have exactly two sub-transformations")
            }

            val (firstTransformation, secondTransformation) = combinedTransformation.standaloneTransformations

            val rotation = firstTransformation as? PrimitiveTransformation.Rotation ?: throw IllegalArgumentException(
                "Pattern piece layout must have a rotation transformation as the second component"
            )

            val translation =
                secondTransformation as? PrimitiveTransformation.Translation ?: throw IllegalArgumentException(
                    "Pattern piece layout must have a translation transformation as the first component"
                )

            return pieceId to PatternPieceLayout(
                position = Point(pointVector = translation.translationVector),
                rotationAngle = rotation.angle,
            )
        }
    }

    val transformation: CombinedTransformation
        get() = PrimitiveTransformation.Companion.combine(
            PrimitiveTransformation.Rotation.relative(angle = rotationAngle),
            PrimitiveTransformation.Translation(translationVector = position.pointVector),
        )

    fun layOut(
        pieceId: PatternPieceId,
        patternPiece: PatternPiece,
    ): PureSvgGroup = PureSvgGroup(
        id = pieceId.name,
        transformation = transformation,
        children = listOf(
            patternPiece.outline.innerSpline.toSvgPath(
                stroke = PureSvgShape.Stroke(
                    color = PureColor.black,
                    width = 0.4,
                    dashArray = listOf(2.0, 1.0),
                ),
            ),
            patternPiece.outline.findSeamContour().toSvgPath(
                stroke = PureSvgShape.Stroke(
                    color = PureColor.black,
                    width = 0.8,
                ),
            )
        ),
    )
}
