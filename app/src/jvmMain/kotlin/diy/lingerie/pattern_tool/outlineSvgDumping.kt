package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toSvgPath
import diy.lingerie.simple_dom.pt
import diy.lingerie.simple_dom.svg.SvgRoot

fun Outline.dumpSvg(): SvgRoot = SvgRoot(
    width = 256.pt,
    height = 256.pt,
    children = listOf(
        innerSpline.toSvgPath(),
    ),
)
