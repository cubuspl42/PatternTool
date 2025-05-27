package diy.lingerie.pattern_tool

import diy.lingerie.simple_dom.svg.PureSvgRoot

abstract class PatternPiecePreparator {
    abstract fun preparePatternPieceOutlines(
        svgRootByName: Map<String, PureSvgRoot>,
    ): PatternPieceOutlineSet
}
