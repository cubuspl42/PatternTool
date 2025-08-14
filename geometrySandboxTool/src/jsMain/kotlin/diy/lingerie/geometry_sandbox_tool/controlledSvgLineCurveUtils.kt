package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgGroupElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGSVGElement

fun createControlledSvgLineCurve(
    svgElement: SVGSVGElement,
    userLineSegment: UserLineSegment,
    color: PureColor,
): SVGGElement = document.createReactiveSvgGroupElement(
    svgElement = svgElement,
    transformation = null,
    children = ReactiveList.Companion.of(
        userLineSegment.reactiveLineSegment.createReactiveSvgLineElement(
            style = ReactiveStyle(
                fill = Cell.Companion.of(PureFill.None),
                strokeStyle = PureStrokeStyle(
                    color = color,
                    width = 1.px,
                ),
            ),
        ),
        createCircleHandleElement(
            container = svgElement,
            position = userLineSegment.start,
        ),
        createCircleHandleElement(
            container = svgElement,
            position = userLineSegment.end,
        ),
    ),
)
