package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2

internal data class ColumnMajorMatrix2x2(
    override val column0: Vector2,
    override val column1: Vector2,
) : Matrix2x2() {
    override fun get(i: Int, j: Int): Double = getColumn(j)[i]

    override val transposed: Matrix2x2
        get() = RowMajorMatrix2x2(
            row0 = column0,
            row1 = column1,
        )

    override val row0: Vector2
        get() = Vector2(
            a0 = column0.a0,
            a1 = column1.a0,
        )

    override val row1: Vector2
        get() = Vector2(
            a0 = column0.a1,
            a1 = column1.a1,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other is Matrix2x2 -> when {
            other is ColumnMajorMatrix2x2 -> when {
                !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
                !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
                else -> true
            }

            else -> equalsWithToleranceRowWise(
                other = other,
                tolerance = tolerance,
            )
        }

        else -> false
    }

    override fun apply(argumentVector: Vector2): Vector2 = dotV(argumentVector)

    override fun times(other: Matrix2x2): Matrix2x2 = columnMajor(
        column0 = Vector2(
            row0.dot(other.column0),
            row1.dot(other.column0),
        ),
        column1 = Vector2(
            row0.dot(other.column1),
            row1.dot(other.column1),
        ),
    )
}
