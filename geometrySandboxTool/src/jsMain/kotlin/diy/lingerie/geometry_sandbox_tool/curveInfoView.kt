package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlSpanElement
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element

internal fun createCurveInfoView(
    userCurve: UserCurve<*>,
    intersectionPolynomial: Cell<Polynomial>,
): Element = document.createReactiveHtmlDivElement(
    style = ReactiveStyle(
        flexItemStyle = PureFlexItemStyle(
            grow = 1.0,
        ),
        displayStyle = Cell.of(
            PureFlexStyle(
                direction = PureFlexDirection.Column,
            ),
        ),
    ),
    children = ReactiveList.of(
        document.createReactiveHtmlSpanElement(
            children = ReactiveList.of(
                document.createReactiveTextNode(
                    userCurve.primitiveCurve.map {
                        it.basisFunction.toReprString()
                    },
                ),
            ),
        ),
        document.createReactiveHtmlSpanElement(
            children = ReactiveList.of(
                document.createReactiveTextNode(
                    intersectionPolynomial.map {
                        it.coefficients.toString()
                    },
                ),
            ),
        ),
        createCanvasPolynomialPlot(
            polynomial = intersectionPolynomial,
        ),
    ),
)
