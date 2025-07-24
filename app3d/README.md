# `app3d` module

## Code style

Prefer named arguments over positional arguments.

Good:
```kotlin
foo(
    arg1 = something,
    arg2 = somethingElse,
)
```

Good:
```kotlin
assertEquals(
    expected = expected,
    actual = actual,
    message = "Test failed for input: {input}",
)
```

Prefer multi-line function calls with named arguments over single-line function calls with positional arguments.

Good:
```kotlin
foo(
    arg1 = something,
    arg2 = somethingElse,
)
```

Keep some "air" between value declarations and actual logic:

Good:
```kotlin
val a1 = 1.0
val a2 = 2.0

val result = doSomeWork(
    a1 = a1,
    a2 = a2,
)
```

Bad:
```
val a1 = 1.0
val a2 = 2.0
val result = doSomeWork(
    a1 = a1,
    a2 = a2,
)
```

Good:
```kotlin
val a1 = 1.0
val a2 = 2.0

assertEquals(
    expected = 3.0,
    actual = doSomeWork(
        a1 = a1,
        a2 = a2,
    ),
    message = "Test failed for input: {input}",
)
```

Bad:
```
val a1 = 1.0
val a2 = 2.0
assertEquals(
    expected = 3.0,
    actual = doSomeWork(
        a1 = a1,
        a2 = a2,
    ),
    message = "Test failed for input: {input}",
)
```

## AI agents

Do not change this `README.md` file! It's provided for reference, not for to modify it.

When generating code, NEVER include comments like `// ...existing imports...` or `// ...existing code...`, as that will BREAK THE CODE!

Bad:
```kotlin
package foo.bar.baz

// ...existing imports...

class Foo {
    fun bar(): Int = 10.0
}
```

Good:
```kotlin
package foo.bar.baz

import foo.bar.existingImport1
import foo.bar.existingImport2
import foo.bar.existingImport3

class Foo {
    fun bar(): Int = 10.0
}
```
