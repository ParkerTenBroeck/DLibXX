package DLibX;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Handles drawing to an image.
 * It's nice.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class DCanvas {
    /**
     * TOP LEFT point of a shape, image, or line of text.
     */
    public static final int ORIGIN_TOP_LEFT = 0;
    /**
     * TOP point of a shape, image, or line of text.
     */
    public static final int ORIGIN_TOP = 1;
    /**
     * TOP RIGHT point of a shape, image, or line of text.
     */
    public static final int ORIGIN_TOP_RIGHT = 2;
    /**
     * LEFT point of a shape, image, or line of text.
     */
    public static final int ORIGIN_LEFT = 3;
    /**
     * CENTER point of a shape, image, or line of text; also a horizontal text alignment LEFT.
     */
    public static final int ORIGIN_CENTER = 4;
    /**
     * RIGHT point of a shape, image, or line of text; also a horizontal text alignment CENTER.
     */
    public static final int ORIGIN_RIGHT = 5;
    /**
     * BOTTOM LEFT point of a shape, image, or line of text; also a horizontal text alignment RIGHT.
     */
    public static final int ORIGIN_BOTTOM_LEFT = 6;
    /**
     * BOTTOM point of a shape, image, or line of text.
     */
    public static final int ORIGIN_BOTTOM = 7;
    /**
     * BOTTOM RIGHT point of a shape, image, or line of text.
     */
    public static final int ORIGIN_BOTTOM_RIGHT = 8;
    /**
     * DEFAULT coordinate plane with (0, 0) at the top left.
     */
    public static final int PLANE_DEFAULT = 9;
    /**
     * CARTESIAN coordinate plane with (0, 0) at the bottom left.
     */
    public static final int PLANE_CARTESIAN = 10;
    /**
     * Defines an ease-of-use set of HIGH QUALITY rendering hints.
     */
    public static final RenderingHints RENDER_HIGH_QUALITY;
    /**
     * Defines an ease-of-use set of LOW QUALITY rendering hints.
     */
    public static final RenderingHints RENDER_LOW_QUALITY;
    /**
     * LEFT point of a shape, image, or line of text.
     */
    public static final int ALIGN_LEFT = 11;
    /**
     * CENTER point of a shape, image, or line of text; also a horizontal text alignment LEFT.
     */
    public static final int ALIGN_CENTER = 12;
    /**
     * RIGHT point of a shape, image, or line of text; also a horizontal text alignment CENTER.
     */
    public static final int ALIGN_RIGHT = 13;
    /**
     * OPAQUE DCanvas where image data is guaranteed to be completely opaque.
     */
    public static final int ALPHA_OPAQUE;
    /**
     * BITMASK DCanvas where image data is guaranteed to be either completely opaque, or completely transparent.
     */
    public static final int ALPHA_BITMASK;
    /**
     * TRANSLUCENT DCanvas where image data contains or might contain arbitrary alpha values.
     */
    public static final int ALPHA_TRANSLUCENT;

    static {
        RENDER_HIGH_QUALITY = new RenderingHints(null, null);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_DITHERING,           RenderingHints.VALUE_DITHER_ENABLE);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_FRACTIONALMETRICS,   RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);
        RENDER_HIGH_QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        RENDER_LOW_QUALITY = new RenderingHints(null, null);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_OFF);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_DITHERING,           RenderingHints.VALUE_DITHER_DISABLE);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_FRACTIONALMETRICS,   RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_SPEED);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_NORMALIZE);
        RENDER_LOW_QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        ALPHA_OPAQUE = Transparency.OPAQUE;
        ALPHA_BITMASK = Transparency.BITMASK;
        ALPHA_TRANSLUCENT = Transparency.TRANSLUCENT;
    }

    private VolatileImage         image;         // holds buffer data until drawn to screen
    private Graphics2D            graphics;      // draws to buffer

    private GraphicsConfiguration config;        // get configuration info and make compatible stuff

    private int                   align;         // defines horizontal text alignment
    private boolean               correct;       // defines if shapes should be corrected for sharp drawing
    private Color                 background;    // defines background colour
    private Composite             composite;     // defines how colours are layered
    private Font                  font;          // defines font face and style
    private int                   origin;        // defines origin for drawing and transformations
    private Paint                 paint;         // defines foreground colour
    protected int                 plane;         // defines plane for drawing
    private RenderingHints        hints;         // defines preferred rendering algorithms
    private Stroke                stroke;        // defines line style
    private AffineTransform       transform;     // defines mathematical transforms performed
    private AffineTransform       postTransform;
    private final AffineTransform clearTrans;

    private int                   transparency;  // defines type of transparency stored image has

    private Rectangle2D           bounds;
    private FontMetrics           metrics;
    private Line2D                line;
    private Rectangle2D           rectangle;
    private Ellipse2D             ellipse;
    private Arc2D                 arc;
    private Path2D                path;

    public DCanvas(int width, int height, int transparency) {
        this.config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        this.transparency = transparency;

        this.align          = DCanvas.ALIGN_LEFT;
        this.background     = (this.transparency == DCanvas.ALPHA_OPAQUE)? Color.WHITE: new Color(0,0,0,0);
        this.composite      = AlphaComposite.SrcOver;
        this.font           = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        this.hints          = DCanvas.RENDER_LOW_QUALITY;
        this.origin         = DCanvas.ORIGIN_TOP_LEFT;
        this.paint          = Color.BLACK;
        this.stroke         = new BasicStroke(1);
        this.clearTrans     = new AffineTransform();
        this.transform      = this.clearTrans;
        this.plane          = DCanvas.PLANE_DEFAULT;
        this.correct        = false;
        this.bounds         = new Rectangle2D.Double();
        this.postTransform  = new AffineTransform();
        this.line           = new Line2D.Double();
        this.ellipse        = new Ellipse2D.Double();
        this.arc            = new Arc2D.Double();
        this.path           = new Path2D.Double();

        this.resize(width, height);

        this.metrics = this.graphics.getFontMetrics(this.font);

        this.clearRect(0, 0, width, height);
    }

    private void makePath(double[] x, double[] y) {
        if (x.length != y.length || x.length == 0)
            throw new IllegalArgumentException("Length of coordinate arrays must be equal in length and may not be empty");

        this.path.reset();
        this.path.moveTo(x[0], y[0]);
        for (int i = 1; i < x.length; i++)
            this.path.lineTo(x[i], y[i]);
    }

    private void makePath(int[] x, int[] y) {
        if (x.length != y.length || x.length == 0)
            throw new IllegalArgumentException("Length of coordinate arrays must be equal in length and may not be empty");

        this.path.reset();
        this.path.moveTo(x[0], y[0]);
        for (int i = 1; i < x.length; i++)
            this.path.lineTo(x[i], y[i]);
    }

    private void makePath(Point2D[] p) {
        if (p.length == 0)
            throw new IllegalArgumentException("Length of coordinate array may not be empty");

        this.path.reset();
        this.path.moveTo(p[0].getX(), p[0].getY());
        for (int i = 1; i < p.length; i++)
            this.path.lineTo(p[i].getX(), p[i].getY());
    }

    private void applyOrigin(Image img, double x, double y) {
        bounds.setRect(x, y, img.getWidth(null), img.getHeight(null));
        applyOrigin();
    }

    private void applyOrigin(Shape shape) {
        applyOrigin(shape.getBounds2D());
    }

    private void applyOrigin(RectangularShape shape) {
        bounds.setRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
        applyOrigin();
    }

    private void applyOrigin(double x, double y, double width, double height) {
        bounds.setRect(x, y, width, height);
        applyOrigin();
    }

    private void applyOrigin() {
        double x = -bounds.getX();
        double y = -bounds.getY();
        double p = (plane == DCanvas.PLANE_DEFAULT)? bounds.getY(): getHeight()-bounds.getY(); // getHeight = get height of drawing surface
        boolean manipulated = false;

        switch (origin) {
        case ORIGIN_LEFT:
        case ORIGIN_CENTER:
        case ORIGIN_RIGHT:
            manipulated = true;
            y -= bounds.getHeight()/2;
            break;
        case ORIGIN_BOTTOM_LEFT:
        case ORIGIN_BOTTOM:
        case ORIGIN_BOTTOM_RIGHT:
            manipulated = true;
            y -= bounds.getHeight();
            break;
        }

        switch (origin) {
        case ORIGIN_TOP:
        case ORIGIN_CENTER:
        case ORIGIN_BOTTOM:
            manipulated = true;
            x -= bounds.getWidth()/2;
            break;
        case ORIGIN_TOP_RIGHT:
        case ORIGIN_RIGHT:
        case ORIGIN_BOTTOM_RIGHT:
            manipulated = true;
            x -= bounds.getWidth();
            break;
        }

        if (plane == DCanvas.PLANE_CARTESIAN) switch (origin) {
        case ORIGIN_TOP_LEFT:
        case ORIGIN_TOP:
        case ORIGIN_TOP_RIGHT:
            manipulated = true;
            y -= bounds.getHeight();
            break;
        case ORIGIN_BOTTOM_LEFT:
        case ORIGIN_BOTTOM:
        case ORIGIN_BOTTOM_RIGHT:
            manipulated = true;
            y += bounds.getHeight();
            break;
        }

        if (correct) {
            manipulated = true;
            x += 0.5;
            y += 0.5;
            if (plane == DCanvas.PLANE_CARTESIAN) y -= 1;
        }

        if (manipulated) {
            postTransform.setToTranslation(x, y);
            postTransform.preConcatenate(transform);
            postTransform.preConcatenate(AffineTransform.getTranslateInstance(bounds.getX(), p)); // this is the line I am most concerened with
            graphics.setTransform(postTransform); // graphics object to draw on surface
        } else {
            graphics.setTransform(transform); // graphics object to draw on surface
        }
    }

    //////////////////////////////////////////////////////////////////////////// CLEAR STUFF

    /**
     * Clears the buffer by filling it with the background colour of the current DCanvas object.
     * This operation does not use the current paint mode.
     */

    public synchronized void clear() {
        this.clearRect(0,0,this.getWidth(), this.getHeight());
    }

    /**
     * Clears a rectangle in the buffer by filling it with the background colour of the current DCanvas object.
     * This operation does not use the current paint mode.
     *
     * @param x      The top left x coordinate of the rectangle
     * @param y      The top left y coordinate of the rectangle
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     */

    public synchronized void clearRect(int x, int y, int width, int height) {
        this.graphics.setTransform(this.clearTrans); //centres transform

        if (this.transparency != ALPHA_OPAQUE && this.background.getAlpha() != 255) { //if transparency is supported completely clear
            this.graphics.setComposite(AlphaComposite.Clear);
            this.graphics.fillRect(x, y, width, height);
            this.graphics.setComposite(AlphaComposite.SrcOver);
        } else {
            this.graphics.setComposite(AlphaComposite.SrcOver); //fills all with white (simple clear)
            this.graphics.setPaint(Color.WHITE);
            this.graphics.fillRect(x, y, width, height);
        }

        this.graphics.setPaint(this.background); // fill with bg colour
        this.graphics.fillRect(x, y, width, height);

        this.graphics.setPaint(this.paint); // reset colour and composite
        this.graphics.setComposite(this.composite);
    }

    //////////////////////////////////////////////////////////////////////////// DRAW SHAPES

    /**
     * Draws the outline of a Shape using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param shape the Shape to be rendered
     */

    public synchronized void draw(Shape shape) {
        this.applyOrigin(shape);
        this.graphics.draw(shape);
    }

    private void drawNoOrigin(Shape shape) {
        int temp = this.origin;
        this.origin = DCanvas.ORIGIN_TOP_LEFT;

        if (this.plane == DCanvas.PLANE_CARTESIAN) {
            bounds = shape.getBounds2D();
            double s = 2*bounds.getY() + bounds.getHeight();
            postTransform.setTransform(1, 0, 0, -1, 0, s);
            shape = postTransform.createTransformedShape(shape);
        }

        this.draw(shape);

        this.origin = temp;
    }

    private void drawNoOrigin(RectangularShape shape) {
        int temp = this.origin;
        this.origin = DCanvas.ORIGIN_TOP_LEFT;

        if (this.plane == DCanvas.PLANE_CARTESIAN) {
            double s = 2*shape.getY() + shape.getHeight();
            postTransform.setTransform(1, 0, 0, -1, 0, s);
            shape = (RectangularShape)postTransform.createTransformedShape(shape);
        }

        this.draw(shape);

        this.origin = temp;
    }

    /**
     * Draws a line using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param x1 the X position of the start-point
     * @param y1 the Y position of the start-point
     * @param x2 the X position of the endpoint
     * @param y2 the Y position of the endpoint
     */

    public void drawLine(double x1, double y1, double x2, double y2) {
        this.line.setLine(x1, y1, x2, y2);
        this.drawNoOrigin(this.line);
    }

    /**
     * Draws the outline of a rectangle using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param x      the X position of the rectangle, anchored by the specified origin
     * @param y      the Y position of the rectangle, anchored by the specified origin
     * @param width  the pixel width of the rectangle
     * @param height the pixel height of the rectangle
     */

    public void drawRect(double x, double y, double width, double height) {
        this.bounds.setRect(x, y, width, height);
        this.draw(this.bounds);
    }

    /**
     * Draws the outline of an ellipse using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param x      the X position of the ellipse, anchored by the specified origin
     * @param y      the Y position of the ellipse, anchored by the specified origin
     * @param width  the pixel width of the ellipse
     * @param height the pixel height of the ellipse
     */

    public void drawEllipse(double x, double y, double width, double height) {
        this.ellipse.setFrame(x, y, width, height);
        this.draw(this.ellipse);
    }

    /**
     * Draws the outline of an arc using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param x      the X position of the arc, anchored by the specified origin
     * @param y      the Y position of the arc, anchored by the specified origin
     * @param width  the pixel width of the arc
     * @param height the pixel height of the arc
     * @param start  the angle at which the filled part of the arc starts
     * @param extent the angle at which the filled part of the arc ends
     */

    public void drawArc(double x, double y, double width, double height, double start, double extent) {
        this.arc.setArc(x, y, width, height, start, extent, Arc2D.OPEN);
        this.draw(this.arc);
    }

    /**
     * Draws a polyline using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param points the list of coordinates
     */

    public void drawPolyline(Point2D... points) {
        this.makePath(points);
        this.drawNoOrigin(this.path);
    }

    /**
     * Draws a polyline using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void drawPolyline(double[] xPoints, double[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.drawNoOrigin(this.path);
    }

    /**
     * Draws a polyline using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void drawPolyline(int[] xPoints, int[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.drawNoOrigin(this.path);
    }

    /**
     * Draws the outline of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param points the list of coordinates
     */

    public void drawPolygon(Point2D... points) {
        this.makePath(points);
        this.path.closePath();
        this.drawNoOrigin(this.path);
    }

    /**
     * Draws the outline of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void drawPolygon(double[] xPoints, double[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.path.closePath();
        this.drawNoOrigin(this.path);
    }

    /**
     * Draws the outline of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void drawPolygon(int[] xPoints, int[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.path.closePath();
        this.drawNoOrigin(this.path);
    }

    //////////////////////////////////////////////////////////////////////////// DRAW SPECIAL STUFF

    /**
     * Draws a string using the settings of the current DCanvas object.
     * The rendering attributes applied include the Clip, Transform, Paint, Font and Composite attributes.
     *
     * @param str the string to draw
     * @param x   the X position of the string, anchored by the specified origin
     * @param y   the Y position of the string, anchored by the specified origin
     */

    public synchronized void drawString(Object str, double x, double y) {

        final String[] lines = str.toString().split("\n"); // split up into lines
        int width = 0;
        int height = this.metrics.getHeight();
        for (String s: lines) { // get widest line
            int tmp = this.metrics.stringWidth(s);
            if (tmp > width) width = tmp;
        }
        this.bounds.setRect(x, y, width, lines.length*height); // shift it all
        this.applyOrigin();
        switch (this.align) {
        case ALIGN_LEFT:
            for (String s: lines)
                this.graphics.drawString(s, (float)x, (float)(y+=height)); // draw lines, while increasing height
            break;
        case ALIGN_CENTER:
            for (String s: lines)
                this.graphics.drawString(s, (float)(x+(width-this.metrics.stringWidth(s))/2.0), (float)(y+=height)); // draw lines while increasing hight
            break;
        case ALIGN_RIGHT:
            for (String s: lines)
                this.graphics.drawString(s, (float)(x+width-this.metrics.stringWidth(s)), (float)(y+=height)); // draw lines while increasing height
            break;
        }
    }

    /**
     * Draws an image using the settings of the current DCanvas object.
     * The rendering attributes applied include the Clip, Transform and Composite attributes.
     *
     * @param img the image to draw
     * @param x   the X position of the Image, anchored by the specified origin
     * @param y   the Y position of the Image, anchored by the specified origin
     */

    public synchronized void drawImage(Image img, double x, double y) {
        this.applyOrigin(img, (int)x, (int)y);
        this.graphics.drawImage(img, (int)x, (int)y, null);
    }

    /**
     * Draws a point using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param p the point to draw
     */

    public void drawPoint(Point2D p) {
        this.drawPoint(p.getX(), p.getY());
    }

    /**
     * Draws a point using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, Composite, and Stroke attributes.
     *
     * @param x the X position of the point to draw
     * @param y the Y position of the point to draw
     */

    public void drawPoint(double x, double y) {
        this.bounds.setRect(x, y, 0, 0);
        this.drawNoOrigin(this.bounds);
    }

    //////////////////////////////////////////////////////////////////////////// FILL STUFF

    /**
     * Fills the interior of a Shape using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param shape the Shape to be rendered
     */

    public synchronized void fill(Shape shape) {
        applyOrigin(shape);
        graphics.fill(shape);
    }

    private void fillNoOrigin(Shape shape) {
        if (this.plane == DCanvas.PLANE_CARTESIAN) {
            bounds = shape.getBounds2D();
            double s = 2*bounds.getY() + bounds.getHeight();
            postTransform.setTransform(1, 0, 0, -1, 0, s);
            shape = postTransform.createTransformedShape(shape);
        }

        this.fill(shape);
    }

    private void fillNoOrigin(RectangularShape shape) {
        if (this.plane == DCanvas.PLANE_CARTESIAN) {
            double s = 2*shape.getY() + shape.getHeight();
            postTransform.setTransform(1, 0, 0, -1, 0, s);
            shape = (RectangularShape)postTransform.createTransformedShape(shape);
        }

        this.fill(shape);
    }

    /**
     * Fills the interior of a rectangle using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param x      the X position of the shape, anchored by the specified origin
     * @param y      the Y position of the shape, anchored by the specified origin
     * @param width  the pixel width of the rectangle
     * @param height the pixel height of the rectangle
     */

    public void fillRect(double x, double y, double width, double height) {
        this.bounds.setRect(x, y, width, height);
        this.fill(this.bounds);
    }

    /**
     * Fills the interior of an ellipse using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param x      the X position of the shape, anchored by the specified origin
     * @param y      the Y position of the shape, anchored by the specified origin
     * @param width  the pixel width of the ellipse
     * @param height the pixel height of the ellipse
     */

    public void fillEllipse(double x, double y, double width, double height) {
        this.ellipse.setFrame(x, y, width, height);
        this.fill(this.ellipse);
    }

    /**
     * Fills the interior of an arc using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param x      the X position of the shape, anchored by the specified origin
     * @param y      the Y position of the shape, anchored by the specified origin
     * @param width  the pixel width of the arc
     * @param height the pixel height of the arc
     * @param start  the angle at which the filled part of the arc starts
     * @param extent the angle at which the filled part of the arc ends
     */

    public void fillArc(double x, double y, double width, double height, double start, double extent) {
        this.arc.setArc(x, y, width, height, start, extent, Arc2D.PIE);
        this.fill(this.arc);
    }

    /**
     * Fills the interior of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param points the list of coordinates
     */

    public void fillPolygon(Point2D... points) {
        this.makePath(points);
        this.path.closePath();
        this.fillNoOrigin(this.path);
    }

    /**
     * Fills the interior of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void fillPolygon(double[] xPoints, double[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.path.closePath();
        this.fillNoOrigin(this.path);
    }

    /**
     * Fills the interior of a polygon using the settings of the current DCanvas object.
     * The rendering attributes applied include the Transform, Paint, and Composite attributes.
     *
     * @param xPoints the list of X coordinates
     * @param yPoints the list of Y coordinates
     */

    public void fillPolygon(int[] xPoints, int[] yPoints) {
        this.makePath(xPoints, yPoints);
        this.path.closePath();
        this.fillNoOrigin(this.path);
    }

    //////////////////////////////////////////////////////////////////////////// DRAWING ATTRIBUTE SETTERS

    /**
     * Set the DCanvas's current background colour to the specified colour.
     * All subsequent graphics operations will use this specified colour.
     * If the Colour is null, the background will be reset.
     *
     * @param background the colour the canvas will be filled with on clear
     */

    public synchronized void setBackground(Color background) {
        this.background = (background == null)? Color.WHITE: background;
        this.graphics.setBackground(this.background);
    }

    /**
     * Set the DCanvas's current paint to the specified paint.
     * All subsequent graphics operations will use this specified paint.
     * If the Paint is null, the Paint will be reset.
     *
     * @param paint the paint that will be used to draw on the canvas
     */

    public synchronized void setPaint(Paint paint) {
        this.paint = (paint == null)? Color.BLACK: paint;
        this.graphics.setPaint(this.paint);
    }

    /**
     * Set the DCanvas's current font to the specified font.
     * All subsequent graphics operations will use this specified font.
     * If the Font is null, the Font will be reset.
     *
     * @param font the font that will be used to draw text on the canvas
     */

    public synchronized void setFont(Font font) {
        this.font = (font == null)? new Font(Font.SANS_SERIF, Font.PLAIN, 12): font;
        this.graphics.setFont(this.font);
        this.metrics = this.graphics.getFontMetrics(this.font);
    }

    /**
     * Sets from which point any shapes drawn by the DCanvas will be drawn.
     * Custom shapes drawn through {@link #draw(Shape)} or {@link #fill(Shape)} may behave strangely if not the default.
     *
     * @param origin the point from which the shapes will be drawn
     */

    public void setOrigin(int origin) {
        if (0 > origin || origin > 8) throw new IllegalArgumentException ("Unknown origin type");
        this.origin = origin;
    }

    /**
     * Sets the plane mode on which shapes are drawn.
     * Custom shapes drawn through {@link #draw(Shape)} or {@link #fill(Shape)} may behave strangely if not the default.
     *
     * @param plane the plane type on which shapes will be drawn
     */

    public void setPlane(int plane) {
        if (!(plane == 9 || 10 == plane)) throw new IllegalArgumentException ("Unknown plane type.");
        this.plane = plane;
    }

    /**
     * Set the DCanvas's current composite to a composite that reflects drawing semitransparent.
     * Same as running <code>setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity))</code>
     *
     * @param opacity the opacity to draw at
     */

    public void setOpacity(double opacity) {
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity);
        this.setComposite(composite);
    }

    /**
     * Set the DCanvas's current stroke to the specified stroke.
     * All subsequent graphics operations will use this specified stroke.
     * If the Stroke is null, the Stroke will be reset.
     *
     * @param stroke the stroke that will be used to draw on the canvas
     */

    public synchronized void setStroke(Stroke stroke) {
        this.stroke = (stroke == null)? new BasicStroke(1): stroke;
        this.graphics.setStroke(this.stroke);
    }

    /**
     * Sets the text alignment mode that the DCanvas will use for multi-line text.
     *
     * @param align the text alignment mode that will be used to draw text
     */

    public void setTextAlignment(int align) {
        if (11 > align || align > 13) throw new IllegalArgumentException ("Unknown text alignment type " + align);
        this.align = align;
    }

    /**
     * Set the DCanvas's current transform to the specified transform.
     * All subsequent graphics operations will use this specified transform.
     * If the AffineTransfrom is null, the AffineTransfrom will be reset.
     *
     * @param transform the transformation that will be applied when drawing on the canvas
     */

    public void setTransform(AffineTransform transform) {
        this.transform = (transform == null)? new AffineTransform(): transform;
    }

    /**
     * Modifies a single rendering hint that the DCanvas will use for rendering images.
     *
     * @param k the rendering hints key to modify
     * @param v the value to modify to
     */

    public synchronized void setRenderingHint(RenderingHints.Key k, Object v) {
        graphics.setRenderingHint(k, v);
        this.hints = this.graphics.getRenderingHints();
    }

    /**
     * Set the DCanvas's current rendering hints to the specified rendering hints.
     * All subsequent graphics operations will use this specified rendering hints.
     *
     * @param hints the rendering hints used to draw on the canvas
     */

    public synchronized void setRenderingHints(RenderingHints hints) {
        this.hints = hints;
        graphics.setRenderingHints(this.hints);
    }

    /**
     * Set the DCanvas's current composite to the specified composite.
     * All subsequent graphics operations will use this specified composite.
     * If the Composite is null, the Composite will be reset.
     *
     * @param composite the composite used to draw on the canvas
     */

    public synchronized void setComposite(Composite composite) {
        this.composite = (composite == null)? AlphaComposite.SrcOver: composite;
        this.graphics.setComposite(this.composite);
    }

    /**
     * Set if the DCanvas should shift all shapes (+0.5, +0.5) to draw crisp shapes in HIGH_QUALITY mode.
     * The default is false.
     *
     * @param inUse if the shapes should be corrected
     */

    public void setCorrectionMode(boolean inUse) {
        this.correct = inUse;
    }

    //////////////////////////////////////////////////////////////////////////// DRAWING ATTRIBUTE GETTERS

    /**
     * Returns the DCanvas's current background colour.
     *
     * @return the current colour the canvas will be filled with on clear
     */

    public Color getBackground() {
        return this.background;
    }

    /**
     * Returns the DCanvas's current paint.
     *
     * @return the current paint that will be used to draw on the canvas
     */

    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Returns the DCanvas's current font.
     *
     * @return the current font that will be used to draw text on the canvas
     */

    public Font getFont() {
        return this.font;
    }

    /**
     * Returns the DCanvas's current origin point.
     *
     * @return the current point from which the shapes will be drawn
     */

    public int getOrigin() {
        return this.origin;
    }

    /**
     * Returns the DCanvas's current stroke.
     *
     * @return the current stroke that will be used to draw on the canvas
     */

    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Returns the DCanvas's current text alignment mode.
     *
     * @return the current text alignment mode that will be used to draw text
     */

    public int getTextAlignment() {
        return this.align;
    }

    /**
     * Returns the DCanvas's current transform.
     *
     * @return the current transformation that will be applied when drawing on the canvas
     */

    public AffineTransform getTransform() {
        return this.transform;
    }

    /**
     * Returns the DCanvas's current rendering hints.
     *
     * @return the current rendering hints used to draw on the canvas
     */

    public RenderingHints getRenderingHints() {
        return this.graphics.getRenderingHints();
    }

    /**
     * Returns the DCanvas's current composite.
     *
     * @return the current composite used to draw on the canvas
     */

    public Composite getComposite() {
        return this.composite;
    }

    /**
     * Returns the DCanvas's current width.
     *
     * @return the width of the canvas in pixels
     */

    public synchronized int getWidth() {
        return this.image.getWidth();
    }

    /**
     * Returns the DCanvas's current height.
     *
     * @return the height of the canvas in pixels
     */

    public synchronized int getHeight() {
        return this.image.getHeight();
    }

    /**
     * Returns the pixel dimensions of the specified string, as it would be drawn on the canvas
     *
     * @param str The object whose string representation is to be measured 
     *
     * @return the dimensions on the string in pixels
     */

    public Dimension getStringMetrics(Object str) {
        final String[] lines = str.toString().split("\n"); // split up into lines
        int width = 0;
        int height = this.metrics.getHeight();
        for (String s: lines) { // get widest line
            int tmp = this.metrics.stringWidth(s);
            if (tmp > width) width = tmp;
        }
        return new Dimension(width, lines.length*height);
    }

    /**
     * Sets the DCanvas's dimensions.
     * The underlying VolatileImage and Graphics context will be re-created.
     *
     * @param width  the width to set the canvas to in pixels
     * @param height the height to set the canvas to in pixels
     */

    public void setSize(int width, int height) {
        this.resize(width, height);
    }

    protected synchronized void resize(int width, int height) {
        VolatileImage tmpImage = this.config.createCompatibleVolatileImage(width, height, transparency);

        Graphics2D g = null;
        try {
            g = tmpImage.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, tmpImage.getWidth(), tmpImage.getHeight());
            g.setComposite(AlphaComposite.SrcOver);
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, null);
                this.image.flush();
            }
        } finally {
            g.dispose();
        }

        this.image = tmpImage;

        if (this.graphics != null) this.graphics.dispose();

        this.graphics = this.image.createGraphics();

        this.graphics.setTransform(this.clearTrans);
        this.graphics.setBackground(this.background);
        this.graphics.setPaint(this.paint);
        this.graphics.setStroke(this.stroke);
        this.graphics.setRenderingHints(this.hints);
        this.graphics.setFont(this.font);
        this.graphics.setComposite(this.composite);
    }

    /**
     * Returns the DCanvas's graphics context.
     *
     * @return the graphics object used to render the canvas
     */

    public synchronized Graphics2D getGraphics() {
        return this.graphics;
    }

    /**
     * Returns what the DCanvas draws on.
     *
     * @return the image being drawn to
     */

    public synchronized VolatileImage getImage() {
        return this.image;
    }

    /**
     * Returns a snapshot of what the DCanvas draws on.
     *
     * @return a snapshot of the image being drawn to
     */

    public synchronized BufferedImage getSnapshot() {
        return this.image.getSnapshot();
    }

    /**
     * Returns <code>Color</code> representation of the colour data contained in a pixel.
     * Be advised that this method is expensive, as it creates and destroys a <code>BufferedImage</code> object each time it is called.
     * <p>
     * An <code>ArrayOutOfBoundsException</code> may be thrown if the coordinates are not in bounds.
     * However, explicit bounds checking is not guaranteed.
     *
     * @param x the X coordinate of the pixel from which to get the colour data
     * @param y the Y coordinate of the pixel from which to get the colour data
     * @return a Color representation of the pixel's colour data
     */

    public Color getPixelColor(int x, int y) {
        BufferedImage b = this.getSnapshot();
        int c = b.getRGB(x, y);
        b.flush();
        return new Color(c, true);
    }

    /**
     * Releases all system memory used by the DCanvas.
     */

    public void dispose() {
        this.graphics.dispose();
        this.image.flush();

        this.graphics = null;
        this.image = null;
        this.background = null;
        this.paint = null;
        this.font = null;
        this.stroke = null;
        this.transform = null;
        this.composite = null;
    }
}
