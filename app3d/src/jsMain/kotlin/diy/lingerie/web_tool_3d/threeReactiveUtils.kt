package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.utils.DOMHighResTimeStamp
import dev.toolkt.dom.reactive.utils.requestAnimationFrames
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import three.THREE
import three.THREE.Object3D
import three.WebGLRendererParams
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun createReactiveGroup(
    position: Cell<THREE.Vector3>,
    rotation: Cell<THREE.Euler>,
    children: List<THREE.Object3D>,
): THREE.Group {
    val group = THREE.Group()

    position.bind(
        target = group,
        selector = THREE.Group::position,
    )

    rotation.bind(
        target = group,
        selector = THREE.Group::rotation,
    )

    children.forEach { child ->
        group.add(child)
    }

    return group
}

fun <TargetT : Any> Cell<THREE.Vector3>.bind(
    target: TargetT,
    selector: (TargetT) -> THREE.Vector3,
): Subscription = bind(
    target = target,
) { target, positionNow ->
    selector(target).apply {
        this.x = positionNow.x
        this.y = positionNow.y
        this.z = positionNow.z
    }
}

fun <TargetT : Any> Cell<THREE.Euler>.bind(
    target: TargetT,
    selector: (TargetT) -> THREE.Euler,
): Subscription = bind(
    target = target,
) { target, rotationNow ->
    selector(target).apply {
        this.x = rotationNow.x
        this.y = rotationNow.y
        this.z = rotationNow.z
    }
}

fun createReactivePerspectiveCamera(
    position: Cell<THREE.Vector3>,
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

    position.bind(
        target = camera,
    ) { camera, positionNow ->
        camera.position.x = positionNow.x
        camera.position.y = positionNow.y
        camera.position.z = positionNow.z
    }

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
    children: List<Object3D>,
): THREE.Scene = THREE.Scene().apply {
    children.forEach {
        add(it)
    }
}
