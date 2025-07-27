package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.reactive.cell.Cell
import three.Float32Array
import three.THREE

private const val n = 32

private const val m = 16

private const val apexVertexIndex = 0

fun createUserBezierMeshGeometryData(
    userBezierMesh: UserBezierMesh,
): Cell<GeometryData> = Cell.map2(
    userBezierMesh.bezierCurve,
    userBezierMesh.apexVertex,
) { bezierCurveNow, apexVertexNow ->
    fun buildVertex(
        i: Int,
        j: Int,
    ): Vector3 {
        val ir = i.toDouble() / n
        val jr = j.toDouble() / m

        val baseVertex = bezierCurveNow.apply(ir).toVector3(0.0)

        return apexVertexNow + jr * (baseVertex - apexVertexNow)
    }

    val wireVertices = (0 until n).flatMap { i ->
        (1..m).map { j ->
            buildVertex(
                i = i,
                j = j,
            )
        }
    }

    val wireFaces = (0 until n).flatMap { i ->
        val iNext = (i + 1) % n

        val topFace = GeometryData.Face(
            getVertexIndex(iNext, 1),
            getVertexIndex(i, 1),
            apexVertexIndex,
        )

        val quadFaces = (1 until m).flatMap { j ->
            val jNext = j + 1

            listOf(
                GeometryData.Face(
                    getVertexIndex(iNext, j),
                    getVertexIndex(iNext, jNext),
                    getVertexIndex(i, jNext),
                ),
                GeometryData.Face(
                    getVertexIndex(iNext, j),
                    getVertexIndex(i, jNext),
                    getVertexIndex(i, j),
                ),
            )
        }

        listOf(topFace) + quadFaces
    }

    return@map2 GeometryData(
        vertices = listOf(apexVertexNow) + wireVertices,
        faces = wireFaces,
    )
}

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int = 1 + i * m + (j - 1)
