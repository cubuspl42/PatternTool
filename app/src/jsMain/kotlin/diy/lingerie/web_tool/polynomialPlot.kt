package diy.lingerie.web_tool

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgGroupElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPolylineElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.sample
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.svg.SVGSVGElement
import svg.createLegacySVGPoint

internal fun createPolynomialPlot(
    polynomial: Cell<Polynomial>,
): SVGSVGElement = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
        children = childrenLooped,
    )

    val children = ReactiveList.of(
        document.createReactiveSvgGroupElement(
            children = ReactiveList.of(
                document.createReactiveSvgPolylineElement(
                    points = ReactiveList.diff(
                        polynomial.map { polynomialNow ->
                            polynomialNow.sample(
                                linSpace = LinSpace(sampleCount = 1024),
                            ).map {
                                svgElement.createLegacySVGPoint(
                                    x = it.a,
                                    y = it.b,
                                )
                            }
                        },
                    ),
                ),
            ),
        ),
    )

    return@looped Pair(
        svgElement,
        children,
    )
}
