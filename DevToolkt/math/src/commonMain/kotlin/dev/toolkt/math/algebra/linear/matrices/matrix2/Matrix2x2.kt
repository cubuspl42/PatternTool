package dev.toolkt.math.algebra.linear.matrices.matrix2

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2

sealed class Matrix2x2 : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector2(0.0, 0.0),
            row1 = Vector2(0.0, 0.0),
        )

        val identity = rowMajor(
            row0 = Vector2(1.0, 0.0),
            row1 = Vector2(0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector2,
            row1: Vector2,
        ): Matrix2x2 = RowMajorMatrix2x2(
            row0 = row0,
            row1 = row1,
        )

        fun columnMajor(
            column0: Vector2,
            column1: Vector2,
        ): Matrix2x2 = ColumnMajorMatrix2x2(
            column0 = column0,
            column1 = column1,
        )
    }

    final override fun equals(
        other: Any?,
    ): Boolean {
        return equalsWithTolerance(
            other = other as? NumericObject ?: return false,
            tolerance = NumericTolerance.Zero,
        )
    }

    fun getRow(i: Int): Vector2 = when (i) {
        0 -> row0
        1 -> row1
        else -> throw IllegalArgumentException("Invalid row index: $i")
    }

    fun getColumn(j: Int): Vector2 = when (j) {
        0 -> column0
        1 -> column1
        else -> throw IllegalArgumentException("Invalid column index: $j")
    }

    val adjugate: Matrix2x2
        get() {
            val a = row0.a0
            val b = row0.a1
            val c = row1.a0
            val d = row1.a1

            return Matrix2x2.rowMajor(
                row0 = Vector2(d, -b),
                row1 = Vector2(-c, a),
            )
        }

    val determinant: Double
        get() {
            val a = row0.a0
            val b = row0.a1
            val c = row1.a0
            val d = row1.a1

            return a * d - b * c
        }

    protected fun equalsWithToleranceRowWise(
        other: Matrix2x2,
        tolerance: NumericTolerance,
    ): Boolean = when {
        !row0.equalsWithTolerance(other.row0, tolerance = tolerance) -> false
        !row1.equalsWithTolerance(other.row1, tolerance = tolerance) -> false
        else -> true
    }

    override fun toString(): String = """[
        |  $row0,
        |  $row1,
        |]
    """.trimMargin()

    fun invert(): Matrix2x2? {
        val determinant = determinant

        if (determinant == 0.0) {
            return null
        }

        val adjugate = this.adjugate

        return adjugate / determinant
    }

    fun dotV(other: Vector2): Vector2 = Vector2(
        row0.dot(other),
        row1.dot(other),
    )

    operator fun times(
        scalar: Double,
    ): Matrix2x2 = Matrix2x2.rowMajor(
        row0 = row0 * scalar,
        row1 = row1 * scalar,
    )

    operator fun div(
        scalar: Double,
    ): Matrix2x2 = Matrix2x2.rowMajor(
        row0 = row0 / scalar,
        row1 = row1 / scalar,
    )

    abstract val row0: Vector2
    abstract val row1: Vector2

    abstract val column0: Vector2
    abstract val column1: Vector2

    abstract val transposed: Matrix2x2

    abstract operator fun get(
        i: Int,
        j: Int,
    ): Double

    abstract fun apply(argumentVector: Vector2): Vector2

    abstract operator fun times(other: Matrix2x2): Matrix2x2
}
