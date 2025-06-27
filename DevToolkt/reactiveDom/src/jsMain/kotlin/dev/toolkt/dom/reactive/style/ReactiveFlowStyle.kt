package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PureDisplayInsideType
import dev.toolkt.dom.pure.style.PureDisplayOutsideType
import dev.toolkt.reactive.Subscription
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveFlowStyle(
    override val outsideType: PureDisplayOutsideType? = null,
    val someFlexStuff: String,
) : ReactiveDisplayStyle() {
    override val insideType: PureDisplayInsideType = PureDisplayInsideType.Flow

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Subscription.Noop = Subscription.Noop
}
