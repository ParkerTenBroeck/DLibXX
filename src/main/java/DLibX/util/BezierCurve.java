package DLibX.util;

import java.awt.geom.Point2D;
import java.awt.geom.Path2D;

/**
 * Makes and calculates Bezier Curves and the points on them.
 * Here's a wikipedia article:
 * <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bezier Curves</a>
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class BezierCurve {
    /**
     * Constant for LINEAR Bezier curve.
     * Represents {@code ((0,0), (1,1))}
     */
    public static final BezierCurve LINEAR;
    /**
     * Constant for EASE Bezier curve.
     * Represents {@code ((0,0), (0.25,0.1), (0.25,1), (1,1))}
     */
    public static final BezierCurve EASE;
    /**
     * Constant for EASE_IN Bezier curve.
     * Represents {@code ((0,0), (0.42, 0), (1, 1), (1,1))}
     */
    public static final BezierCurve EASE_IN;
    /**
     * Constant for EASE_IN_OUT Bezier curve.
     * Represents {@code ((0,0), (0.42,0), (0.58, 1), (1,1))}
     */
    public static final BezierCurve EASE_IN_OUT;
    /**
     * Constant for EASE_IN_OUT Bezier curve.
     * Represents {@code ((0,0), (0,0), (0.58, 1), (1,1))}
     */
    public static final BezierCurve EASE_OUT;

    static {
        final Point2D zero = new Point2D.Double(0, 0);
        final Point2D one  = new Point2D.Double(1, 1);

        final Point2D a = new Point2D.Double(0.25, 0.1);
        final Point2D b = new Point2D.Double(0.25, 1);
        final Point2D c = new Point2D.Double(0.42, 0);
        final Point2D d = new Point2D.Double(0.58, 1);

        LINEAR      = new BezierCurve(zero,            one);
        EASE        = new BezierCurve(zero, a,    b,   one);
        EASE_IN     = new BezierCurve(zero, c,    one, one);
        EASE_IN_OUT = new BezierCurve(zero, c,    d,   one);
        EASE_OUT    = new BezierCurve(zero, zero, d,   one);
    }

    private final Point2D[] p;

    /**
     * Makes an immutable Bezier curve.
     * More Points make a higher order Bezier curve
     * The minimum number of points is 2.
     *
     * @param p Points specified
     *
     * @throws IllegalArgumentException if {@code p.length < 2}
     */

    public BezierCurve(Point2D... p) {
        if (p.length < 2) // if bad throw
            throw new IllegalArgumentException("Minimum number of interpolation points is 2, you have : " + p.length);

        this.p = p;
    }

    /**
     * Calculates point at given time.
     * Time must be between 0 and 1, inclusive.
     *
     * @param t Time given
     *
     * @return Point at given time
     *
     * @throws IllegalArgumentException if {@code 1 < t < 0}
     */

    public Point2D getPoint(double t) {
        return BezierCurve.getPoint(t, this.p);
    }

    public Path2D toPath(int points) {
        if (points < 2)
            throw new IllegalArgumentException("Minimum number of points is 2, you have : " + points);

        Path2D  path  = new Path2D.Double(Path2D.WIND_NON_ZERO, points);
        Point2D point = getPoint(0);
        double  speed = 1.0/(points-1);

        path.moveTo(point.getX(), point.getY());

        for (double time = speed; time < 1; time += speed) {
            point = getPoint(time);
            path.lineTo(point.getX(), point.getY());
        }

        point = getPoint(1);
        path.lineTo(point.getX(), point.getY());

        return path;
    }

    /**
     * Calculates point in given curve at given time.
     * More Points make a higher order Bezier curve
     * The minimum number of points is 2.
     * Time must be between 0 and 1, inclusive.
     *
     * @param t Time given
     * @param p Curve given
     *
     * @return Point at given time on given curve
     *
     * @throws IllegalArgumentException if {@code 1 < t < 0}
     * @throws IllegalArgumentException if {@code p.length < 2}
     */

    public static Point2D getPoint(double t, Point2D... p) {
        final double x[] = new double[p.length]; // get xs and ys
        final double y[] = new double[p.length];
        for (int i = 0; i < p.length; i++) {
            x[i] = p[i].getX();
            y[i] = p[i].getY();
        }

        x[0] = getPoint(t, x); //do the do
        y[0] = getPoint(t, y);

        return new Point2D.Double(x[0], y[0]);
    }

    /**
     * Calculates point on given line at given time.
     * Calculated through higher order linear interpolation with given points.
     * More points given make a higher order calculation
     * The minimum number of points is 2.
     * Time must be between 0 and 1, inclusive.
     *
     * @param t Time given
     * @param p Line given
     *
     * @return Point at given time on given line
     *
     * @throws IllegalArgumentException if {@code 1 < t < 0}
     * @throws IllegalArgumentException if {@code p.length < 2}
     */

    public static double getPoint(double t, double... p) {
        if (p.length == 2) // if only two points return linear interpolation of the two
            return p[0] + (p[1] - p[0])*t;

        if (0 > t || t > 1) // if bad, throw
            throw new IllegalArgumentException("Time must be greater than or equal to zero and less then or equal to one, you have: " + t);
        if (p.length < 2)
            throw new IllegalArgumentException("Minimum number of interpolation points is 2, you have : " + p.length);

        final double[] d = new double[p.length];
        for (int i = 0; i < d.length; i++)
            d[i] = p[i];

        for (int i = d.length; i > 0; i--) // interpolate and lower order until only one is left
            for (int j = 1; j < i; j++)
                d[j-1] = getPoint(t, d[j-1], d[j]);

        return d[0]; // return
    }
}
