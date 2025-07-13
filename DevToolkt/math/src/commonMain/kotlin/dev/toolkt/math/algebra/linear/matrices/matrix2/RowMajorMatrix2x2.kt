package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector2

internal data class RowMajorMatrix2x2(
    override val row0: Vector2,
    override val row1: Vector2,
) : Matrix2x2() {
    override fun get(i: Int, j: Int): Double = getRow(i)[j]

    override val transposed: Matrix2x2
        get() = ColumnMajorMatrix2x2(
            column0 = row0,
            column1 = row1,
        )

    override val column0: Vector2
        get() = Vector2(
            a0 = row0.a0,
            a1 = row1.a0,
        )

    override val column1: Vector2
        get() = Vector2(
            a0 = row0.a1,
            a1 = row1.a1,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Matrix2x2 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            tolerance = tolerance,
        )
    }

    override fun apply(argumentVector: Vector2): Vector2 = dotV(argumentVector)

    override fun times(other: Matrix2x2): Matrix2x2 = rowMajor(
        row0 = Vector2(
            row0.dot(other.column0),
            row0.dot(other.column1),
        ),
        row1 = Vector2(
            row1.dot(other.column0),
            row1.dot(other.column1),
        ),
    )
}
