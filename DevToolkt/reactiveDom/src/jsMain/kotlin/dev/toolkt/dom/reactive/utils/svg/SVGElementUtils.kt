package dev.toolkt.dom.reactive.utils.svg

import dev.toolkt.dom.pure.collections.pointList
import dev.toolkt.dom.pure.svg.PureSvg
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveElement
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.DOMPoint
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGAnimatedLength
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPolylineElement
import org.w3c.dom.svg.SVGSVGElement
import svg.SVGPathSegment
import svg.SVGPoint
import svg.setPathData

fun Document.createReactiveSvgElement(
    localSvgName: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): SVGElement = createReactiveElement(
    namespace = PureSvg.Namespace,
    name = localSvgName,
    style = style,
    children = children,
) as SVGElement

fun Document.createReactiveSvgSvgElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<SVGElement>? = null,
): SVGSVGElement = createReactiveSvgElement(
    localSvgName = "svg",
    style = style,
    children = children,
) as SVGSVGElement

fun Document.createReactiveSvgCircleElement(
    style: ReactiveStyle? = null,
    position: Cell<Point>,
    radius: Double,
    children: ReactiveList<SVGElement>? = null,
): SVGCircleElement {
    val circleElement = createReactiveSvgElement(
        localSvgName = "circle",
        style = style,
        children = children,
    ) as SVGCircleElement

    position.bind(
        target = circleElement,
        xAnimatedLength = circleElement.cx,
        yAnimatedLength = circleElement.cy,
    )

    circleElement.r.baseValue = radius

    return circleElement
}

fun Document.createReactiveSvgPathElement(
    style: ReactiveStyle? = null,
    pathSegments: ReactiveList<SVGPathSegment>,
): SVGPathElement {
    val pathElement = createReactiveSvgElement(
        localSvgName = "path",
        style = style,
    ) as SVGPathElement

    pathSegments.elements.bind(
        target = pathElement,
    ) { pathElement, pathSegments ->
        val pathData = pathSegments.toTypedArray()

        pathElement.setPathData(
            pathData = pathData,
        )
    }

    return pathElement
}

fun Document.createReactiveSvgPolylineElement(
    style: ReactiveStyle? = null,
    points: ReactiveList<SVGPoint>,
): SVGElement {
    val polylineElement = createReactiveSvgElement(
        localSvgName = "polyline",
        style = style,
    ) as SVGPolylineElement

    points.bind(
        target = polylineElement,
        extract = SVGPolylineElement::pointList,
    )

    return polylineElement
}

fun Document.createReactiveSvgLineElement(
    style: ReactiveStyle? = null,
    start: Cell<Point>,
    end: Cell<Point>,
): SVGLineElement {
    val lineElement = createReactiveSvgElement(
        localSvgName = "line",
        style = style,
    ) as SVGLineElement

    start.bind(
        target = lineElement,
        xAnimatedLength = lineElement.x1,
        yAnimatedLength = lineElement.y1,
    )

    end.bind(
        target = lineElement,
        xAnimatedLength = lineElement.x2,
        yAnimatedLength = lineElement.y2,
    )

    return lineElement
}

fun Document.createReactiveSvgGroupElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<SVGElement>,
): SVGGElement = createReactiveSvgElement(
    localSvgName = "g",
    style = style,
    children = children,
) as SVGGElement

var SVGAnimatedLength.baseValue: Double
    get() = this.baseVal.value.toDouble()
    set(value) {
        this.baseVal.value = value.toFloat()
    }

private fun <T : Any> Cell<Point>.bind(
    target: T,
    xAnimatedLength: SVGAnimatedLength,
    yAnimatedLength: SVGAnimatedLength,
) {
    this.bind(
        target = target,
        setX = { _, x -> xAnimatedLength.baseValue = x },
        setY = { _, y -> yAnimatedLength.baseValue = y },
    )
}

private fun <T : Any> Cell<Point>.bind(
    target: T,
    setX: (T, Double) -> Unit,
    setY: (T, Double) -> Unit,
) {
    this.bindAndForget(
        target = target,
    ) { it, point ->
        setX(it, point.x)
    }

    this.bindAndForget(
        target = target,
    ) { it, point ->
        setY(it, point.y)
    }
}
