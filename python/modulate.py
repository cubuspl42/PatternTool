import sympy as sp

from geometry.CommonSymbols import x_sym, y_sym, t_sym
from geometry.CubicBezierCurve import CubicBezierCurve
from geometry.QuadraticBezierCurve import QuadraticBezierCurve

x0_sym, x1_sym, x2_sym, x3_sym = sp.symbols('x0 x1 x2 x3', real=True)
y0_sym, y1_sym, y2_sym, y3_sym = sp.symbols('y0 y1 y2 y3', real=True)
shift_sym, dilation_sym = sp.symbols('s d', real=True)


def main():
    symbolic_cubic_curve = CubicBezierCurve(
        p0=(x0_sym, y0_sym),
        p1=(x1_sym, y1_sym),
        p2=(x2_sym, y2_sym),
        p3=(x3_sym, y3_sym),
    )

    cubic_curve_x = symbolic_cubic_curve.to_polynomial()[0]

    modulated_cubic_curve = cubic_curve_x.subs(t_sym, t_sym / shift_sym).subs(t_sym, t_sym - shift_sym)

    print(modulated_cubic_curve.expand().collect(t_sym))

if __name__ == '__main__':
    main()
