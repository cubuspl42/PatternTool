package diy.lingerie.pattern_tool.layout

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.toSvgPath
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.pattern_tool.PatternPiece
import diy.lingerie.simple_dom.svg.SvgPath

data class PatternPieceLayout(
    /**
     * Piece's position in the document's coordinates
     */
    val position: Point,
    /**
     * Rotation angle relative to the local origin
     */
    val rotationAngle: Angle,
) {
    val transformation: CombinedTransformation
        get() = PrimitiveTransformation.Companion.combine(
            PrimitiveTransformation.Rotation(angle = rotationAngle),
            PrimitiveTransformation.Translation(translationVector = position.pointVector),
        )

    fun toSvgElement(
        patternPiece: PatternPiece,
    ): SvgPath {
        return patternPiece.outline.innerSpline.toSvgPath()
    }
}
