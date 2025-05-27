package diy.lingerie.pattern_tool

import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.splines.ClosedSpline
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.svg.importSvgPath
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.PureUnit
import diy.lingerie.simple_dom.svg.PureSvgPath
import diy.lingerie.simple_dom.svg.PureSvgRoot

private const val mmPerInch = 25.4
private const val defaultDpi = 300.0

fun Outline.Companion.loadSvg(
    svgRoot: PureSvgRoot,
    edgeMetadataMap: Outline.EdgeMetadataMap,
): Outline {
    val transformationToMm = PrimitiveTransformation.Scaling(
        scaleVector = determineToMmScale(
            width = svgRoot.width,
            height = svgRoot.height,
            viewBox = svgRoot.effectiveViewBox,
        ),
    )

    val singleElement =
        svgRoot.graphicsElements.singleOrNull() ?: throw IllegalArgumentException("SVG document must contain a single element")

    val svgPath =
        singleElement as? PureSvgPath ?: throw IllegalArgumentException("The single element must be a path element")

    val closedSpline = Spline.importSvgPath(
        svgPath = svgPath,
    ) as? ClosedSpline ?: throw IllegalArgumentException("The path must be closed")

    return Outline.reconstruct(
        cyclicSmoothCurves = closedSpline.transformBy(
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
    width: PureDimension<*>,
    height: PureDimension<*>,
    viewBox: PureSvgRoot.ViewBox,
): Vector2 {
    if (viewBox.x != 0.0 || viewBox.y != 0.0) {
        throw IllegalArgumentException("ViewBox must start at (0, 0)")
    }

    val unit = width.unit

    if (height.unit != unit) {
        throw IllegalArgumentException("Width and height units must be the same")
    }

    return when (unit) {
        PureUnit.Percent -> {
            if (width.value != 100.0 || height.value != 100.0) {
                throw IllegalArgumentException("Width and height must be 100% when using percent units")
            }

            // If width = 100% and height = 100%, we assume that the internal units are points with DPI = 300

            Vector2.full(mmPerInch / defaultDpi)
        }

        PureUnit.Mm -> {
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
