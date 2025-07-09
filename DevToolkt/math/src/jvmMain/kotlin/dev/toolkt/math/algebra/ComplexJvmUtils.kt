package dev.toolkt.math.algebra

import org.apache.commons.math3.complex.Complex as CommonsComplex

fun Complex.Companion.fromCommons(
    complex: CommonsComplex,
): Complex = Complex(
    real = complex.real,
    imaginary = complex.imaginary,
)
