package diy.lingerie.cup_layout_tool

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.joinOf
import diy.lingerie.cup_layout_tool.application_state.ApplicationState
import diy.lingerie.cup_layout_tool.application_state.DocumentState
import diy.lingerie.cup_layout_tool.presentation.createRootComponent
import kotlinx.browser.document
import kotlinx.browser.window

private val apexVertex = Point3D(x = 0.0, y = 0.0, z = 100.0)

private val bezierCurve = BezierCurve(
    Point(x = 0.0, y = 100.0),
    Point(x = 50.0, y = 100.0),
    Point(x = 100.0, y = 50.0),
    Point(x = 100.0, y = 0.0),
)

fun main() {
    val windowDynamic: dynamic = window
    windowDynamic.PlatformSystem = PlatformSystem

    document.addEventListener(
        type = "DOMContentLoaded",
        callback = {
            document.body!!.appendChild(
                Actions.external {
                    val documentState = DocumentState(
                        userBezierMesh = UserBezierMesh.create(
                            initialApexVertex = apexVertex,
                            initialBezierCurve = bezierCurve,
                        ),
                    )

                    ApplicationState.enter(
                        documentState = documentState,
                    ).joinOf { applicationState ->
                        createRootComponent(
                            applicationState = applicationState,
                        ).buildLeaf()
                    }.start().result
                },
            )
        },
    )
}
