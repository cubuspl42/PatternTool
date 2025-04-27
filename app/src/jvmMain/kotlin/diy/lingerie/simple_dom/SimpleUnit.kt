package diy.lingerie.simple_dom

enum class SimpleUnit(
    val string: String
) {
    Mm("mm"), Pt("pt"), Percent("%");

    companion object {
        fun parse(string: String): SimpleUnit = when (string) {
            Mm.string -> Mm
            Pt.string -> Pt
            Percent.string -> Percent
            else -> error("Unsupported unit: $string")
        }
    }
}
