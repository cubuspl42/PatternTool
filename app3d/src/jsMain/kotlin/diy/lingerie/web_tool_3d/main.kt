package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import diy.lingerie.web_tool_3d.application_state.ApplicationState
import diy.lingerie.web_tool_3d.application_state.DocumentState
import kotlinx.browser.document

private val apexVertex = Vector3(x = 0.0, y = 0.0, z = 1.0)

private val bezierCurve = CubicBezierBinomial(
    point0 = Vector2(x = 0.0, y = 1.0),
    point1 = Vector2(x = 0.5, y = 1.0),
    point2 = Vector2(x = 1.0, y = 0.5),
    point3 = Vector2(x = 1.0, y = 0.0),
)

private val documentState = DocumentState(
    userBezierMesh = UserBezierMesh.create(
        initialApexVertex = apexVertex,
        initialBezierCurve = bezierCurve,
    ),
)

private val applicationState = ApplicationState(
    documentState = documentState,
)


fun main() {
    document.addEventListener(
        type = "DOMContentLoaded",
        callback = {
            document.body!!.appendChild(
                createRootElement(applicationState = applicationState),
            )
        },
    )
}
