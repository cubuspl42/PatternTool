package diy.lingerie.algebra.linear.matrices.matrix4

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.algebra.linear.vectors.Vector4
import diy.lingerie.algebra.linear.vectors.VectorN

data class MatrixNx4(
    val rows: List<Vector4>,
) : NumericObject {
    val column0: VectorN
        get() = VectorN(
            rows.map { it.a0 },
        )

    val column1: VectorN
        get() = VectorN(
            rows.map { it.a1 },
        )

    val column2: VectorN
        get() = VectorN(
            rows.map { it.a2 },
        )

    val column3: VectorN
        get() = VectorN(
            rows.map { it.a3 },
        )

    val height: Int
        get() = rows.size

    val transposed: Matrix4xN
        get() = Matrix4xN(
            columns = rows,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when (other) {
        !is MatrixNx4 -> false

        else -> rows.equalsWithTolerance(
            other.rows,
            tolerance = tolerance,
        )
    }
}
