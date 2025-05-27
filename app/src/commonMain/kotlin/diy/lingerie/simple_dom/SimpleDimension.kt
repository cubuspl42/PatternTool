package diy.lingerie.simple_dom

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.dom.pure.PureUnit

data class SimpleDimension<out U : PureUnit>(
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
            val unit = PureUnit.parse(unitString)

            return SimpleDimension(
                value = value,
                unit = unit,
            )
        }
    }

    fun toDimensionString(): String = "$value${unit.string}"

    val asAbsolute: SimpleDimension<PureUnit.Absolute>?
        get() {
            val absoluteUnit = unit as? PureUnit.Absolute ?: return null

            return SimpleDimension(
                value = value,
                unit = absoluteUnit,
            )
        }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SimpleDimension<*> -> false
        !value.equalsWithTolerance(other.value, tolerance = tolerance) -> false
        unit != other.unit -> false
        else -> true
    }
}

fun <U : PureUnit.Absolute> SimpleDimension<PureUnit.Absolute>.inUnit(
    otherUnit: U,
): SimpleDimension<U> = SimpleDimension(
    value = value * otherUnit.per(unit),
    unit = otherUnit,
)

val Double.mm: SimpleDimension<PureUnit.Mm>
    get() = SimpleDimension(
        value = this,
        unit = PureUnit.Mm,
    )

val Int.mm: SimpleDimension<PureUnit.Mm>
    get() = this.toDouble().mm

val Double.inch: SimpleDimension<PureUnit.Inch>
    get() = SimpleDimension(
        value = this,
        unit = PureUnit.Inch,
    )

val Int.inch: SimpleDimension<PureUnit.Inch>
    get() = this.toDouble().inch

val Double.pt: SimpleDimension<PureUnit.Pt>
    get() = SimpleDimension(
        value = this,
        unit = PureUnit.Pt,
    )

val Int.pt: SimpleDimension<PureUnit.Pt>
    get() = this.toDouble().pt


val Double.px: SimpleDimension<PureUnit.Px>
    get() = SimpleDimension(
        value = this,
        unit = PureUnit.Px,
    )

val Int.px: SimpleDimension<PureUnit.Px>
    get() = this.toDouble().px

val Double.percent: SimpleDimension<PureUnit.Percent>
    get() = SimpleDimension(
        value = this,
        unit = PureUnit.Percent,
    )

val Int.percent: SimpleDimension<PureUnit.Percent>
    get() = this.toDouble().percent
