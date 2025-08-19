package dev.toolkt.dom.reactive.utils.svg

import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveElement
import dev.toolkt.dom.reactive.utils.svg.transforms.bind
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.extra.createSvgCircleElement
import org.w3c.dom.extra.createSvgGroupElement
import org.w3c.dom.extra.createSvgLineElement
import org.w3c.dom.extra.createSvgPathElement
import org.w3c.dom.extra.createSvgPolylineElement
import org.w3c.dom.extra.createSvgSvgElement
import org.w3c.dom.extra.svg.SVGPathSegment
import org.w3c.dom.extra.svg.setPathData
import org.w3c.dom.svg.SVGAnimatedLength
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGGraphicsElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGSVGElement
import svg.SVGPoint

fun Document.createReactiveSvgSvgElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<SVGElement>? = null,
): SVGSVGElement = createReactiveElement(
    createElement = Document::createSvgSvgElement,
    style = style,
    children = children,
)

fun Document.createReactiveSvgCircleElement(
    style: ReactiveStyle? = null,
    position: Cell<Point>,
    radius: Double,
    children: ReactiveList<SVGElement>? = null,
): SVGCircleElement = createReactiveElement(
    createElement = Document::createSvgCircleElement,
    style = style,
    children = children,
).apply {
    position.bind(
        target = this,
        xAnimatedLength = this.cx,
        yAnimatedLength = this.cy,
    )

    this.r.baseValue = radius
}

fun Document.createReactiveSvgPathElement(
    style: ReactiveStyle? = null,
    pathSegments: ReactiveList<SVGPathSegment>,
): SVGPathElement = createReactiveElement(
    createElement = Document::createSvgPathElement,
    style = style,
).apply {
    pathSegments.elements.bind(
        target = this,
    ) { pathElement, pathSegments ->
        val pathData = pathSegments.toTypedArray()

        pathElement.setPathData(
            pathData = pathData,
        )
    }
}

fun Document.createReactiveSvgPolylineElement(
    style: ReactiveStyle? = null,
    points: ReactiveList<SVGPoint>,
): SVGElement = createReactiveElement(
    createElement = Document::createSvgPolylineElement,
    style = style,
).apply {
    points.bind(
        target = this,
    )
}

fun Document.createReactiveSvgLineElement(
    style: ReactiveStyle? = null,
    start: Cell<Point>,
    end: Cell<Point>,
): SVGLineElement = createReactiveElement(
    createElement = Document::createSvgLineElement,
    style = style,
).apply {
    start.bind(
        target = this,
        xAnimatedLength = this.x1,
        yAnimatedLength = this.y1,
    )

    end.bind(
        target = this,
        xAnimatedLength = this.x2,
        yAnimatedLength = this.y2,
    )
}

fun Document.createReactiveSvgGroupElement(
    svgElement: SVGSVGElement,
    style: ReactiveStyle? = null,
    transformation: Cell<Transformation>?,
    children: ReactiveList<SVGElement>,
): SVGGElement = createReactiveSvgGraphicsElement(
    svgElement = svgElement,
    createSvgGraphicsElement = Document::createSvgGroupElement,
    style = style,
    transformation = transformation,
    children = children,
)

private fun <SvgGraphicsElementT : SVGGraphicsElement> Document.createReactiveSvgGraphicsElement(
    svgElement: SVGSVGElement,
    createSvgGraphicsElement: Document.() -> SvgGraphicsElementT,
    style: ReactiveStyle? = null,
    transformation: Cell<Transformation>? = null,
    children: ReactiveList<Node>? = null,
): SvgGraphicsElementT = createReactiveElement(
    createElement = createSvgGraphicsElement,
    style = style,
    children = children,
).apply {
    transformation?.let {
        transform.baseVal.bind(
            svgElement = svgElement,
            transformation = it,
        )
    }
}

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
