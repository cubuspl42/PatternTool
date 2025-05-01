package diy.lingerie.pattern_tool

import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.toClosedSpline
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.simple_dom.SimpleDimension
import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.utils.iterable.clusterSimilar
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.untrail

private const val mmPerInch = 25.4
private const val defaultDpi = 300.0

fun Outline.Companion.loadSvg(
    svgRoot: SvgRoot,
    edgMetadata: Outline.EdgeMetadata = Outline.EdgeMetadata(
        seamAllowance = SeamAllowance(allowanceMm = 6.0),
    ),
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

    return buildOutline(
        internalSpline = svgPath.toClosedSpline().transformBy(
            transformation = transformationToMm,
        ),
        edgMetadata = edgMetadata,
    )
}

private fun buildOutline(
    internalSpline: ClosedSpline,
    edgMetadata: Outline.EdgeMetadata,
): Outline {
    val links = internalSpline.cyclicLinks.clusterSimilar { prevLink, nextLink ->
        false
    }.map { smoothLinks ->
        buildOutlineLink(
            smoothLinks = smoothLinks,
            edgMetadata = edgMetadata,
        )
    }

    return Outline(
        links = links,
    )
}

private fun buildOutlineLink(
    smoothLinks: List<Spline.Link>,
    edgMetadata: Outline.EdgeMetadata,
): Outline.Link {
    val (firstSmoothLink, trailingSmoothLinks) = smoothLinks.uncons()
        ?: throw AssertionError("List of smooth links must not be empty")

    val (innerSmoothLinks, lastSmoothLink) = trailingSmoothLinks.untrail() ?: run {
        return firstSmoothLink.toOutlineLink(edgMetadata = edgMetadata)
    }

    val (intermediateJoints, _) = innerSmoothLinks.mapCarrying(
        initialCarry = firstSmoothLink,
    ) { previousLink: Spline.Link, smoothLink ->
        val rearHandlePosition = previousLink.effectiveSecondControlPoint
        val anchorPosition = previousLink.end
        val frontHandlePosition = smoothLink.effectiveSecondControlPoint

        val anchorDistance = Point.distanceBetween(
            rearHandlePosition,
            anchorPosition,
        )

        val controlSegmentLength = Point.distanceBetween(
            rearHandlePosition,
            frontHandlePosition,
        )

        val t = anchorDistance / controlSegmentLength

        Pair(
            Outline.Joint.Smooth(
                rearHandle = Outline.Handle(position = rearHandlePosition),
                anchorCoord = OpenCurve.Coord(t = t),
                frontHandle = Outline.Handle(position = frontHandlePosition),
            ),
            smoothLink,
        )
    }

    return Outline.Link(
        edge = Outline.Edge(
            startHandle = firstSmoothLink.firstControlOrNull?.let {
                Outline.Handle(position = it)
            },
            intermediateJoints = intermediateJoints,
            endHandle = lastSmoothLink.secondControlOrNull?.let {
                Outline.Handle(position = it)
            },
            metadata = edgMetadata,
        ),
        endAnchor = Outline.Anchor(
            position = lastSmoothLink.end,
        ),
    )
}

val Spline.Link.firstControlOrNull: Point?
    get() = when (edge) {
        is MonoBezierCurve.Edge -> edge.firstControl
        is LineSegment.Edge -> null
        else -> throw UnsupportedOperationException("Unsupported edge type: ${edge::class}")
    }

val Spline.Link.secondControlOrNull: Point?
    get() = when (edge) {
        is MonoBezierCurve.Edge -> edge.secondControl
        is LineSegment.Edge -> null
        else -> throw UnsupportedOperationException("Unsupported edge type: ${edge::class}")
    }

val Spline.Link.effectiveSecondControlPoint: Point
    get() = when (edge) {
        is MonoBezierCurve.Edge -> edge.secondControl
        is LineSegment.Edge -> end
        else -> throw UnsupportedOperationException("Unsupported edge type: ${edge::class}")
    }

fun Spline.Link.toOutlineLink(
    edgMetadata: Outline.EdgeMetadata,
): Outline.Link = Outline.Link(
    edge = when (edge) {
        is MonoBezierCurve.Edge -> Outline.Edge(
            startHandle = Outline.Handle(
                position = edge.firstControl,
            ),
            intermediateJoints = emptyList(),
            endHandle = Outline.Handle(
                position = edge.secondControl,
            ),
            metadata = edgMetadata,
        )


        is LineSegment.Edge -> Outline.Edge(
            startHandle = null,
            intermediateJoints = emptyList(),
            endHandle = null,
            metadata = edgMetadata,
        )

        else -> throw UnsupportedOperationException("Unsupported edge type: ${edge::class}")
    },
    endAnchor = Outline.Anchor(
        position = end,
    ),
)

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
