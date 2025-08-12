package diy.lingerie.pattern_tool

import dev.toolkt.dom.pure.svg.PureSvgRoot

abstract class PatternPiecePreparator {
    abstract fun preparePatternPieceOutlines(
        svgRootByName: Map<String, PureSvgRoot>,
    ): PatternPieceOutlineSet
}
