package diy.lingerie.web_tool

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasGroupElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasLineElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasPathElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.CanvasPolylineElement
import dev.toolkt.dom.reactive.extra.reactive_canvas.createReactiveCanvasElement
import dev.toolkt.dom.reactive.utils.createResponsiveFlexElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Rectangle
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.TransProjection
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.sample
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

fun createCanvasPolynomialPlot(
    polynomial: Cell<Polynomial>,
): Element = createResponsiveFlexElement { size ->
    createReactiveCanvasElement(
        size = size,
        root = CanvasGroupElement(
            transformation = size.map {
                PrimitiveTransformation.Translation(
                    tx = 0.0,
                    ty = it.height / 2.0,
                )
            },
            children = ReactiveList.of(
                CanvasPolylineElement(
                    stroke = Cell.of(
                        CanvasPathElement.CanvasStroke(
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
                            )
                        },
                    ),
                ),
                CanvasLineElement(
                    stroke = Cell.of(
                        CanvasPathElement.CanvasStroke(
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
}

private fun buildPolynomialGeometricPlot(
    polynomial: Polynomial,
    plotSize: PureSize,
): List<Point> {
    val yMax = 1e14

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
