package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.core.math.sq
import three.Float32Array
import three.THREE
import three.toList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val R = 1.0

private const val n = 32

private const val m = 16

private const val apexVertexIndex = 0

fun createDomeGeometry(): THREE.BufferGeometry {
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

        val topFace = listOf(
            apexVertexIndex,
            getVertexIndex(i, 1),
            getVertexIndex(iNext, 1),
        )

        val quadFaces = (1 until m).flatMap { j ->
            val jNext = j + 1

            listOf(
                getVertexIndex(i, j),
                getVertexIndex(i, jNext),
                getVertexIndex(iNext, j),
                getVertexIndex(i, jNext),
                getVertexIndex(iNext, jNext),
                getVertexIndex(iNext, j)
            )
        }

        topFace + quadFaces
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

    return geometry
}

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

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int = 1 + i * m + (j - 1)
