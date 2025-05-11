package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.algebra.NumericObject
import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgLine(
    val start: Point,
    val end: Point,
    override val stroke: Stroke = Stroke.default,
) : SvgShape() {
    override val fill: Fill.Specified? = null

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("line").apply {
        setAttribute("x1", start.x.toString())
        setAttribute("y1", start.y.toString())
        setAttribute("x2", end.x.toString())
        setAttribute("y2", end.y.toString())

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SvgLine -> false
        !start.equalsWithTolerance(other.start, tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance) -> false
        !stroke.equalsWithTolerance(other.stroke, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): SvgLine = SvgLine(
        start = transformation.transform(point = start),
        end = transformation.transform(point = end),
        stroke = stroke,
    )
}
