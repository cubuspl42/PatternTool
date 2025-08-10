package dev.toolkt.dom.reactive.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit

val Duration.inSeconds: Double
    get() = toDouble(DurationUnit.SECONDS)

val Duration.inMilliseconds: Double
    get() = toDouble(DurationUnit.MILLISECONDS)
