package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPathElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPolylineElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.sample
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.extra.svg.SVGPathSegment
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGSVGElement
import svg.createLegacySVGPoint

data class ReactiveBezierCurve(
    val start: Cell<Point>,
    val firstControl: Cell<Point>,
    val secondControl: Cell<Point>,
    val end: Cell<Point>,
) : ReactiveCurve<BezierCurve>() {
    companion object {
        fun diff(
            curveCell: Cell<BezierCurve>,
        ): ReactiveBezierCurve = ReactiveBezierCurve(
            start = curveCell.map { it.start }.calm(),
            firstControl = curveCell.map { it.firstControl }.calm(),
            secondControl = curveCell.map { it.secondControl }.calm(),
            end = curveCell.map { it.end }.calm(),
        )
    }

    val bezierCurve: Cell<BezierCurve> = Cell.map4(
        cell1 = start,
        cell2 = firstControl,
        cell3 = secondControl,
        cell4 = end,
    ) { startNow, firstControlNow, secondControlNow, endNow ->
        BezierCurve(
            start = startNow,
            firstControl = firstControlNow,
            secondControl = secondControlNow,
            end = endNow,
        )
    }

    override val primitiveCurve: Cell<BezierCurve>
        get() = bezierCurve

    context(momentContext: MomentContext) fun createReactiveSvgPathElement(
        style: ReactiveStyle,
    ): SVGPathElement = document.createReactiveSvgPathElement(
        style = style,
        pathSegments = ReactiveList.fuse(
            start.map {
                SVGPathSegment(
                    type = "M",
                    values = it.toArray(),
                )
            },
            Cell.map3(
                cell1 = firstControl,
                cell2 = secondControl,
                cell3 = end,
            ) { firstControlNow, secondControlNow, lastControlNow ->
                SVGPathSegment(
                    type = "C",
                    values = firstControlNow.toArray() + secondControlNow.toArray() + lastControlNow.toArray(),
                )
            },
        ),
    )

    context(momentContext: MomentContext) fun createReactiveExtendedSvgPolylineElement(
        svgElement: SVGSVGElement,
    ): SVGElement {
        val maxT = 5.0

        return document.createReactiveSvgPolylineElement(
            style = ReactiveStyle(
                fill = Cell.of(PureFill.None),
                strokeStyle = PureStrokeStyle(
                    color = PureColor.darkGray,
                ),
                pointerEvents = Cell.of(PurePointerEvents.None),
            ),
            points = ReactiveList.diff(
                bezierCurve.map { bezierCurveNow ->
                    bezierCurveNow.basisFunction.sample(
                        linSpace = LinSpace(
                            range = -maxT..maxT,
                            sampleCount = 1024,
                        )
                    ).map {
                        svgElement.createLegacySVGPoint(
                            x = it.b.x,
                            y = it.b.y,
                        )
                    }
                },
            ),
        )
    }


}
