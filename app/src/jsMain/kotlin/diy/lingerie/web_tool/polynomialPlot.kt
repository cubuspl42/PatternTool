package diy.lingerie.web_tool

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgGroupElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgLineElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPolylineElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Rectangle
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.TransProjection
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.sample
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element
import svg.createLegacySVGPoint

internal fun createPolynomialPlot(
    polynomial: Cell<Polynomial>,
): Element = createResponsiveElement { size ->
    ReactiveList.looped { childrenLooped ->
        val svgElement = document.createReactiveSvgSvgElement(
            style = ReactiveStyle(
                width = Cell.of(100.percent),
                height = Cell.of(100.percent),
            ),
            children = childrenLooped,
        )

        val children = ReactiveList.of(
            document.createReactiveSvgGroupElement(
                svgElement = svgElement,
                transformation = size.map {
                    PrimitiveTransformation.Translation(
                        tx = 0.0,
                        ty = it.height / 2.0,
                    )
                },
                children = ReactiveList.of(
                    document.createReactiveSvgPolylineElement(
                        style = ReactiveStyle(
                            fill = Cell.of(PureFill.None),
                            strokeStyle = PureStrokeStyle(
                                color = PureColor.blue,
                            ),
                        ),
                        points = ReactiveList.diff(
                            Cell.map2(
                                size,
                                polynomial,
                            ) { sizeNow, polynomialNow ->
                                buildPolynomialGeometricPlot(
                                    polynomialNow,
                                    sizeNow,
                                ).map {
                                    svgElement.createLegacySVGPoint(point = it)
                                }
                            },
                        ),
                    ),
                    document.createReactiveSvgLineElement(
                        style = ReactiveStyle(
                            strokeStyle = PureStrokeStyle(
                                color = PureColor.darkGray,
                            ),
                        ),
                        start = Cell.of(Point.origin),
                        end = size.map {
                            Point(
                                x = it.width,
                                y = 0.0,
                            )
                        },
                    ),
                ),
            ),
        )

        return@looped Pair(
            svgElement,
            children,
        )
    }
}

private fun buildPolynomialGeometricPlot(
    polynomial: Polynomial,
    plotSize: PureSize,
): List<Point> {
    val yMax = 1e9

    val transProjection = TransProjection(
        sourceRectangle = Rectangle.of(
            xMin = 0.0,
            xMax = 1.0,
            yMin = -yMax,
            yMax = yMax,
        ),
        targetRectangle = Rectangle.of(
            xMin = 0.0,
            xMax = plotSize.width,
            yMin = -plotSize.height / 2,
            yMax = plotSize.height / 2,
        ),
    )

    return polynomial.sample(
        linSpace = LinSpace(sampleCount = 1024),
    ).map {
        transProjection.transform(
            x = it.a,
            y = it.b,
        )
    }
}
