package diy.lingerie.pattern_tool

import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.toClosedSpline
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.simple_dom.SimpleDimension
import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot

private const val mmPerInch = 25.4
private const val defaultDpi = 300.0

fun Outline.Companion.loadSvg(
    svgRoot: SvgRoot,
    edgeMetadataMap: Outline.EdgeMetadataMap,
): Outline {
    val transformationToMm = PrimitiveTransformation.Scaling(
        scaleVector = determineToMmScale(
            width = svgRoot.width,
            height = svgRoot.height,
            viewBox = svgRoot.viewBox,
        ),
    )

    val singleElement =
        svgRoot.children.singleOrNull() ?: throw IllegalArgumentException("SVG document must contain a single element")

    val svgPath =
        singleElement as? SvgPath ?: throw IllegalArgumentException("The single element must be a path element")

    return Outline.reconstruct(
        cyclicSmoothCurves = svgPath.toClosedSpline().transformBy(
            transformation = transformationToMm,
        ).smoothSubSplines,
        edgeMetadataMap = edgeMetadataMap,
    )
}

/**
 * Computes the scale factor that converts the internal SVG units to millimeters
 * (x_mm = x_internal * s_x, y_mm = y_internal * s_y).
 *
 * @return The scale factor [s_x, s_y]
 */
private fun determineToMmScale(
    width: SimpleDimension,
    height: SimpleDimension,
    viewBox: SvgRoot.ViewBox,
): Vector2 {
    if (viewBox.x != 0.0 || viewBox.y != 0.0) {
        throw IllegalArgumentException("ViewBox must start at (0, 0)")
    }

    val unit = width.unit

    if (height.unit != unit) {
        throw IllegalArgumentException("Width and height units must be the same")
    }

    return when (unit) {
        SimpleUnit.Percent -> {
            if (width.value != 100.0 || height.value != 100.0) {
                throw IllegalArgumentException("Width and height must be 100% when using percent units")
            }

            // If width = 100% and height = 100%, we assume that the internal units are points with DPI = 300

            Vector2.each(mmPerInch / defaultDpi)
        }

        SimpleUnit.Mm -> {
            // If the SVG document size is expressed in millimeters, we just need to project the viewbox onto the
            // expected size

            return Vector2(
                x = width.value / viewBox.width,
                y = height.value / viewBox.height,
            )
        }

        else -> throw UnsupportedOperationException("Unsupported unit: $unit")
    }
}
