package DLibX.legacy;

import DLibX.EventAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * DConsole is a console window program that allows the programmer to draw graphics and text on the screen
 * and get feedback on keystrokes, mouse clicks, and the current mouse location from the user.
 * <p>
 * There are two main modes: normal and buffered.
 * If you switch to buffered mode, you will need to call redraw to get your graphics to show up on the screen.
 * This mode allows you to queue up a bunch of graphic objects without the system drawing them one at a time.
 * Buffered mode helps eliminate screen flicker.
 * <p>
 * Created for compatibility with the original DConsole.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class DConsole {
    private final DLibX.DConsole dc;
    private final DConsoleAdapter adapter;
    private boolean buffered;

    /**
     * Default constructor.
     */

    public DConsole() {
        this(600,400);
    }

    /**
     * Construct the console with specified x and y dimensions.
     *
     * @param xSize the number of pixels horizontally in the window
     * @param ySize the number of pixels vertically in the window
     */

    public DConsole(int xSize, int ySize) {
        this.adapter = new DConsoleAdapter(this);
        this.dc = new DLibX.DConsole("", xSize, ySize, true);
        this.dc.addEventListener(this.adapter);
        this.buffered = false;
    }

    /**
     * Draws a line from (x1,y1) to (x2,y2).
     * The line is drawn in the current color of the console.
     *
     * @param x1 x coordinate of the top left corner
     * @param y1 y coordinate of the top left corner
     * @param x2 x coordinate of the bottom right corner
     * @param y2 y coordinate of the bottom right corner
     */

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.dc.drawLine(x1, y1, x2, y2);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draws a thick line from (x1,y1) to (x2,y2).
     * The line is drawn in the current color of the console.
     *
     * @param x1        x coordinate of the top left corner
     * @param y1        y coordinate of the top left corner
     * @param x2        x coordinate of the bottom right corner
     * @param y2        y coordinate of the bottom right corner
     * @param thickness the thickness of the line being drawn
     */

    public void drawThickLine(int x1, int y1, int x2, int y2, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawLine(x1, y1, x2, y2);
        this.dc.setStroke(null);
    }

    /**
     * Draw the outline of an arc centred at (x, y).
     * The arc is part of the oval specified from startAngle to finishAngle.
     * Angles are measured in degrees with 3 o'clock considered 0 degrees and then moving counter-clockwise around the oval.
     * The arc is drawn in the current color of the console.
     *
     * @param x           the x coordinate of the centre point of the arc
     * @param y           the y coordinate of the centre point of the arc
     * @param xRadius     the distance from the centre point to the left and right edges of the arc
     * @param yRadius     the distance from the centre point to the top and bottom edges of the arc
     * @param startAngle  the angle in degrees that the arc starts
     * @param finishAngle the angle in degrees that the arc stops
     */

    public void drawArc(int x, int y, int xRadius, int yRadius, int startAngle, int finishAngle) {
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_CENTER);
        this.dc.drawArc(x, y, xRadius*2, yRadius*2, startAngle, finishAngle-startAngle);
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_TOP_LEFT);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a filled arc centred at (x, y).
     * The arc is part of the oval specified from startAngle to finishAngle.
     * Angles are measured in degrees with 3 o'clock considered 0 degrees and then moving counter-clockwise around the oval.
     * The arc is drawn in the current color of the console.
     * <p>
     * @param x           the x coordinate of the centre point of the arc
     * @param y           the y coordinate of the centre point of the arc
     * @param xRadius     the distance from the centre point to the left and right edges of the arc
     * @param yRadius     the distance from the centre point to the top and bottom edges of the arc
     * @param startAngle  the angle in degrees that the arc starts
     * @param finishAngle the angle in degrees that the arc stops
     */

    public void fillArc(int x, int y, int xRadius, int yRadius, int startAngle, int finishAngle) {
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_CENTER);
        this.dc.fillArc(x, y, xRadius*2, yRadius*2, startAngle, finishAngle-startAngle);
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_TOP_LEFT);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw the thick outline of an arc centred at (x, y).
     * The arc is part of the oval specified from startAngle to finishAngle.
     * Angles are measured in degrees with 3 o'clock considered 0 degrees and then moving counter-clockwise around the oval.
     * The arc is drawn in the current color of the console.
     *
     * @param x           the x coordinate of the centre point of the arc
     * @param y           the y coordinate of the centre point of the arc
     * @param xRadius     the distance from the centre point to the left and right edges of the arc
     * @param yRadius     the distance from the centre point to the top and bottom edges of the arc
     * @param startAngle  the angle in degrees that the arc starts
     * @param finishAngle the angle in degrees that the arc stops
     * @param thickness   the thickness of the line being drawn
     */

    public void drawThickArc(int x, int y, int xRadius, int yRadius, int startAngle, int finishAngle, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawArc(x, y, xRadius*2, yRadius*2, startAngle, finishAngle-startAngle);
        this.dc.setStroke(null);
    }

    /**
     * Draw the outline of an oval centred at (x, y) with the specified x and y radii.
     * The oval is drawn in the current color of the console.
     * <p>
     * To make a circle draw an oval with the same value for xRadius and yRadius.
     *
     * @param x       the x coordinate of the centre point of the oval
     * @param y       the y coordinate of the centre point of the oval
     * @param xRadius the distance from the centre point to the left and right edges of the oval
     * @param yRadius the distance from the centre point to the top and bottom edges of the oval
     */

    public void drawOval(int x, int y, int xRadius, int yRadius) {
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_CENTER);
        this.dc.drawEllipse(x, y, xRadius*2, yRadius*2);
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_TOP_LEFT);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a filled oval centred at (x, y) with the specified x and y radii.
     * The oval is drawn in the current color of the console.
     * <p>
     * To make a circle draw an oval with the same value for xRadius and yRadius.
     *
     * @param x       the x coordinate of the centre point of the oval
     * @param y       the y coordinate of the centre point of the oval
     * @param xRadius the distance from the centre point to the left and right edges of the oval
     * @param yRadius the distance from the centre point to the top and bottom edges of the oval
     */

    public void fillOval(int x, int y, int xRadius, int yRadius) {
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_CENTER);
        this.dc.fillEllipse(x, y, xRadius*2, yRadius*2);
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_TOP_LEFT);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw an oval with a thick border centred at (x, y) with the specified x and y radii.
     * The oval is drawn in the current color of the console.
     * <p>
     * To make a circle draw an oval with the same value for xRadius and yRadius.
     *
     * @param x         the x coordinate of the centre point of the oval
     * @param y         the y coordinate of the centre point of the oval
     * @param xRadius   the distance from the centre point to the left and right edges of the oval
     * @param yRadius   the distance from the cetnre point to the top and bottom edges of the oval
     * @param thickness the thickness of the outer border
     */

    public void drawThickOval(int x, int y, int xRadius, int yRadius, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawOval(x, y, xRadius*2, yRadius*2);
        this.dc.setStroke(null);
    }

    /**
     * Draw the outline of a rectangle with the top left corner at (x,y).
     *
     * @param x      the x coordinate of the top left corner
     * @param y      the y coordinate of the top left corner
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */

    public void drawRect(int x, int y, int width, int height) {
        this.dc.drawRect(x, y, width, height);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a filled rectangle with the top left corner at (x,y).
     *
     * @param x      the x coordinate of the top left corner
     * @param y      the y coordinate of the top left corner
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */

    public void fillRect(int x, int y, int width, int height) {
        this.dc.fillRect(x, y, width, height);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a rectangle with thick border and the top left corner at (x,y).
     *
     * @param x         the x coordinate of the top left corner
     * @param y         the y coordinate of the top left corner
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param thickness the thickness of the rectangle border
     */

    public void drawThickRect(int x, int y, int width, int height, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawRect(x, y, width, height);
        this.dc.setStroke(null);
    }

    private Shape makeStar(double x, double y, double width, double height, double numPoints, double step, double rotation) {
        if (numPoints < 5) {
            throw new IllegalArgumentException("Invalid star configuration: must have at least 5 points");
        } else if (step < 2) {
            throw new IllegalArgumentException("Invalid star configuration: step must be at least 2");
        } else if (numPoints <= step*2) {
            throw new IllegalArgumentException("Invalid star configuration: step must be LESS than half numPoints");
        } else if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Invalid star configuration: both width and height must be positive numbers");
        }

        width /= 2;
        height /= 2;
        rotation = Math.toRadians(rotation) + Math.PI/2;

        final Path2D.Double path = new Path2D.Double();

        final double offset = Math.PI/numPoints;
        final double invert = Math.cos(Math.PI*step/numPoints) / Math.cos(Math.PI*(step-1) / numPoints);

        for (int i = 0; i < numPoints*2; i++) {
            final double angle = i*offset + rotation;

            final double xp = ((i%2==0)?1:invert) * width * Math.cos(angle) + x;
            final double yp = ((i%2==0)?1:invert) * height * -Math.sin(angle) + y;

            if (i == 0) {
                path.moveTo(xp, yp);
            } else {
                path.lineTo(xp, yp);
            }
        }
        path.closePath();

        return path;
    }

    /**
     * Draw a star with five points.
     *
     * @param x      the x coordinate of the centre of the star
     * @param y      the y coordinate of the centre of the star
     * @param width  the width of the star
     * @param height the height of the star
     */

    public void drawStar(int x, int y, int width, int height) {
        this.drawStar(x, y, width, height, 5, 2, 0);
    }

    /**
     * Draw a star on the screen with the centre at (x, y).
     * <p>
     * For a "stardard" 5-point star numPoints would be 5 and step would be 2.
     * <p>
     * The number of points must be at least five and the step must be at least two and must also be smaller than half the number of points.
     * Example: If you want to make a star with 10 points, the step could be anywhere from 2 to 4 (one less than half the number of points).
     *
     * @param x         the x coordinate of the centre of the star
     * @param y         the y coordinate of the centre of the star
     * @param width     the width of the star
     * @param height    the height of the star
     * @param numPoints the number of points on the star. Must be at least five
     * @param step      the number of points away that each point is connected to. Must be AT LEAST 2 and LESS THAN half of numPoints
     * @param rotation  the degrees of rotation of the star from standard form
     */

    public void drawStar(int x, int y, int width, int height, int numPoints, int step, double rotation) {
        dc.draw(this.makeStar(x, y, width, height, numPoints, step, rotation));
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a filled star with five points.
     *
     * @param x      the x coordinate of the centre of the star
     * @param y      the y coordinate of the centre of the star
     * @param width  the width of the star
     * @param height the height of the star
     */

    public void fillStar(int x, int y, int width, int height) {
        this.fillStar(x, y, width, height, 5, 2, 0);
    }

    /**
     * Draw a filled star on the screen with the centre at (x, y).
     * <p>
     * For a "stardard" 5-point star numPoints would be 5 and step would be 2.
     * <p>
     * The number of points must be at least five and the step must be at least two and must also be smaller than half the number of points.
     * Example: If you want to make a star with 10 points, the step could be anywhere from 2 to 4 (one less than half the number of points).
     *
     * @param x         the x coordinate of the centre of the star
     * @param y         the y coordinate of the centre of the star
     * @param width     the width of the star
     * @param height    the height of the star
     * @param numPoints the number of points on the star. Must be at least five
     * @param step the  number of points away that each point is connected to. Must be AT LEAST 2 and LESS THAN half of numPoints
     * @param rotation  the degrees of rotation of the star from standard form
     */

    public void fillStar(int x, int y, int width, int height, int numPoints, int step, double rotation) {
        dc.fill(this.makeStar(x, y, width, height, numPoints, step, rotation));
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a thick star with five points.
     *
     * @param x      the x coordinate of the centre of the star
     * @param y      the y coordinate of the centre of the star
     * @param width  the width of the star
     * @param height the height of the star
     * @param thickness the thickness of the outer border
     */

    public void drawThickStar(int x, int y, int width, int height, int thickness) {
        this.drawThickStar(x, y, width, height, 5, 2, 0, thickness);
    }

    /**
     * Draw a thick star on the screen with the centre at (x, y).
     * <p>
     * For a "stardard" 5-point star numPoints would be 5 and step would be 2.
     * <p>
     * The number of points must be at least five and the step must be at least two and must also be smaller than half the number of points.
     * Example: If you want to make a star with 10 points, the step could be anywhere from 2 to 4 (one less than half the number of points).
     *
     * @param x         the x coordinate of the centre of the star
     * @param y         the y coordinate of the centre of the star
     * @param width     the width of the star
     * @param height    the height of the star
     * @param numPoints the number of points on the star. Must be at least five
     * @param step      the number of points away that each point is connected to. Must be AT LEAST 2 and LESS THAN half of numPoints
     * @param rotation  the degrees of rotation of the star from standard form
     * @param thickness the thickness of the outer border
     */

    public void drawThickStar(int x, int y, int width, int height, int numPoints, int step, double rotation, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawStar(x, y, width, height, numPoints, step, rotation);
        this.dc.setStroke(null);
    }

    /**
     * Draw the outline of a closed polygon defined by arrays of x and y coordinates.
     * Each pair of (x,y) coordinates defines a point.
     * <p>
     * The last point is automatically connected to the first point.
     *
     * @param xPoints   an array of x coordinates
     * @param yPoints   an array of y coordinates
     * @param numPoints the total number of points in the polygon
     */

    public void drawPolygon(int[] xPoints, int[] yPoints, int numPoints) {
        if (numPoints != xPoints.length) {
            throw new IllegalArgumentException("drawPolygon: the number of points does not match the size of the xPoints array");
        } else if (numPoints != yPoints.length) {
            throw new IllegalArgumentException("drawPolygon: the number of points does not match the size of the yPoints array");
        }
        this.dc.drawPolygon(xPoints, yPoints);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw a filled closed polygon defined by arrays of x and y coordinates.
     * Each pair of (x,y) coordinates defines a point.
     * <p>
     * The last point is automatically connected to the first point.
     *
     * @param xPoints   an array of x coordinates
     * @param yPoints   an array of y coordinates
     * @param numPoints the total number of points in the polygon
     */

    public void fillPolygon(int[] xPoints, int[] yPoints, int numPoints) {
        if (numPoints != xPoints.length) {
            throw new IllegalArgumentException("drawPolygon: the number of points does not match the size of the xPoints array");
        } else if (numPoints != yPoints.length) {
            throw new IllegalArgumentException("drawPolygon: the number of points does not match the size of the yPoints array");
        }
        this.dc.fillPolygon(xPoints, yPoints);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw the thick outline of a closed polygon defined by arrays of x and y coordinates.
     * Each pair of (x,y) coordinates defines a point.
     * <p>
     * The last point is automatically connected to the first point.
     *
     * @param xPoints   an array of x coordinates
     * @param yPoints   an array of y coordinates
     * @param numPoints the total number of points in the polygon
     * @param thickness the thickness of the outer border
     */

    public void drawThickPolygon(int[] xPoints, int[] yPoints, int numPoints, int thickness) {
        this.dc.setStroke(new BasicStroke(thickness));
        this.drawPolygon(xPoints, yPoints, numPoints);
        this.dc.setStroke(null);
    }

    /**
     * Draw a String on the screen.
     * (x,y) is the position of the left most character.
     *
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @param str the string to be drawn
     */

    public void drawString(int x, int y, String str) {
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_BOTTOM_LEFT);
        this.dc.drawString(str, x, y);
        this.dc.setOrigin(DLibX.DConsole.ORIGIN_TOP_LEFT);
        if (!this.buffered) this.redraw();
    }

    /**
     * Draw the picture specified by fileName on the screen with the top corner at coordinates (x, y).
     * <p>
     * GIF and JPEG file formats are supported.
     *
     * @param x        the x coordinate of the top left corner
     * @param y        the y coordinate of the top left corner
     * @param fileName the name of the file containing the picture.
     */

    public void drawIcon(int x, int y, String fileName) {
        this.dc.drawImage(fileName, x, y);
        if (!this.buffered) this.redraw();
    }

    /**
     * Set the current color of the console.
     * All shapes will be drawn in this color until it is changed again.
     *
     * @param c the new color
     */

    public void setColor(Color c) {
        this.dc.setPaint(c);
    }

    /**
     * Set the current font of the console.
     * All strings will be drawn in this font until it is changed again.
     *
     * @param f the new font
     */

    public void setFont(Font f) {
        this.dc.setFont(f);
    }

    /**
     * Set the buffering mode of the Console.
     * <p>
     * If buffered mode is turned on, you may need to call redraw before the graphics are displayed on the screen.
     * Buffering will help prevent screen flicker.
     *
     * @param inUse true if you want to turn on buffered mode, false to turn it off
     */

    public void setBufferedMode(boolean inUse) {
        this.buffered = inUse;
    }

    /**
     * Redraw components on the screen.
     * If you are in buffered mode you will need to call this to get graphics to be drawn on the screen.
     */

    public void redraw() {
        this.dc.redraw();
    }

    /**
     * Clear everything from the screen.
     */

    public void clear() {
        this.dc.clear();
    }

    /**
     * Clear a rectangular section of the screen by filling in the background colour in the specified area.
     * (x, y) is the top left corner of the rectangle.
     *
     * @param x      the x coordinate of the top left corner
     * @param y      the y coordinate of the top left corner
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */

    public void clearRect(int x, int y, int width, int height) {
        final Paint old = this.dc.getPaint();
        this.dc.setPaint(this.dc.getBackground());
        this.fillRect(x, y, width, height);
        this.dc.setPaint(old);
    }

    /**
     * Saves screen-shot of DConsole to specified file name.
     * Image format is determined from file extension.
     *
     * @param filename The location to save to
     */

    public void saveImage(String filename) {
        this.dc.saveImage(filename);
    }

    /**
     * Pauses the program for a given length of time.
     *
     * @param ms the length of time to pause the program in milliseconds
     */

    public void pause(int ms) {
        DLibX.DConsole.pause(ms);
    }

    /**
     * Pauses the program for a given length of time.
     *
     * @param ms the length of time to pause the program in milliseconds
     * @param ns 0-999999 additional nanoseconds to sleep.
     */

    public void pause(int ms, int ns) {
        DLibX.DConsole.pause(ms, ns);
    }

    /**
     * Returns the current width of the console.
     *
     * @return the width of the console
     */

    public int getWidth() {
        return this.dc.getWidth();
    }

    /**
     * Returns the current height of the console.
     *
     * @return the height of the console
     */

    public int getHeight() {
        return this.dc.getHeight();
    }

    /**
     * Check if the user has typed a character.
     * Example: if(dc.hasChar()) { ... }
     *
     * @return true if a character is available, false otherwise
     */

    public boolean hasChar() {
        return this.adapter.hasChar();
    }

    /**
     * Get the first available character from the user.
     * If no character is currently available, this will pause until the user types a character.
     * Example: char ch = dc.getChar()
     *
     * @return the first available character from the user
     */

    public char getChar() {
        return this.adapter.getChar();
    }

    /**
     * Check if a given key is currently pressed on the keyboard.
     * <p>
     * Example: if(dc.isKeyPressed('a')) { ... }
     *
     * @param  c the key that will be checked
     * @return   true if the specified key is pressed, and false otherwise
     */

    public boolean isKeyPressed(char c) {
        return this.adapter.isKeyPressed(c);
    }

    /**
     * Check if the user has clicked the left mouse button.
     * <p>
     * This will continue to return true after a single click until {@link #getLeftMouseClick()} has been called.
     * If it has been clicked multiple times, this method will continue to return true until {@link #getLeftMouseClick()} has been called once for each press.
     * <p>
     * Example: if(dc.hasLeftMouseClick()) { MouseData m = dc.getLeftMouseClick() }
     *
     * @return true if the specified key is pressed, and false otherwise
     */

    public boolean hasLeftMouseClick() {
        return this.adapter.hasLeftMouseClick();
    }

    /**
     * Check if the user has clicked the right mouse button.
     * <p>
     * This will continue to return true after a single click until {@link #getRightMouseClick()} has been called.
     * If it has been clicked multiple times, this method will continue to return true until {@link #getRightMouseClick()} has been called once for each press.
     * <p>
     * Example: if(dc.hasRightMouseClick()) { MouseData m = dc.getRightMouseClick() }
     *
     * @return true if the right button has been clicked, false otherwise
     */

    public boolean hasRightMouseClick() {
        return this.adapter.hasRightMouseClick();
    }

    /**
     * Get the location of where the left mouse button was clicked.
     * Removes this click from the list of clicks made by the user.
     * <p>
     * If the user has clicked the mouse more than once, this will return the first event, and will return the next event when called again.
     * <p>
     * If the user has not clicked the mouse, the program will pause until it is clicked.
     *
     * @return contains the location at which the left button was clicked
     */

    public MouseData getLeftMouseClick() {
        return this.adapter.getLeftMouseClick();
    }

    /**
     * Get the location of where the right mouse button was clicked.
     * Removes this click from the list of clicks made by the user.
     * <p>
     * If the user has cliced the mouse more than once, this will return the first event, and will return the next event when called again.
     * <p>
     * If the user has not clicked the mouse, the program will pause until it is clicked.
     *
     * @return contains the location at which the right button was clicked
     */

    public MouseData getRightMouseClick() {
        return this.adapter.getRightMouseClick();
    }

    /**
     * Get information about the current location of the mouse in the console.
     * <p>
     * If the mouse has left the window, this will continue to return the last location the mouse was in the console.
     *
     * @return contains the current location of the mouse in the console window
     */

    public MouseData getMouseLocation() {
        final Point p = this.dc.getMousePosition();
        return new MouseData(p.x, p.y);
    }

    /**
     * Get information about if the left mouse button is currently pressed.
     *
     * @return true if the mouse button is pressed, false otherwise
     */

    public boolean mouseLeftIsPressed() {
        return dc.isMouseButton(MouseEvent.BUTTON1);
    }

    /**
     * Get information about if the right mouse button is currently pressed.
     *
     * @return true if the mouse button is pressed, false otherwise
     */

    public boolean mouseRightIsPressed() {
        return dc.isMouseButton(MouseEvent.BUTTON3);
    }

    /**
     * Makes the console invisible.
     */

    public void hideConsole() {
        this.dc.setVisible(false);
    }

    /**
     * Makes the console visible (if currently hidden).
     */

    public void showConsole() {
        this.dc.setVisible(true);
    }

    /**
     * Set where the console is on the screen.
     * The top left of the console will be at position (x, y).
     *
     * @param x the x component of the top left corner
     * @param y the y component of the top left corner
     */

    public void setLocation(int x, int y) {
        this.dc.getFrame().setLocation(x, y);
    }

    /**
     * Set the size of the console.
     *
     * @param width  the new width of the console
     * @param height the new height of the console
     */

    public void changeSize(int width, int height) {
        this.dc.setSize(new Dimension(width, height));
    }

    private class DConsoleAdapter extends EventAdapter {
        private final Object lock;
        private final ArrayList<KeyData> keys        = new ArrayList<>();
        private final Queue<Character>   chars       = new LinkedList<>();
        private final Queue<MouseData>   leftClicks  = new LinkedList<>();
        private final Queue<MouseData>   rightClicks = new LinkedList<>();

        private int wait = -1;

        public DConsoleAdapter(Object lock) {
            this.lock = lock;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            final Character key = Character.valueOf(e.getKeyChar());
            this.chars.offer(key);
            this.notify(0);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            final KeyData key = new KeyData(e.getKeyChar());
            if (!this.keys.contains(key)) this.keys.add(key);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            final KeyData key = new KeyData(e.getKeyChar());
            if (this.keys.contains(key)) this.keys.remove(key);
        }

        private class KeyData {
            public final char keycode;
            public boolean active = true;

            KeyData(char keycode) {
                this.keycode = keycode;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null) return false;
                if (o == this) return true;
                return (this.keycode == ((KeyData)o).keycode);
            }

            @Override
            public int hashCode() {
                return this.keycode;
            }
        }

        public void mouseClicked(MouseEvent e) {
            final MouseData m = new MouseData(e.getX(), e.getY());

            if (e.getButton() == MouseEvent.BUTTON1) {
                this.leftClicks.offer(m);
                this.notify(1);
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                this.rightClicks.offer(m);
                this.notify(2);
            }
        }

        private void wait(int wait) {
            this.wait = wait;
            try {
                synchronized (this.lock) {
                    this.lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void notify(int wait) {
            if (this.wait == wait) {
                synchronized (this.lock) {
                    this.lock.notify();
                    this.wait = -1;
                }
            }
        }

        boolean isKeyPressed(char c) {
            final int index = this.keys.indexOf(new KeyData(c));
            return (index != -1 && this.keys.get(index).active);
        }

        char getChar() {
            if (!hasChar()) this.wait(0);
            return this.chars.remove();
        }

        boolean hasChar() {
            return !this.chars.isEmpty();
        }

        MouseData getLeftMouseClick() {
            if (!hasLeftMouseClick()) this.wait(1);
            return this.leftClicks.remove();
        }

        MouseData getRightMouseClick() {
            if (!hasRightMouseClick()) this.wait(2);
            return this.rightClicks.remove();
        }

        boolean hasLeftMouseClick() {
            return !this.leftClicks.isEmpty();
        }

        boolean hasRightMouseClick() {
            return !this.rightClicks.isEmpty();
        }
    }
}
