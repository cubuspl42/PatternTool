package diy.lingerie.pattern_tool

import diy.lingerie.simple_dom.svg.SvgRoot

abstract class PatternPiecePreparator {
    abstract fun preparePatternPieceOutlines(
        svgRootByName: Map<String, SvgRoot>,
    ): PatternPieceOutlineSet
}
