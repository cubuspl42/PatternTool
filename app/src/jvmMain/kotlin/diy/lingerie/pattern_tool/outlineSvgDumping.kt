package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toSvgPathElement
import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgRoot

fun Outline.dumpSvg(): SvgRoot = SvgRoot(
    width = 256,
    height = 256,
    unit = SimpleUnit.pt,
    children = listOf(
        innerSpline.toSvgPathElement(),
    ),
)
