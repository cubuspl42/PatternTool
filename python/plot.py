import plotly.io as pio
from plotly.subplots import make_subplots

from geometry.BezierCurve import BezierCurve

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

# curve1 = BezierCurve(
#     p0=(492.59773540496826, 197.3452272415161),
#     p1=(393.3277416229248, 180.14210319519043),
#     p2=(287.3950023651123, 260.3726043701172),
#     p3=(671.4185047149658, 490.2051086425781),
# )

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


def add_curve_traces(
        curve,
        name,
        primary_color,
):
    traces_2d = curve.plot_traces_2d(
        curve_name=name,
        primary_color=primary_color,
    )

    fig.add_traces(
        traces_2d,
        rows=[1, 1],
        cols=[1, 1],
    )

    traces_3d = curve.plot_traces_3d(
        curve_name=name,
        primary_color=primary_color,
    )

    fig.add_traces(
        traces_3d,
        rows=[1, 1, 1],
        cols=[2, 2, 2],
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
