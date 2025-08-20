package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlSpanComponent
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

context(momentContext: MomentContext) internal fun createCurveInfoView(
    userCurve: UserCurve<*>,
    intersectionPolynomial: Cell<Polynomial>,
): Component<HTMLDivElement> = document.createReactiveHtmlDivComponent(
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
        document.createReactiveHtmlSpanComponent(
            children = ReactiveList.of(
                document.createReactiveTextComponent(
                    userCurve.primitiveCurve.map {
                        it.basisFunction.toReprString()
                    },
                ),
            ),
        ),
        document.createReactiveHtmlSpanComponent(
            children = ReactiveList.of(
                document.createReactiveTextComponent(
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
