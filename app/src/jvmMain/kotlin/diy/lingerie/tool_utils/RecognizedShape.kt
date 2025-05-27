package diy.lingerie.tool_utils

import dev.toolkt.geometry.Line
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.svg.importSvgPath
import dev.toolkt.geometry.transformations.Transformation
import diy.lingerie.simple_dom.svg.PureSvgLine
import diy.lingerie.simple_dom.svg.PureSvgPath
import diy.lingerie.simple_dom.svg.PureSvgRectangle
import diy.lingerie.simple_dom.svg.PureSvgRoot
import diy.lingerie.simple_dom.svg.PureSvgShape
import diy.lingerie.simple_dom.toHexString

sealed class RecognizedShape {
    companion object {
        fun interpretSvg(
            svgRoot: PureSvgRoot,
        ): List<RecognizedShape> {
            val svgShapes = svgRoot.flatten(
                baseTransformation = Transformation.Identity,
            )

            val (svgRects, otherSvgShapes) = svgShapes.partition {
                it is PureSvgRectangle
            }

            val singleSvgRect = svgRects.singleOrNull()
                ?: error("Expected a single rectangle, but found ${svgRects.size} rectangles")

            val areaRectangle = singleSvgRect as PureSvgRectangle

            return RecognizedShape.interpretSvgShapes(
                areaRectangle = areaRectangle,
                svgShapes = otherSvgShapes,
            )
        }

        fun interpretSvgShapes(
            areaRectangle: PureSvgRectangle,
            svgShapes: Iterable<PureSvgShape>,
        ): List<RecognizedShape> = svgShapes.map { svgShape ->
            interpretSvgShape(
                areaRectangle = areaRectangle,
                svgShape = svgShape,
            )
        }

        fun interpretSvgShape(
            areaRectangle: PureSvgRectangle,
            svgShape: PureSvgShape,
        ): RecognizedShape = when (svgShape) {
            is PureSvgLine -> {
                interpretSvgLine(
                    areaRectangle = areaRectangle,
                    svgLine = svgShape,
                )
            }

            is PureSvgPath -> {
                interpretSvgPath(
                    areaRectangle = areaRectangle,
                    svgPath = svgShape,
                )
            }

            else -> throw UnsupportedOperationException("Unsupported SVG shape: $svgShape")
        }

        private fun interpretSvgLine(
            areaRectangle: PureSvgRectangle,
            svgLine: PureSvgLine,
        ): RecognizedShape = when {
            areaRectangle.contains(svgLine.start) && areaRectangle.contains(svgLine.end) -> {
                recognizeLineSegment(
                    start = svgLine.start,
                    end = svgLine.end,
                )
            }

            areaRectangle.contains(svgLine.start) -> {
                recognizeRay(
                    startingPoint = svgLine.start,
                    finalPoint = svgLine.end,
                )
            }

            areaRectangle.contains(svgLine.end) -> {
                recognizeRay(
                    startingPoint = svgLine.end,
                    finalPoint = svgLine.start,
                )
            }

            else -> {
                recognizeLine(
                    pointA = svgLine.start,
                    pointB = svgLine.end,
                )
            }
        }

        private fun recognizeRay(
            startingPoint: Point,
            finalPoint: Point,
        ): RecognizedRay {
            val ray = startingPoint.castRayTo(
                target = finalPoint,
            ) ?: throw UnsupportedOperationException("Ray cannot be created")

            return RecognizedRay(
                ray = ray,
            )
        }

        private fun recognizeLineSegment(
            start: Point,
            end: Point,
        ): RecognizedLineSegment {
            val lineSegment = LineSegment(
                start = start,
                end = end,
            )

            return RecognizedLineSegment(
                lineSegment = lineSegment,
            )
        }

        private fun recognizeLine(
            pointA: Point,
            pointB: Point,
        ): RecognizedLine {
            val line = LineSegment(
                pointA,
                pointB,
            ).containingLine ?: throw UnsupportedOperationException("Line cannot be created")

            return RecognizedLine(
                line = line,
            )
        }

        private fun interpretSvgPath(
            areaRectangle: PureSvgRectangle,
            svgPath: PureSvgPath,
        ): RecognizedShape {
            val hexColorString = svgPath.stroke?.let { stroke ->
                "[${svgPath.stroke.color.toHexString()}]"
            }

            println("(SVG path, color: $hexColorString)")

            val spline = Spline.Companion.importSvgPath(svgPath = svgPath)

            val lineSegment = (spline as? OpenSpline)?.toLineSegment()

            return when {
                lineSegment != null -> interpretSvgLine(
                    areaRectangle = areaRectangle,
                    svgLine = PureSvgLine(
                        start = lineSegment.start,
                        end = lineSegment.end,
                    ),
                )

                else -> recognizeSpline(spline = spline)
            }
        }

        private fun recognizeSpline(
            spline: Spline,
        ): RecognizedSpline = RecognizedSpline(
            spline = spline,
        )
    }

    data class RecognizedLine(
        val line: Line,
    ) : RecognizedShape()

    data class RecognizedLineSegment(
        val lineSegment: LineSegment,
    ) : RecognizedShape()

    data class RecognizedRay(
        val ray: Ray,
    ) : RecognizedShape()

    data class RecognizedSpline(
        val spline: Spline,
    ) : RecognizedShape()
}