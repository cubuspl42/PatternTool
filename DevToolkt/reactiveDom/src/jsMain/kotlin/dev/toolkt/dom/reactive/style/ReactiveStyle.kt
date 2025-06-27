package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PureDisplayStyle
import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayStyle: Cell<PureDisplayStyle>? = null,
    val width: Cell<PureDimension<*>>? = null,
    val height: Cell<PureDimension<*>>? = null,
    val backgroundColor: Cell<PureColor>? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        displayStyle?.bind(
            styleDeclaration = styleDeclaration,
        )

        width?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Width,
        )

        height?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Height,
        )
        backgroundColor?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.BackgroundColor,
        )
    }
}
