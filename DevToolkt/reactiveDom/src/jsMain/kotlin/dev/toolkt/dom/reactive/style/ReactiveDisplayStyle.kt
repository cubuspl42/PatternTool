package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PureDisplayInsideType
import dev.toolkt.dom.pure.style.PureDisplayOutsideType
import dev.toolkt.reactive.Subscription
import org.w3c.dom.css.CSSStyleDeclaration

sealed class ReactiveDisplayStyle {
    abstract val outsideType: PureDisplayOutsideType?

    abstract val insideType: PureDisplayInsideType

    val displayString: String
        get() = listOfNotNull(
            outsideType?.cssString,
            insideType.type,
        ).joinToString(separator = " ")

    abstract fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Subscription
}
