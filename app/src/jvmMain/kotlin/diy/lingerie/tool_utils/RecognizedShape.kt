package diy.lingerie.tool_utils

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Ray
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.svg.importSvgPath
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.svg.SvgLine
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRectangle
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape

sealed class RecognizedShape {
    companion object {
        fun interpretSvg(
            svgRoot: SvgRoot,
        ): List<RecognizedShape> {
            val svgShapes = svgRoot.flatten(
                baseTransformation = Transformation.Identity,
            )

            val (svgRects, otherSvgShapes) = svgShapes.partition {
                it is SvgRectangle
            }

            val singleSvgRect = svgRects.singleOrNull()
                ?: error("Expected a single rectangle, but found ${svgRects.size} rectangles")

            val areaRectangle = singleSvgRect as SvgRectangle

            return RecognizedShape.interpretSvgShapes(
                areaRectangle = areaRectangle,
                svgShapes = otherSvgShapes,
            )
        }

        fun interpretSvgShapes(
            areaRectangle: SvgRectangle,
            svgShapes: Iterable<SvgShape>,
        ): List<RecognizedShape> = svgShapes.map { svgShape ->
            interpretSvgShape(
                areaRectangle = areaRectangle,
                svgShape = svgShape,
            )
        }

        fun interpretSvgShape(
            areaRectangle: SvgRectangle,
            svgShape: SvgShape,
        ): RecognizedShape = when (svgShape) {
            is SvgLine -> {
                interpretSvgLine(
                    areaRectangle = areaRectangle,
                    svgLine = svgShape,
                )
            }

            is SvgPath -> {
                interpretSvgPath(
                    areaRectangle = areaRectangle,
                    svgPath = svgShape,
                )
            }

            else -> throw UnsupportedOperationException("Unsupported SVG shape: $svgShape")
        }

        private fun interpretSvgLine(
            areaRectangle: SvgRectangle,
            svgLine: SvgLine,
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
            areaRectangle: SvgRectangle,
            svgPath: SvgPath,
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
                    svgLine = SvgLine(
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