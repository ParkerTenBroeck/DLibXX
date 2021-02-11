package DLibX.legacy;

/**
 * Contains the information about the position of the mouse at a given point.
 *
 * Created for compatibility with the original DConsole
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class MouseData {
    private final int x;
    private final int y;

    protected MouseData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the X value of the mouse location.
     *
     * @return  The X coordinate.
     */

    public int getX() {
        return this.x;
    }

    /**
     * Returns the Y value of the mouse location.
     *
     * @return  The Y coordinate.
     */

    public int getY() {
        return this.y;
    }
}
