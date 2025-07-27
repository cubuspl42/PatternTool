package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.hold
import kotlinx.browser.document

private val apexVertex = Vector3(x = 0.0, y = 0.0, z = 1.0)

private val bezierCurve = CubicBezierBinomial(
    point0 = Vector2(x = 0.0, y = 1.0),
    point1 = Vector2(x = 0.5, y = 1.0),
    point2 = Vector2(x = 1.0, y = 0.5),
    point3 = Vector2(x = 1.0, y = 0.0),
)

val userSystem = UserSystem(
    userBezierMesh = UserBezierMesh.create(
        initialApexVertex = apexVertex,
        initialBezierCurve = bezierCurve,
    ),
)

fun Cell<Point>.trackTranslation(): Cell<PrimitiveTransformation.Translation> {
    val initialPoint = currentValue

    return newValues.map {
        initialPoint.translationTo(it)
    }.hold(
        initialValue = PrimitiveTransformation.Translation.None,
    )
}

fun main() {
    document.addEventListener(
        type = "DOMContentLoaded",
        callback = {
            document.body!!.appendChild(
                createRootElement(userSystem = userSystem),
            )
        },
    )
}
