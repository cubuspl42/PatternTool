# simple_dom

The basic idea of the `simple_dom` package is to provide a _simple_, statically typed and (ideally) purely functional Kotlin interface for SVG and FO (Formatting Objects).

For geometric primitives (like points and transformations), the custom classes from the `geometry` package are used.

Every simple element should implement "lowering" to a _raw_ element (`org.w3c.dom.Element`) using just standard DOM APIs (`toRawElement()`.

For SVG elements, a best-effort conversion _from_ SVG DOM should also be provided (`SvgSomethingElement.toSimpleSomething()`).
