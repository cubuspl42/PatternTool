package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.utils.DOMHighResTimeStamp
import dev.toolkt.dom.reactive.utils.requestAnimationFrames
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import three.THREE
import three.THREE.Object3D
import three.WebGLRendererParams
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun createReactivePerspectiveCamera(
    size: Cell<PureSize>,
    fov: Double,
    near: Double,
    far: Double,
): THREE.PerspectiveCamera {
    val camera = THREE.PerspectiveCamera(
        fov,
        size.currentValue.width / size.currentValue.height,
        near,
        far,
    )

    size.newValues.pipe(
        target = camera,
    ) { camera, sizeNow ->
        camera.aspect = sizeNow.width / sizeNow.height
        camera.updateProjectionMatrix()
    }

    return camera
}

fun createReactiveRenderer(
    canvas: HTMLCanvasElement,
    camera: THREE.Camera,
    size: Cell<PureSize>,
    buildScene: (time: Cell<Duration>) -> THREE.Scene,
): THREE.WebGLRenderer {
    val renderer = THREE.WebGLRenderer(
        WebGLRendererParams(
            canvas = canvas,
        ),
    )

    size.bind(
        target = renderer,
    ) { renderer, sizeNow ->
        renderer.setSize(sizeNow.width, sizeNow.height)
    }

    val mutableTime = MutableCell(initialValue = Duration.ZERO)

    val scene = buildScene(mutableTime)

    var initialTimestamp: DOMHighResTimeStamp? = null

    window.requestAnimationFrames { timestamp ->
        val previousTimestamp = when (val foundInitialTimestamp = initialTimestamp) {
            null -> {
                initialTimestamp = timestamp
                timestamp
            }

            else -> foundInitialTimestamp
        }

        mutableTime.set((timestamp - previousTimestamp).milliseconds)

        renderer.render(
            scene = scene,
            camera = camera,
        )
    }

    return renderer
}

fun createReactiveMesh(
    geometry: THREE.BufferGeometry,
    material: THREE.Material,
    rotation: Cell<THREE.Euler>,
): THREE.Mesh {
    val mesh = THREE.Mesh(
        geometry = geometry,
        material = material,
    )

    rotation.bind(
        target = mesh,
    ) { mesh, rotationNow ->
        mesh.rotation.x = rotationNow.x
        mesh.rotation.y = rotationNow.y
        mesh.rotation.z = rotationNow.z
    }

    return mesh
}

fun createReactiveScene(
    mainObject: Object3D,
): THREE.Scene = THREE.Scene().apply {
    add(mainObject)
}
