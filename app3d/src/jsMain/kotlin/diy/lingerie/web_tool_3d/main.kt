package diy.lingerie.web_tool_3d

import dev.toolkt.core.math.sq
import dev.toolkt.dom.reactive.utils.trackSize
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
import kotlin.random.Random

private const val R = 1.0
private const val n = 32
private const val m = 16

private fun buildVertex(
    apex: THREE.Vector3,
    i: Int,
    j: Int,
): THREE.Vector3 {
    val ir = i.toDouble() / n // i ratio
    val jr = j.toDouble() / m // j ratio

    val r = sqrt(R.sq - (1 - jr).sq)
    val fi = 2 * PI * ir

    val x = r * cos(fi)
    val y = r * sin(fi)
    val z = apex.z * (1 - jr)

    return THREE.Vector3(apex.x + x, apex.y + y, z)
}

private const val apexVertexIndex = 0

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int {
    return 1 + i * m + (j - 1)
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

    val wireVertices = (0 until n).flatMap { i ->
        (1..m).flatMap { j ->
            buildVertex(apex, i, j).toList()
        }
    }

    // Build flat vertex array for Three.js
    val flatVertices = apex.toList() + wireVertices

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

    // Create a new BufferGeometry
    val geometry = THREE.BufferGeometry().apply {
        // Create a BufferAttribute on the geometry
        setAttribute(
            "position",
            THREE.BufferAttribute(
                Float32Array(flatVertices.toTypedArray()), 3
            ),
        )

        // Set the index buffer for the geometry
        setIndex(wireFaces.toTypedArray())

        // Compute normals for the faces, for proper lighting
        computeVertexNormals()
    }

    val color = Random(3).nextInt()

    // Create a material
    val material = THREE.MeshBasicMaterial(
        MeshBasicMaterialParams(
            color = color,
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
