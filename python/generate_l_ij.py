import sympy as sp

from geometry.CommonSymbols import x_sym, y_sym
from geometry.CubicBezierCurve import CubicBezierCurve
from geometry.QuadraticBezierCurve import QuadraticBezierCurve

x0_sym, x1_sym, x2_sym, x3_sym = sp.symbols('x0 x1 x2 x3', real=True)
y0_sym, y1_sym, y2_sym, y3_sym = sp.symbols('y0 y1 y2 y3', real=True)


def main():
    print(QuadraticBezierCurve)

    symbolic_quadratic_curve = QuadraticBezierCurve(
        p0=(x0_sym, y0_sym),
        p1=(x1_sym, y1_sym),
        p2=(x2_sym, y2_sym),
    )

    print("l10: ", extract_implicit_line_function_coeffs(symbolic_quadratic_curve.l10))
    print("l20: ", extract_implicit_line_function_coeffs(symbolic_quadratic_curve.l20))
    print("l21: ", extract_implicit_line_function_coeffs(symbolic_quadratic_curve.l21))

    print(CubicBezierCurve)

    symbolic_cubic_curve = CubicBezierCurve(
        p0=(x0_sym, y0_sym),
        p1=(x1_sym, y1_sym),
        p2=(x2_sym, y2_sym),
        p3=(x3_sym, y3_sym),
    )

    print("l10: ", extract_implicit_line_function_coeffs(symbolic_cubic_curve.l10))
    print("l20: ", extract_implicit_line_function_coeffs(symbolic_cubic_curve.l20))
    print("l21: ", extract_implicit_line_function_coeffs(symbolic_cubic_curve.l21))
    print("l30: ", extract_implicit_line_function_coeffs(symbolic_cubic_curve.l30))
    print("l31: ", extract_implicit_line_function_coeffs(symbolic_cubic_curve.l31))


def extract_implicit_line_function_coeffs(l):
    a = l.coeff(x_sym)
    b = l.coeff(y_sym)
    c = extract_independent_terms(l, {x_sym, y_sym})

    return {
        'a': a,
        'b': b,
        'c': c,
    }


def extract_independent_terms(expr, syms):
    """
    Extract terms from a sympy expression that do not depend on the specified symbols.

    Parameters:
    expr (sp.Expr): The sympy expression from which to extract terms.
    symbols (set or list of sp.Symbol): The symbols on which dependency is to be checked.

    Returns:
    sp.Expr: A sympy expression consisting of only the terms that do not depend on the specified symbols.
    """
    # Ensure symbols is a set of sympy Symbols
    if not isinstance(syms, set):
        syms = set(syms)

    # Separate the expression into terms if it's additive
    terms = expr.as_ordered_terms()

    # Filter terms that do not depend on the specified symbols
    independent_terms = [term for term in terms if not term.free_symbols.intersection(syms)]

    # Combine independent terms back into a single expression
    if independent_terms:
        independent_expr = sum(independent_terms)
    else:
        independent_expr = sp.Integer(0)  # Return 0 if no independent terms are found

    return independent_expr


if __name__ == '__main__':
    main()
