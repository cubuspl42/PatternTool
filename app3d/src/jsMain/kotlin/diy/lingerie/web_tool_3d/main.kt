package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.BezierCurve
import diy.lingerie.web_tool_3d.application_state.ApplicationState
import diy.lingerie.web_tool_3d.application_state.DocumentState
import kotlinx.browser.document

private val apexVertex = Point3D(x = 0.0, y = 0.0, z = 1.0)

private val bezierCurve = BezierCurve(
    Point(x = 0.0, y = 1.0),
    Point(x = 0.5, y = 1.0),
    Point(x = 1.0, y = 0.5),
    Point(x = 1.0, y = 0.0),
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
