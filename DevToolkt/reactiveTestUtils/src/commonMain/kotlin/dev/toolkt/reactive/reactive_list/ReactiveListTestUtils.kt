package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.managed_io.Moments

fun <ElementT> ReactiveList<ElementT>.sampleContentExternally(
): List<ElementT> = Moments.external {
    sampleContent()
}

fun <ElementT> MutableReactiveList.Companion.createExternally(
    initialContent: List<ElementT>,
): MutableReactiveList<ElementT> = Moments.external {
    MutableReactiveList.create(initialContent = initialContent)
}

fun <ElementT> MutableReactiveList.Companion.createExternally(
    vararg initialContent: ElementT,
): MutableReactiveList<ElementT> = Moments.external {
    MutableReactiveList.create(initialContent = initialContent.toList())
}

fun <ElementT> MutableReactiveList<ElementT>.setExternally(
    index: Int,
    newValue: ElementT,
) {
    Actions.external {
        set(
            index = index,
            newValue = newValue,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.addExternally(
    index: Int,
    element: ElementT,
) {
    Actions.external {
        add(
            index = index,
            element = element,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.addAllExternally(
    index: Int,
    elements: List<ElementT>,
) {
    Actions.external {
        addAll(
            index = index,
            elements = elements,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.replaceAllExternally(
    indexRange: IntRange,
    changedElements: List<ElementT>,
) {
    Actions.external {
        replaceAll(
            indexRange = indexRange,
            changedElements = changedElements,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.appendExternally(
    element: ElementT,
) {
    Actions.external {
        append(
            element = element,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.removeRangeExternally(
    indexRange: IntRange,
) {
    Actions.external {
        removeRange(
            indexRange = indexRange,
        )
    }
}

fun <ElementT> MutableReactiveList<ElementT>.removeAtExternally(
    index: Int,
) {
    Actions.external {
        removeAt(
            index = index,
        )
    }
}
