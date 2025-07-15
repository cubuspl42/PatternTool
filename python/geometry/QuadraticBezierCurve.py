from typing import Tuple, List

from sympy import binomial, Matrix, expand, Expr, lambdify

from geometry.CommonSymbols import x_sym, y_sym, t_sym

# N = 2 for quadratic Bézier curves
n = 2


class QuadraticBezierCurve:
    def __init__(
            self,
            p0: Tuple[float, float],
            p1: Tuple[float, float],
            p2: Tuple[float, float],
    ) -> None:
        self.p0: Tuple[float, float] = p0
        self.p1: Tuple[float, float] = p1
        self.p2: Tuple[float, float] = p2

        self.p: List[Tuple[float, float]] = [
            p0,
            p1,
            p2,
        ]

    def evaluate(self, t):
        return self.to_polynomial_lambda()(t)

    def evaluate_unwrapped(self, t):
        x_values_wrapped, y_values_wrapped = self.evaluate(t)
        return x_values_wrapped[0], y_values_wrapped[0]

    def to_polynomial(self) -> Tuple[Expr, Expr]:
        p0 = Matrix(self.p0)
        p1 = Matrix(self.p1)
        p2 = Matrix(self.p2)

        return (
                (1 - t_sym) ** 2 * p0 +
                2 * (1 - t_sym) * t_sym * p1 +
                t_sym ** 2 * p2
        )

    def build_distance_squared_polynomial(self, point):
        p_x, p_y = self.to_polynomial()
        x, y = point
        return (p_x - x) ** 2 + (p_y - y) ** 2

    def build_distance_squared_polynomial_lambda(self, point):
        return lambdify(t_sym, self.build_distance_squared_polynomial(point), 'numpy')

    def to_polynomial_lambda(self):
        return lambdify(t_sym, self.to_polynomial(), 'numpy')

    def l_ij(self, i: int, j: int) -> Expr:
        """
        l_ij(x,y) = C(n,i)C(n,j) w_i w_j * det( [ [ x, y, 1 ],
                                                   [ x_i, y_i, 1 ],
                                                   [ x_j, y_j, 1 ] ] )
        """
        pi = self.p[i]
        pj = self.p[j]

        bi = binomial(n, i)
        bj = binomial(n, j)

        return bi * bj * Matrix([
            [x_sym, y_sym, 1],
            [pi[0], pi[1], 1],
            [pj[0], pj[1], 1]
        ]).det()

    @property
    def l10(self) -> Expr:
        return self.l_ij(1, 0)

    @property
    def l20(self) -> Expr:
        return self.l_ij(2, 0)

    @property
    def l21(self) -> Expr:
        return self.l_ij(2, 1)

    def implicitize(self) -> Expr:
        a_impl_mat = Matrix([
            [self.l21, self.l20],
            [self.l20, self.l10],
        ])

        # Get the determinant, which is the implicit polynomial f(x,y)
        return a_impl_mat.det()

    def implicitize_lambda(self):
        return lambdify((x_sym, y_sym), self.implicitize(), 'numpy')

    def invert(self) -> Expr | None:
        l20 = self.l20
        l21 = self.l21

        return l20 / (l20 - l21)
