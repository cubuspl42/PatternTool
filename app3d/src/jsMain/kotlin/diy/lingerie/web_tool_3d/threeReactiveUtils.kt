package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.utils.DOMHighResTimeStamp
import dev.toolkt.dom.reactive.utils.requestAnimationFrames
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.linear.vectors.Vector3
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
    position: Cell<Vector3>,
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

fun <TargetT : Any> Cell<Vector3>.bind(
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
    position: Cell<Vector3>,
    rotation: Cell<THREE.Euler>,
    size: Cell<PureSize>,
    fov: Double,
    near: Double,
    far: Double,
): THREE.PerspectiveCamera {
    val camera = THREE.PerspectiveCamera(
        fov,
        1.0,
        near,
        far,
    )

    size.bind(
        target = camera,
    ) { camera, sizeNow ->
        camera.aspect = sizeNow.width / sizeNow.height
        camera.updateProjectionMatrix()
    }

    position.bind(
        target = camera,
        selector = THREE.Camera::position,
    )

    rotation.bind(
        target = camera,
        selector = THREE.Camera::rotation,
    )

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
    position: Cell<Vector3>? = null,
    rotation: Cell<THREE.Euler>? = null,
): THREE.Mesh {
    val mesh = THREE.Mesh(
        geometry = geometry,
        material = material,
    )

    position?.bind(
        target = mesh,
        selector = THREE.Object3D::position,
    )

    rotation?.bind(
        target = mesh,
        selector = THREE.Object3D::rotation,
    )

    return mesh
}

fun createReactiveDualMeshGroup(
    geometry: THREE.BufferGeometry,
    primaryMaterial: THREE.Material,
    secondaryMaterial: THREE.Material,
    position: Cell<Vector3>,
    rotation: Cell<THREE.Euler>,
    secondaryScale: Double = 1.001,
): THREE.Group = createReactiveGroup(
    position = position,
    rotation = rotation,
    children = listOf(
        THREE.Mesh(
            geometry = geometry,
            material = primaryMaterial,
        ),
        THREE.Mesh(
            geometry = geometry,
            material = secondaryMaterial,
        ).apply {
            scale.x = secondaryScale
            scale.y = secondaryScale
            scale.z = secondaryScale
        },
    ),
)

fun createReactiveAmbientLight(
    position: Cell<Vector3>,
): THREE.Light {
    val light = THREE.PointLight(0xff0000, 1.0, 100.0)

    position.bind(
        target = light,
        selector = THREE.PointLight::position,
    )

    return light
}


fun createReactivePointLight(
    position: Cell<Vector3>,
): THREE.Light {
    val light = THREE.PointLight(0xff0000, 1.0, 100.0)

    position.bind(
        target = light,
        selector = THREE.PointLight::position,
    )

    return light
}

fun createReactiveScene(
    children: List<Object3D>,
): THREE.Scene = THREE.Scene().apply {
    children.forEach {
        add(it)
    }
}
