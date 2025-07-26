package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.times
import three.Float32Array
import three.THREE

private val apexVertex = Vector3(0.0, 0.0, 1.0)

private const val n = 32

private const val m = 16

private const val apexVertexIndex = 0

private val bezierCurve = CubicBezierBinomial(
    point0 = Vector2(0.0, 1.0),
    point1 = Vector2(0.5, 1.0),
    point2 = Vector2(1.0, 0.5),
    point3 = Vector2(1.0, 0.0),
)

fun createBezierGeometry(): THREE.BufferGeometry {
    val wireVertices = (0 until n).flatMap { i ->
        (1..m).map { j ->
            buildVertex(
                i = i,
                j = j,
            )
        }
    }

    // Build flat vertex array for Three.js
    val flatVertices = listOf(apexVertex) + wireVertices

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
                array = Float32Array(
                    flatVertices.flatMap { it.toList() }.toTypedArray()
                ),
                itemSize = 3,
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
    i: Int,
    j: Int,
): Vector3 {
    val ir = i.toDouble() / n
    val jr = j.toDouble() / m

    val baseVertex = bezierCurve.apply(ir).toVector3(0.0)

    return apexVertex + jr * (baseVertex - apexVertex)
}

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int = 1 + i * m + (j - 1)
