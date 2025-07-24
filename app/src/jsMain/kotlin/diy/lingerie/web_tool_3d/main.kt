package diy.lingerie.web_tool_3d

import dev.toolkt.core.platform.PlatformSystem
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

fun main() {
    val scene = THREE.Scene()
    val camera = THREE.PerspectiveCamera(
        75.0, window.innerWidth.toDouble() / window.innerHeight.toDouble(), 0.1, 1000.0
    )

    val renderer = THREE.WebGLRenderer()
    renderer.setSize(window.innerWidth.toDouble(), window.innerHeight.toDouble())
    document.body?.appendChild(renderer.domElement)

    fun onWindowResize() {
        camera.aspect = window.innerWidth.toDouble() / window.innerHeight.toDouble()
        camera.updateProjectionMatrix()
        renderer.setSize(window.innerWidth.toDouble(), window.innerHeight.toDouble())
    }

    window.addEventListener("resize", { onWindowResize() }, false)

    val apex = THREE.Vector3(0.0, 0.0, 1.0)

    val wireVertices = Array(n) { i ->
        Array(m) { j0 ->
            buildVertex(apex, i, j0 + 1)
        }
    }

    val wireFaces = Array(n) { i ->
        val iNext = (i + 1) % n
        val faces = mutableListOf<Int>()

        // Add the top faces
        faces.addAll(
            listOf(
                apexVertexIndex, getVertexIndex(i, 1), getVertexIndex(iNext, 1)
            )
        )

        // Add the rest of the faces
        for (j0 in 0 until m - 1) {
            val j = j0 + 1
            val jNext = j + 1

            faces.addAll(
                listOf(
                    getVertexIndex(i, j), getVertexIndex(i, jNext), getVertexIndex(iNext, j)
                )
            )

            faces.addAll(
                listOf(
                    getVertexIndex(i, jNext), getVertexIndex(iNext, jNext), getVertexIndex(iNext, j)
                )
            )
        }

        faces.toTypedArray()
    }.flatten()

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
            color = 0xff0000,
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

// Add DOM content loaded event listener
fun onDomContentLoaded() {
    main()
}

fun main(args: Array<String>) {
    document.addEventListener("DOMContentLoaded", { onDomContentLoaded() })
}
