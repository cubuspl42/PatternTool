package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createResponsiveElement
import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlCanvasElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import three.MeshBasicMaterialParams
import three.MeshLambertMaterialParams
import three.THREE
import kotlin.math.PI
import kotlin.random.Random

private const val colorId = 20

private const val cameraDistance = 2.0

private const val cameraZ = 0.5

private val lightPosition = Vector3(x = 1.0, y = 1.0, z = 1.0)

private fun createRootElement(): HTMLDivElement = document.createReactiveHtmlDivElement(
    style = ReactiveStyle(
        displayStyle = Cell.of(
            PureFlexStyle(
                direction = PureFlexDirection.Column,
            ),
        ),
        boxSizing = PureBoxSizing.BorderBox,
        borderStyle = PureBorderStyle(
            width = 4.px,
            color = PureColor.darkGray,
            style = PureBorderStyle.Style.Solid,
        ),
        width = Cell.of(PureUnit.Vw.full),
        height = Cell.of(PureUnit.Vh.full),
        backgroundColor = Cell.of(PureColor.lightGray),
    ),
    children = ReactiveList.of(
        createRendererElement(),
    ),
)

fun createRendererElement(): HTMLElement = createResponsiveElement { size ->
    val canvas = document.createReactiveHtmlCanvasElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    grow = 1.0,
                ),
            ),
        ),
    )

    val geometry = createBezierGeometry()

    val color = Random(colorId).nextInt()

    // Create a material
    val material = THREE.MeshLambertMaterial(
        MeshLambertMaterialParams(
            color = color,
        ),
    )

    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )

    val camera = createReactivePerspectiveCamera(
        position = Cell.of(
            Vector3(
                x = 0.0,
                y = -cameraDistance,
                z = 0.0,
            ),
        ),
        rotation = Cell.of(
            THREE.Euler(
                x = PI / 2,
            ),
        ),
        size = size,
        fov = 75.0,
        near = 0.1,
        far = 1000.0,
    )

    val cameraGroup = createReactiveGroup(
        position = Cell.of(Vector3(x = 0.0, y = 0.0, z = cameraZ)),
        rotation = cameraRotation.map { THREE.Euler(z = it) },
        children = listOf(camera),
    )

    canvas.onMouseDragGestureStarted(
        button = ButtonId.LEFT,
    ).forEach { mouseGesture ->
        val initialCameraRotation = cameraRotation.currentValue

        cameraRotation.bindUntil(
            boundValue = mouseGesture.offsetPosition.trackTranslation().map { translation ->
                val delta = translation.tx * 0.01

                initialCameraRotation + delta
            },
            until = mouseGesture.onFinished,
        )

        mouseGesture.offsetPosition
    }

    createReactiveRenderer(
        canvas = canvas,
        camera = camera,
        size = size,
    ) { time ->
        createReactiveScene(
            listOf(
                THREE.AmbientLight(),
                createReactivePointLight(
                    position = Cell.of(lightPosition),
                ),
                createReactiveMesh(
                    geometry = geometry,
                    material = material,
                    rotation = Cell.of(THREE.Euler()),
                ),
                cameraGroup,
            ),
        )
    }

    return@createResponsiveElement canvas
}

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
            document.body!!.appendChild(createRootElement())
        },
    )
}
