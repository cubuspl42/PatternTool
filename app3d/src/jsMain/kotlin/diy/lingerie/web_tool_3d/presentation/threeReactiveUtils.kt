package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.utils.DOMHighResTimeStamp
import dev.toolkt.dom.reactive.utils.requestAnimationFrames
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import three.Float32Array
import three.THREE
import three.THREE.Object3D
import three.Uint16Array
import three.WebGLRendererParams
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun createReactiveGroup(
    position: Cell<Point3D>? = null,
    rotation: Cell<THREE.Euler>? = null,
    children: List<Object3D>,
): THREE.Group {
    val group = THREE.Group()

    position?.bind(
        target = group,
        selector = THREE.Group::position,
    )

    rotation?.bind(
        target = group,
        selector = THREE.Group::rotation,
    )

    children.forEach { child ->
        group.add(child)
    }

    return group
}

fun <TargetT : Any> Cell<Point3D>.bind(
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
    position: Cell<Point3D>,
    rotation: Cell<THREE.Euler>,
    size: Cell<PureSize>,
    /**
     * Vertical field of view (in degrees)
     */
    fov: Double,
    near: Double,
    far: Double,
): THREE.PerspectiveCamera {
    val camera = THREE.PerspectiveCamera(
        fov,
        1.0, // Temporary value, will be adjusted dynamically
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
    viewportSize: Cell<PureSize>,
    clearColor: PureColor = PureColor.darkGray,
    buildScene: (time: Cell<Duration>) -> THREE.Scene,
): THREE.WebGLRenderer {
    val renderer = THREE.WebGLRenderer(
        WebGLRendererParams(
            canvas = canvas,
        ),
    ).apply {
        setClearColor(clearColor.value)
    }

    viewportSize.bind(
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

data class GeometryData(
    val vertices: List<Point3D>,
    val faces: List<Face>,
) {
    data class Face(
        val index0: Int,
        val index1: Int,
        val index2: Int,
    ) {
        fun toList(): List<Int> = listOf(
            index0,
            index1,
            index2,
        )
    }

    val flatVertices: Array<Double>
        get() = vertices.flatMap { it.pointVector.toList() }.toTypedArray()

    val flatFaces: Array<Int>
        get() = faces.flatMap { it.toList() }.toTypedArray()
}

fun createReactiveGeometry(
    geometryData: Cell<GeometryData>,
): THREE.BufferGeometry {
    val nativeVerticesArray = Float32Array(geometryData.currentValue.flatVertices)

    val positionAttribute = THREE.BufferAttribute(
        array = nativeVerticesArray,
        itemSize = 3,
    )

    val nativeFacesArray = Uint16Array(geometryData.currentValue.flatFaces)

    val indexAttribute = THREE.BufferAttribute(
        array = nativeFacesArray,
        itemSize = 1,
    )

    geometryData.newValues.forEach {
        nativeVerticesArray.set(it.flatVertices)
        positionAttribute.needsUpdate = true

        nativeFacesArray.set(it.flatFaces)
        indexAttribute.needsUpdate = true
    }

    return THREE.BufferGeometry().apply {
        setAttribute(
            "position",
            positionAttribute,
        )

        // Set the index buffer for the geometry
        setIndex(indexAttribute)

        // Compute normals for the faces, for proper lighting
        computeVertexNormals()
    }
}

fun createReactiveMesh(
    geometry: THREE.BufferGeometry,
    material: Cell<THREE.Material>,
    userData: Any? = null,
    position: Cell<Point3D>? = null,
    rotation: Cell<THREE.Euler>? = null,
): THREE.Mesh {
    val mesh = THREE.Mesh(
        geometry = geometry,
    )

    material.bind(
        target = mesh,
    ) { mesh, materialNow ->

        mesh.material = materialNow
    }

    if (userData != null) {
        mesh.userData = userData
    }

    position?.bind(
        target = mesh,
        selector = Object3D::position,
    )

    rotation?.bind(
        target = mesh,
        selector = Object3D::rotation,
    )

    return mesh
}

fun createReactiveDualMeshGroup(
    geometry: THREE.BufferGeometry,
    primaryMaterial: THREE.Material,
    secondaryMaterial: THREE.Material,
    position: Cell<Point3D>,
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
    position: Cell<Point3D>,
): THREE.Light {
    val light = THREE.PointLight(0xff0000, 1.0, 100.0)

    position.bind(
        target = light,
        selector = THREE.PointLight::position,
    )

    return light
}


fun createReactivePointLight(
    position: Cell<Point3D>,
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
