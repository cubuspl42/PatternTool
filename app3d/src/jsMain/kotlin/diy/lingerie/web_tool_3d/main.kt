package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.reactive.utils.trackSize
import dev.toolkt.reactive.cell.Cell
import kotlinx.browser.document
import kotlinx.browser.window
import three.Float32Array
import three.MeshBasicMaterialParams
import three.THREE
import three.requestAnimationFrame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val R = 1.0
private const val n = 32
private const val m = 16

private fun buildVertex(
    apex: THREE.Vector3,
    i: Int,
    j: Int,
): THREE.Vector3 {
    val ir = i.toDouble() / n
    val jr = j.toDouble() / m
    val jr1 = 1 - jr

    // val r = sin(jr * PI / 2)
    val r = sqrt(R * R - jr1 * jr1)
    val fi = 2 * PI * ir

    val x = r * cos(fi)
    val y = r * sin(fi)
    val z = apex.z - apex.z * jr

    return THREE.Vector3(apex.x + x, apex.y + y, z)
}

private const val apexVertexIndex = 0

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int {
    return 1 + i * m + (j - 1)
}

fun createReactivePerspectiveCamera(
    size: Cell<PureSize>,
    fov: Double,
    near: Double,
    far: Double,
): THREE.PerspectiveCamera {
    val camera = THREE.PerspectiveCamera(
        75.0,
        size.currentValue.width / size.currentValue.height,
        near, far,
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
    size: Cell<PureSize>,
): THREE.WebGLRenderer {
    val renderer = THREE.WebGLRenderer()

    size.bind(
        target = renderer,
    ) { renderer, sizeNow ->
        renderer.setSize(sizeNow.width, sizeNow.height)
    }

    return renderer
}

fun main() {
    val size = window.trackSize()

    val scene = THREE.Scene()

    val camera = createReactivePerspectiveCamera(
        size = size,
        fov = 75.0,
        near = 0.1,
        far = 1000.0,
    )

    val renderer = createReactiveRenderer(
        size = size,
    )

    document.body?.appendChild(renderer.domElement)
    val apex = THREE.Vector3(0.0, 0.0, 1.0)

    val wireVertices = Array(n) { i ->
        Array(m) { j0 ->
            buildVertex(apex, i, j0 + 1)
        }
    }

    val wireFaces = (0 until n).flatMap { i ->
        val iNext = (i + 1) % n

        listOf(
            apexVertexIndex, getVertexIndex(i, 1), getVertexIndex(iNext, 1)
        ) + (1 until m).flatMap { j ->
            val jNext = j + 1

            listOf(
                getVertexIndex(i, j), getVertexIndex(i, jNext), getVertexIndex(iNext, j),
                getVertexIndex(i, jNext), getVertexIndex(iNext, jNext), getVertexIndex(iNext, j)
            )
        }
    }

    // Build flat vertex array for Three.js
    val apexArray = apex.toArray()
    val flatVertices = mutableListOf<Double>()
    flatVertices.addAll(apexArray)
    wireVertices.forEach { row ->
        row.forEach { vertex ->
            flatVertices.addAll(vertex.toArray())
        }
    }

    // Create Float32Array from the vertex data
    val vertices = Float32Array(flatVertices.toTypedArray())

    // Create a new BufferGeometry
    val geometry = THREE.BufferGeometry()

    // Create a BufferAttribute on the geometry
    geometry.setAttribute("position", THREE.BufferAttribute(vertices, 3))

    // Set the index buffer for the geometry
    geometry.setIndex(wireFaces.toTypedArray())

    // Compute normals for the faces, for proper lighting
    geometry.computeVertexNormals()

    // Create a material
    val material = THREE.MeshBasicMaterial(
        MeshBasicMaterialParams(
            color = 0xff00ff,
            wireframe = true,
        ),
    )

    // Create a mesh
    val mesh = THREE.Mesh(geometry, material)

    // Add the mesh to the scene
    scene.add(mesh)

    camera.position.z = 5.0

    fun animate() {
        requestAnimationFrame(::animate)
        val s = 0.005
        mesh.rotation.x += s
        mesh.rotation.y += s
        renderer.render(scene, camera)
    }

    animate()
}

private fun THREE.Vector3.toList(): List<Double> = listOf(
    this.x,
    this.y,
    this.z,
)

// Add DOM content loaded event listener
fun onDomContentLoaded() {
    main()
}

fun main(args: Array<String>) {
    document.addEventListener("DOMContentLoaded", { onDomContentLoaded() })
}
