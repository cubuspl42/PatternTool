from sympy import symbols, binomial, Matrix, expand, solve

# N = 3 for cubic Bézier curves
n = 3

# Define symbolic variables
x_sym, y_sym, t_sym = symbols('x y t', real=True)

a_p0_x, a_p0_y, a_p1_x, a_p1_y, a_p2_x, a_p2_y, a_p3_x, a_p3_y = symbols(
    'a_p0_x a_p0_y a_p1_x a_p1_y a_p2_x a_p2_y a_p3_x a_p3_y', real=True)

a_p0 = (a_p0_x, a_p0_y)
a_p1 = (a_p1_x, a_p1_y)
a_p2 = (a_p2_x, a_p2_y)
a_p3 = (a_p3_x, a_p3_y)
a_coords = [a_p0, a_p1, a_p2, a_p3]

a_a3_x, a_a3_y, a_a2_x, a_a2_y, a_a1_x, a_a1_y, a_a0_x, a_a0_y = symbols(
    'a_a3_x a_a3_y a_a2_x a_a2_y a_a1_x a_a1_y a_a0_x a_a0_y', real=True)
b_a3_x, b_a3_y, b_a2_x, b_a2_y, b_a1_x, b_a1_y, b_a0_x, b_a0_y = symbols(
    'b_a3_x b_a3_y b_a2_x b_a2_y b_a1_x b_a1_y b_a0_x b_a0_y', real=True)

class BezierCurve:
    def __init__(self, p0, p1, p2, p3):
        self.p0 = p0
        self.p1 = p1
        self.p2 = p2
        self.p3 = p3

        self.p = [
            p0,
            p1,
            p2,
            p3,
        ]

    def __call__(self, t):
        return (
                (1 - t) ** 3 * self.p0 +
                3 * (1 - t) ** 2 * t * self.p1 +
                3 * (1 - t) * t ** 2 * self.p2 +
                t ** 3 * self.p3
        )

    def to_polynomial(self):
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

    def l_ij(self, i, j):
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
    def l32(self):
        return self.l_ij(3, 2)

    @property
    def l31(self):
        return self.l_ij(3, 1)

    @property
    def l30(self):
        return self.l_ij(3, 0)

    @property
    def l21(self):
        return self.l_ij(2, 1)

    @property
    def l20(self):
        return self.l_ij(2, 0)

    @property
    def l10(self):
        return self.l_ij(1, 0)

    def implicitize(self):
        a_impl_mat = Matrix([
            [self.l32, self.l31, self.l30],
            [self.l31, self.l30 + self.l21, self.l20],
            [self.l30, self.l20, self.l10]
        ])

        # Get the determinant, which is your implicit polynomial f(x,y)
        return a_impl_mat.det()

    def invert(self):
        c1_n_mat = Matrix([
            [a_p0_x, a_p0_y, 1.0],
            [a_p1_x, a_p1_y, 1.0],
            [a_p3_x, a_p3_y, 1.0],
        ])

        c2_n_mat = Matrix([
            [a_p0_x, a_p0_y, 1.0],
            [a_p2_x, a_p2_y, 1.0],
            [a_p3_x, a_p3_y, 1.0],
        ])

        c_d_mat = Matrix([
            [a_p1_x, a_p1_y, 1.0],
            [a_p2_x, a_p2_y, 1.0],
            [a_p3_x, a_p3_y, 1.0],
        ])

        c1 = c1_n_mat.det() / c_d_mat.det()
        c2 = c2_n_mat.det() / c_d_mat.det()

        la = c1 * self.l31 + c2 * (self.l30 + self.l21) + self.l20
        lb = c1 * self.l30 + c2 * self.l20 + self.l10

        return lb / (lb - la)

    def intersect(self, other):
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

bezier_nine_a = BezierCurve(
    p0=(273.80049324035645, 489.08709716796875),
    p1=(1068.5394763946533, 253.16610717773438),
    p2=(-125.00849723815918, 252.71710205078125),
    p3=(671.4185047149658, 490.2051086425781),
)

bezier_nine_b = BezierCurve(
    p0=(372.6355152130127, 191.58710479736328),
    p1=(496.35252571105957, 852.5531311035156),
    p2=(442.4235095977783, -54.72489929199219),
    p3=(569.3854846954346, 487.569091796875),
)

bezier_split_a = BezierCurve(
    p0=(273.80049324035645, 489.08709716796875),
    p1=(684.4749774932861, 329.1851005554199),
    p2=(591.8677291870117, 214.5483512878418),
    p3=(492.59773540496826, 197.3452272415161),
)

bezier_split_b = BezierCurve(
    p0=(492.59773540496826, 197.3452272415161),
    p1=(393.3277416229248, 180.14210319519043),
    p2=(287.3950023651123, 260.3726043701172),
    p3=(671.4185047149658, 490.2051086425781),
)

# bezier0 = bezier_nine_a
# bezier1 = bezier_nine_b

bezier0 = bezier_split_a
bezier1 = bezier_split_b

bezier0_poly_x, bezier0_poly_y = bezier0.to_polynomial()
bezier1_poly_x, bezier1_poly_y = bezier1.to_polynomial()

print("bezier0_poly_x:")
print(expand(bezier0_poly_x))

print("bezier0_poly_y:")
print(expand(bezier0_poly_y))

print("bezier1_poly_x:")
print(expand(bezier1_poly_x))

print("bezier1_poly_y:")
print(expand(bezier1_poly_y))

intersection0 = bezier0.intersect(bezier1)

print("intersection0:")
print(intersection0)

intersection1 = bezier1.intersect(bezier0)

print("intersection1:")
print(intersection1)

roots = solve(intersection1)

print(roots)
