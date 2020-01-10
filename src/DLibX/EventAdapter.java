package DLibX;

import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;

/**
 * An abstract adapter class for receiving input events.
 * The methods in this class are empty.
 * This class exists as convenience for creating listener objects for use with DConsoleX.
 * <p>
 * Extend this class to create an  listener and override the methods for the events of interest.
 * <p>
 * Create a EventAdapter object using the extended class and then register it with a DConsoleX using the it's <code>addEventListener</code> method.
 * When the DConsoleX receives, the relevant method in the listener object is invoked, and the relevant Event is passed to it.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public abstract class EventAdapter { // implements EventListener {
    /**
     * Invoked when the DConsoleX's close button is pressed.
     *
     * @param e Window Event
     */
    public void windowClosing(WindowEvent e) {}

    /**
     * Invoked when the DConsoleX is resized.
     *
     * @param e Component Event
     */
    public void componentResized(ComponentEvent e) {}

    /**
     * Invoked when the DConsoleX gains keyboard focus.
     *
     * @param e Focus Event
     */
    public void focusGained(FocusEvent e) {}

    /**
     * Invoked when the DConsoleX looses keyboard focus.
     *
     * @param e Focus Event
     */
    public void focusLost(FocusEvent e) {}

    /**
     * Invoked when a key is pressed on the DConsoleX.
     *
     * @param e Key Event
     */
    public void keyPressed(KeyEvent e) {}

    /**
     * Invoked when a key is released on the DConsoleX.
     *
     * @param e Key Event
     */
    public void keyReleased(KeyEvent e) {}

    /**
     * Invoked when a key is typed on the DConsoleX.
     *
     * @param e Key Event
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a mouse button is clicked on the DConsoleX.
     *
     * @param e Mouse Event
     */

    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button is pressed on the DConsoleX.
     *
     * @param e Mouse Event
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button is released on the DConsoleX.
     *
     * @param e Mouse Event
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when the mouse is dragged over the DConsoleX.
     *
     * @param e Mouse Event
     */
    public void mouseDragged(MouseEvent e) {}

    /**
     * Invoked when the mouse is moved over the DConsoleX.
     *
     * @param e Mouse Event
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Invoked when the mouse wheel is turned over the DConsoleX.
     *
     * @param e Mouse Wheel Event
     */
    public void mouseWheelMoved(MouseWheelEvent e) {}

    /**
     * Invoked when the DConsoleX is closing due to an uncaught exception.
     */
    public void uncaughtException() {}
}
