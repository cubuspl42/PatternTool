package diy.lingerie.simple_dom

data class SimpleDimension(
    val value: Double,
    val unit: SimpleUnit,
) {
    companion object {
        private val regex = Regex("([0-9.]+)([a-zA-Z%]+)")

        fun parse(
            string: String,
        ): SimpleDimension {
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
}

val Double.mm: SimpleDimension
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Mm,
    )

val Int.mm: SimpleDimension
    get() = this.toDouble().mm

val Double.pt: SimpleDimension
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Pt,
    )

val Int.pt: SimpleDimension
    get() = this.toDouble().pt

val Double.percent: SimpleDimension
    get() = SimpleDimension(
        value = this,
        unit = SimpleUnit.Percent,
    )

val Int.percent: SimpleDimension
    get() = this.toDouble().percent
