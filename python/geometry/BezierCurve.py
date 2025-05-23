from typing import Tuple, List

import numpy as np
import plotly.graph_objs as go
from sympy import symbols, binomial, Matrix, expand, solve, Expr, lambdify
from sympy.core.numbers import I

# N = 3 for cubic Bézier curves
n = 3

# Define symbolic variables
x_sym, y_sym, t_sym = symbols('x y t', real=True)

lightgray = 'lightgray'
blue = 'blue'


class BezierCurve:
    def __init__(
            self,
            p0: Tuple[float, float],
            p1: Tuple[float, float],
            p2: Tuple[float, float],
            p3: Tuple[float, float],
    ) -> None:
        self.p0: Tuple[float, float] = p0
        self.p1: Tuple[float, float] = p1
        self.p2: Tuple[float, float] = p2
        self.p3: Tuple[float, float] = p3

        self.p: List[Tuple[float, float]] = [
            p0,
            p1,
            p2,
            p3,
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
        p3 = Matrix(self.p3)

        return (
                (1 - t_sym) ** 3 * p0 +
                3 * (1 - t_sym) ** 2 * t_sym * p1 +
                3 * (1 - t_sym) * t_sym ** 2 * p2 +
                t_sym ** 3 * p3
        )

    def build_distance_squared_polynomial(self, point):
        p_x, p_y = self.to_polynomial()
        x, y = point
        return (p_x - x) ** 2 + (p_y - y) ** 2

    def build_distance_squared_polynomial_lambda(self, point):
        return lambdify(t_sym, self.build_distance_squared_polynomial(point), 'numpy')

    def plot_traces_2d(
            self,
            curve_name,
            primary_color=blue,
    ):
        def build_traces(t0, t1, t2, t3):
            def build_trace(ta, tb, color, trace_name):
                t_values = np.linspace(ta, tb, 50)

                x_values, y_values = self.evaluate_unwrapped(t_values)

                formatted_text = [f"t = {t:.2f}" for t in t_values]

                return go.Scatter(
                    x=x_values,
                    y=y_values,
                    text = formatted_text,
                    mode='lines',
                    line=dict(color=color),
                    name=trace_name,
                )

            return [
                build_trace(
                    t0,
                    t1,
                    color=lightgray,
                    trace_name=f'{curve_name}: extended t-range (2D, < 0)',
                ),
                build_trace(
                    t1,
                    t2,
                    color=primary_color,
                    trace_name=f'{curve_name}: primary t-range (2D)',
                ),
                build_trace(
                    t2,
                    t3,
                    color=lightgray,
                    trace_name=f'{curve_name}: extended t-range (2D, > 0)',
                ),
            ]

        data = build_traces(
            t0=-0.05,
            t1=0.0,
            t2=1.0,
            t3=1.05,
        )

        return data

    # Plot the Bézier curve in 3D, where the third dimension is the t parameter
    def plot_traces_3d(
            self,
            curve_name,
            primary_color=blue,
    ):
        def build_traces(t0, t1, t2, t3):
            def build_trace(ta, tb, color, trace_name):
                t_values = np.linspace(ta, tb, 50)

                x_values, y_values = self.evaluate_unwrapped(t_values)

                return go.Scatter3d(
                    x=x_values,
                    y=y_values,
                    z=t_values,
                    mode='lines',
                    line=dict(color=color),
                    name=trace_name,
                )

            return [
                build_trace(
                    t0,
                    t1,
                    color=lightgray,
                    trace_name=f'{curve_name}: extended t-range (3D, < 0)',
                ),
                build_trace(
                    t1,
                    t2,
                    color=primary_color,
                    trace_name=f'{curve_name}: primary t-range (3D)',
                ),
                build_trace(
                    t2,
                    t3,
                    color=lightgray,
                    trace_name=f'{curve_name}: extended t-range (3D, > 0)',
                ),
            ]

        data = build_traces(
            t0=-0.2,
            t1=0.0,
            t2=1.0,
            t3=1.2,
        )

        return data

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
    def l32(self) -> Expr:
        return self.l_ij(3, 2)

    @property
    def l31(self) -> Expr:
        return self.l_ij(3, 1)

    @property
    def l30(self) -> Expr:
        return self.l_ij(3, 0)

    @property
    def l21(self) -> Expr:
        return self.l_ij(2, 1)

    @property
    def l20(self) -> Expr:
        return self.l_ij(2, 0)

    @property
    def l10(self) -> Expr:
        return self.l_ij(1, 0)

    def implicitize(self) -> Expr:
        a_impl_mat = Matrix([
            [self.l32, self.l31, self.l30],
            [self.l31, self.l30 + self.l21, self.l20],
            [self.l30, self.l20, self.l10]
        ])

        # Get the determinant, which is the implicit polynomial f(x,y)
        return a_impl_mat.det()

    def implicitize_lambda(self):
        return lambdify((x_sym, y_sym), self.implicitize(), 'numpy')

    def implicitize_bruteforce(self) -> Expr:
        p = self.to_polynomial()
        x_t, y_t = p

        tx_1, tx_2, tx_3 = solve(x_t - x_sym, t_sym)

        assert tx_1.has(I)
        assert tx_2.has(I)
        assert not tx_3.has(I)

        impl = y_t.subs(t_sym, tx_3)

        return impl

    def implicitize_bruteforce_lambda(self):
        return lambdify((x_sym, y_sym), self.implicitize_bruteforce(), 'numpy')

    def invert(self) -> Expr | None:
        c1_n_mat = Matrix([
            [self.p0[0], self.p0[1], 1.0],
            [self.p1[0], self.p1[1], 1.0],
            [self.p3[0], self.p3[1], 1.0],
        ])

        c2_n_mat = Matrix([
            [self.p0[0], self.p0[1], 1.0],
            [self.p2[0], self.p2[1], 1.0],
            [self.p3[0], self.p3[1], 1.0],
        ])

        c_d_mat = Matrix([
            [self.p1[0], self.p1[1], 1.0],
            [self.p2[0], self.p2[1], 1.0],
            [self.p3[0], self.p3[1], 1.0],
        ])

        denominator = 3 * c_d_mat.det()

        if denominator == 0:
            return None

        c1 = c1_n_mat.det() / denominator
        c2 = -(c2_n_mat.det() / denominator)

        la = c1 * self.l31 + c2 * (self.l30 + self.l21) + self.l20
        lb = c1 * self.l30 + c2 * self.l20 + self.l10

        return lb / (lb - la)

    def intersect(self, other: "BezierCurve") -> Expr:
        implicit = self.implicitize()

        print("implicit:")
        print(expand(implicit))

        other_polynomial = other.to_polynomial()
        other_polynomial_x, other_polynomial_y = other_polynomial

        print("other_polynomial_x:")
        print(expand(other_polynomial_x))

        intersection_polynomial = implicit.subs({
            x_sym: other_polynomial_x,
            y_sym: other_polynomial_y,
        })

        return expand(intersection_polynomial)
