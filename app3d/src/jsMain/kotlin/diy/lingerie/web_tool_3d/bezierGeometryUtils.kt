package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.reactive.cell.Cell

private const val n = 32

private const val m = 16

private const val apexVertexIndex = 0

fun createUserBezierMeshGeometryData(
    userBezierMesh: UserBezierMesh,
): Cell<GeometryData> = Cell.map2(
    userBezierMesh.bezierCurve,
    userBezierMesh.apexPosition,
) { bezierCurveNow, apexPositionNow ->
    fun buildVertex(
        i: Int,
        j: Int,
    ): Point3D {
        val ir = i.toDouble() / n
        val jr = j.toDouble() / m

        val basePoint = bezierCurveNow.evaluate(
            OpenCurve.Coord(ir),
        ).toPoint3D()

        return Point3D.interpolate(
            start = apexPositionNow,
            end = basePoint,
            ratio = jr,
        )
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
        vertices = listOf(apexPositionNow) + wireVertices,
        faces = wireFaces,
    )
}

private fun getVertexIndex(
    i: Int,
    j: Int,
): Int = 1 + i * m + (j - 1)
