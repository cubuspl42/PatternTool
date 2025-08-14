package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.js.Float32Array
import dev.toolkt.js.threejs.THREE
import dev.toolkt.js.threejs.toList

private const val cornerCount = 4

value class LineGridGeometry(
    val geometry: THREE.BufferGeometry,
)

fun createLineGridGeometry(
    /**
     * The width of the grid in fragment units
     */
    sideWidth: Int,
): LineGridGeometry {
    val n = sideWidth / 2
    val nD = sideWidth.toDouble()

    // The count of vertices on a single side (not including corners)
    val m = 2 * n - 1

    // Corner vertices
    val corner0 = THREE.Vector3(-nD, -nD, 0.0)
    val corner1 = THREE.Vector3(nD, -nD, 0.0)
    val corner2 = THREE.Vector3(nD, nD, 0.0)
    val corner3 = THREE.Vector3(-nD, nD, 0.0)

    val sideLines = listOf(
        // Top side
        corner0,
        corner1,
        // Right side
        corner1,
        corner2,
        // Bottom side
        corner2,
        corner3,
        // Left side
        corner3,
        corner0,
    )

    val verticalLines = (-m..m).flatMap { j ->
        val jD = j.toDouble()

        listOf(
            THREE.Vector3(jD, -nD, 0.0),
            THREE.Vector3(jD, nD, 0.0),
        )
    }

    val horizontalLines = (-m..m).flatMap { i ->
        val iD = i.toDouble()

        listOf(
            THREE.Vector3(-nD, iD, 0.0),
            THREE.Vector3(nD, iD, 0.0),
        )
    }

    val vertices = sideLines + verticalLines + horizontalLines

    val geometry = THREE.BufferGeometry().apply {
        setAttribute(
            name = "position",
            attribute = THREE.BufferAttribute(
                array = Float32Array(
                    vertices.flatMap { it.toList() }.toTypedArray(),
                ),
                itemSize = 3,
            ),
        )
    }

    return LineGridGeometry(
        geometry = geometry,
    )
}

fun createLineGridObject(
    lineGridGeometry: LineGridGeometry,
    lineMaterial: THREE.LineBasicMaterial,
): THREE.LineSegments {
    val s = 4.0

    return THREE.LineSegments(
        geometry = lineGridGeometry.geometry,
        material = lineMaterial,
    ).apply {
        scale.x = s
        scale.y = s
        scale.z = s
    }
}
