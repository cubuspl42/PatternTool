package diy.lingerie.web_tool_3d

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBlockStyle
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
import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.geometry.negateY
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.minus
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
import kotlin.random.Random

private const val colorId = 20

private const val cameraDistance = 2.0

private const val cameraZ = 0.5

private val apexVertex = Vector3(x = 0.0, y = 0.0, z = 1.0)

private val lightPosition = Vector3(x = 1.0, y = 1.0, z = 1.0)

private val bezierCurve = CubicBezierBinomial(
    point0 = Vector2(x = 0.0, y = 1.0),
    point1 = Vector2(x = 0.5, y = 1.0),
    point2 = Vector2(x = 1.0, y = 0.5),
    point3 = Vector2(x = 1.0, y = 0.0),
)

private val bezierGeometryFactory = BezierGeometryFactory(
    apexVertex = apexVertex,
    bezierCurve = bezierCurve,
)

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

fun createRendererElement(): HTMLElement = createResponsiveElement(
    style = ReactiveStyle(
        displayStyle = Cell.of(
            PureBlockStyle(),
        ),
        width = Cell.of(100.percent),
        height = Cell.of(100.percent),
    ),
) { size ->
    val canvas = document.createReactiveHtmlCanvasElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureBlockStyle(),
            ),
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
    )

    val color = Random(colorId).nextInt()

    val cameraRotation = PropertyCell(
        initialValue = 0.0,
    )

    val myCamera = createMyCamera(
        height = cameraZ,
        distance = cameraDistance,
        viewportSize = size,
        rotation = cameraRotation,
    )

    val camera = myCamera.camera

    val handleBalls = listOf(
        buildHandleBallMesh(
            position = Cell.of(bezierCurve.point0.toVector3(0.0)),
        ),
        buildHandleBallMesh(
            position = Cell.of(bezierCurve.point1.toVector3(0.0)),
        ),
        buildHandleBallMesh(
            position = Cell.of(bezierCurve.point2.toVector3(0.0)),
        ),
        buildHandleBallMesh(
            position = Cell.of(bezierCurve.point3.toVector3(0.0)),
        ),
        buildHandleBallMesh(
            position = Cell.of(apexVertex),
        ),
    )

    val bezierMeshGroup = createReactiveDualMeshGroup(
        position = Cell.of(Vector3.Zero),
        geometry = bezierGeometryFactory.createGeometry(),
        primaryMaterial = THREE.MeshLambertMaterial(
            params = MeshLambertMaterialParams(
                color = color,
            ),
        ),
        secondaryMaterial = THREE.MeshBasicMaterial(
            MeshBasicMaterialParams(
                color = PureColor.green.value,
                wireframe = true,
                transparent = true,
                opacity = 0.25,
            ),
        ),
        rotation = Cell.of(THREE.Euler()),
    )

    val floor = buildFloor()

    canvas.onMouseDragGestureStarted(
        button = ButtonId.MIDDLE,
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

    canvas.onMouseDragGestureStarted(
        button = ButtonId.LEFT,
    ).forEach { mouseGesture ->
        val gesturePosition = Cell.map2(
            cell1 = mouseGesture.offsetPosition,
            cell2 = size,
        ) {
                offsetPositionNow,
                sizeNow,
            ->
            offsetPositionNow.toNdc(size = sizeNow)
        }

        val initialGesturePosition = gesturePosition.currentValue

        val intersections = THREE.Raycaster().apply {
            setFromCamera(
                pointer = initialGesturePosition.toThreeVector2(),
                camera = camera,
            )
        }.intersectObjects(
            objects = handleBalls.toTypedArray(),
        )

        PlatformSystem.log(intersections)
    }

    createReactiveRenderer(
        canvas = canvas,
        camera = camera,
        viewportSize = size,
    ) { time ->
        createReactiveScene(
            listOf(
                THREE.AmbientLight(),
                createReactivePointLight(
                    position = Cell.of(lightPosition),
                ),
                bezierMeshGroup,
                myCamera.wrapperGroup,
                floor,
            ) + handleBalls,
        )
    }

    return@createResponsiveElement canvas
}

fun Point.toNdc(size: PureSize): Vector2 = (size.relativize(this) * 2.0 - 1.0).negateY()

fun Vector2.toThreeVector2(): THREE.Vector2 = THREE.Vector2(
    x = x,
    y = y,
)

fun Point.toThreeVector2(): THREE.Vector2 = THREE.Vector2(
    x = x,
    y = y,
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
            document.body!!.appendChild(createRootElement())
        },
    )
}
