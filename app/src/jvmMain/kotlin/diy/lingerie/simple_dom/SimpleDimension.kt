package diy.lingerie.simple_dom

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance

data class SimpleDimension<out U: SimpleUnit>(
    val value: Double,
    val unit: U,
) : NumericObject {
    companion object {
        private val regex = Regex("([0-9.]+)([a-zA-Z%]+)")

        fun parse(
            string: String,
        ): SimpleDimension<*> {
            val matchResult =
                regex.matchEntire(string) ?: throw IllegalArgumentException("Invalid dimension format: $string")

            val (valueString, unitString) = matchResult.destructured

            val value = valueString.toDouble()
            val unit = SimpleUnit.parse(unitString)

            return SimpleDimension(
                value = value,
                unit = unit,
            )
        }
    }

    fun toDimensionString(): String = "$value${unit.string}"

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SimpleDimension<*> -> false
        !value.equalsWithTolerance(other.value, tolerance = tolerance) -> false
        unit != other.unit -> false
        else -> true
    }
}

val Double.mm: SimpleDimension<SimpleUnit.Mm>
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Mm,
    )

val Int.mm: SimpleDimension<SimpleUnit.Mm>
    get() = this.toDouble().mm

val Double.pt: SimpleDimension<SimpleUnit.Pt>
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Pt,
    )

val Int.pt: SimpleDimension<SimpleUnit.Pt>
    get() = this.toDouble().pt

val Double.percent: SimpleDimension<SimpleUnit.Percent>
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Percent,
    )

val Int.percent: SimpleDimension<SimpleUnit.Percent>
    get() = this.toDouble().percent
