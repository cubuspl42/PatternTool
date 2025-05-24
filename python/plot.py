import numpy as np
import plotly.graph_objs as go
import plotly.io as pio
from plotly.subplots import make_subplots

from geometry.BezierCurve import BezierCurve

curve1 = BezierCurve(
    p0=(200.0, 200.0),
    p1=(100.0, 100.0),
    p2=(400.0, 400.0),
    p3=(300.0, 300.0),
)

curve2 = BezierCurve(
    p0=(273.80049324035645, 489.08709716796875),
    p1=(1068.5394763946533, 253.16610717773438),
    p2=(-125.00849723815918, 252.71710205078125),
    p3=(671.4185047149658, 490.2051086425781),
)

point = (630.5394763946533, 200.16610717773438)

def add_all_traces(
        fig,
        traces,
        row: int,
        col: int,
):
    for i, trace in enumerate(traces):
        fig.add_trace(
            trace,
            row=row,
            col=col,
        )

    return fig


def plot_curves():
    fig = make_subplots(
        rows=1,
        cols=2,
        specs=[[{'type': 'xy'}, {'type': 'surface'}]],
    )

    fig.update_scenes(
        zaxis_title_text='t',
        row=1,
        col=2,
    )

    def add_curve_traces(
            curve,
            name,
            primary_color,
    ):
        traces_2d = curve.plot_traces_2d(
            curve_name=name,
            primary_color=primary_color,
        )

        add_all_traces(
            fig=fig,
            traces=traces_2d,
            row=1,
            col=1,
        )

        traces_3d = curve.plot_traces_3d(
            curve_name=name,
            primary_color=primary_color,
        )

        add_all_traces(
            fig=fig,
            traces=traces_3d,
            row=1,
            col=2,
        )

    add_curve_traces(
        curve=curve1,
        name='curve1',
        primary_color='red',
    )

    add_curve_traces(
        curve=curve2,
        name='curve2',
        primary_color='blue',
    )

    pio.show(fig, renderer='browser')


def plot_distance_squared():
    fig = make_subplots(
        rows=1,
        cols=2,
        specs=[[{'type': 'xy'}, {'type': 'xy'}]],
    )

    traces_2d = curve2.plot_traces_2d(
        curve_name='curve2',
        primary_color='blue',
    )

    add_all_traces(
        fig=fig,
        traces=traces_2d,
        row=1,
        col=1,
    )


    # add point to plot

    fig.add_trace(
        go.Scatter(
            x=[point[0]],
            y=[point[1]],
            mode='markers',
            marker=dict(size=10, color='red'),
            name='Point',
        ),
        row=1,
        col=1,
    )

    fig.update_xaxes(scaleanchor="y", scaleratio=1, row=1, col=1)
    fig.update_yaxes(scaleanchor="x", scaleratio=1, row=1, col=1)

    distance_squared_polynomial = curve2.build_distance_squared_polynomial_lambda(point)

    # plot distance_squared_polynomial

    t_values = np.linspace(-0.2, 1.2, 100)
    distance_squared_values = distance_squared_polynomial(t_values)

    fig.add_trace(
        go.Scatter(
            x=t_values,
            y=distance_squared_values,
            mode='lines',
            name='Distance Squared',
            line=dict(color='blue'),
        ),
        row=1,
        col=2,
    )

    pio.show(fig, renderer='browser')

plot_distance_squared()