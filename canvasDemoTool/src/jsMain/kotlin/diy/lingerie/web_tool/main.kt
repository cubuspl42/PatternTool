package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasCircleElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasGroupElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasPathElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasPolylineElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.createReactiveCanvasElement
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveFlexElement
import dev.toolkt.dom.reactive.utils.createTimeoutStream
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.accum
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    val rootElement = createRootElement()

    document.body!!.apply {
        appendChild(rootElement)
    }
}

private fun createRootElement(): HTMLDivElement {
    val radius = createTimeoutStream(delay = 16.milliseconds).accum(
        initialValue = 50.0,
        transform = { acc, _ -> acc + 1.0 },
    )

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    direction = PureFlexDirection.Column,
                    alignItems = PureFlexAlignItems.Stretch,
                ),
            ),
            width = Cell.of(PureUnit.Vw.full),
            height = Cell.of(PureUnit.Vh.full),
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
            createResponsiveFlexElement { size ->
                createReactiveCanvasElement(
                    size = size,
                    root = CanvasGroupElement(
                        children = ReactiveList.of(
                            CanvasCircleElement(
                                fill = Cell.of(
                                    CanvasPathElement.CanvasFill(
                                        color = PureColor.red,
                                    ),
                                ),
                                stroke = null,
                                radius = radius,
                            ),
                            CanvasPolylineElement(
                                stroke = Cell.of(
                                    CanvasPathElement.CanvasStroke.Default,
                                ),
                                points = ReactiveList.of(
                                    Point(x = 100.0, y = 100.0),
                                    Point(x = 200.0, y = 200.0),
                                    Point(x = 300.0, y = 100.0),
                                ),
                            ),
                        ),
                    ),
                )
            },
        ),
    )
}
