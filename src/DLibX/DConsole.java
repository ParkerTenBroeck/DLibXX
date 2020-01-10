package DLibX;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import javax.imageio.ImageIO;

/**
 * Handles user input and output to graphical device.
 * <p>
 * The DConsole is used to make drawing to the screen and handling user input easier for use in rapid game and application development.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class DConsole extends DCanvas {
    /**
     * DOES NOTHING when close button is pressed.
     */
    public static final int ON_CLOSE_DO_NOTHING = 0;
    /**
     * HIDES console when close button is pressed.
     */
    public static final int ON_CLOSE_HIDE = 1;
    /**
     * DISPOSES of console when close button is pressed.
     */
    public static final int ON_CLOSE_DISPOSE = 2;
    /**
     * EXITS program when close button is pressed.
     */
    public static final int ON_CLOSE_EXIT = 3;
    /**
     * Cursor is visible and behaves normally.
     */
    public static final int CURSOR_NORMAL = 4;
    /**
     * Cursor is invisible when over the window, but not restricted.
     */
    public static final int CURSOR_HIDDEN = 5;
    /**
     * Cursor is invisible and restricted to the window.
     * This provides virtual and unlimited cursor movement.
     */
    public static final int CURSOR_DISABLED = 6;

    //////////////////////////////////////////////////////////////////////////// FUNCTIONAL PARTS OF WINDOW / DRAWING CANVAS

    private final Frame                   frame;       // frame that holds drawing panel
    private final Canvas                  canvas;      // canvas that is drawn to

    private VolatileImage                 onscreen;    // what is displayed on screen

    private final ComponentAdapter        component;   // window resize listener
    private final ArrayList<EventAdapter> event;       // event listen
    private final ExceptionList           exception;   // exception handler
    private final FocusAdapter            focus;       // focus listener
    private final KeyList                 key;         // keyboard input listener
    private final MouseList               mouse;       // mouse input listener
    private final WindowAdapter           window;      // window event listener

    private final GraphicsDevice          screen;      // used to address the main drawing device about window transforms
    private final GraphicsEnvironment     environment; // get environment info and make compatible stuff
    private final GraphicsConfiguration   config;      // get configuration info and make compatible stuff

    private final Robot                   robot;       // deals with mouse lock

    private final ImageLoader             images;      // image loading

    private final Cursor                  blank;       // invisible cursor

    //////////////////////////////////////////////////////////////////////////// WINDOW STATE IMFORMATION

    private Dimension preSize;     // restores previous window bounds
    private boolean   preResize;   // restores previous window resize-ability
    private boolean   preVisible;  // restores previous window resize-ability
    private boolean   fullscreen;  // full-screen catch
    private boolean   undecorated; // undecorated catch
    private boolean   lock;        // mouse lock catch
    private boolean   lockFocus;
    private int       close;

    //////////////////////////////////////////////////////////////////////////// CONSTRUCTORS

    /**
     * Creates a DConsole Window with default width, height, and title.
     * <p>
     * The window is created with the size 600px by 400px.
     */

    public DConsole() {
        // this(null, 600, 400, true);
        this(null, 900, 600, true);
    }

    /**
     * Creates a DConsole Window with default title.
     *
     * @param width  Width of window
     * @param height Height of window
     */

    public DConsole(int width, int height) {
        this(null, width, height, true);
    }

    /**
     * Creates a DConsole Window.
     * <p>
     * If title is <code>null</code>, default title is given.
     *
     * @param title   Title given to window
     * @param width   Width of window
     * @param height  Height of window
     * @param visible Visability of window
     */

    public DConsole(String title, int width, int height, boolean visible) {
        super(width, height, Transparency.OPAQUE);

        System.setProperty("sun.awt.noerasebackground", "true"); // set system properties
        System.setProperty("sun.java2d.opengl", "true");

        this.component = new ComponentList(); // set up listeners
        this.event     = new ArrayList<>();
        this.exception = new ExceptionList();
        this.focus     = new FocusList();
        this.key       = new KeyList();
        this.mouse     = new MouseList();
        this.window    = new WindowList();

        if (title == null) title = initTitle(); // if title is null, make default title of main class name
        this.frame = new Frame(title); // make main frame

        this.canvas = new Canvas() { // set up all event listeners. anon because why not
            public static final long serialVersionUID = 34742732665L; //fisharecool (lint told me to make this)

            @Override
            public void paint(Graphics g) {
                g.drawImage(DConsole.this.onscreen, 0, 0, this); // draw front buffer
                Toolkit.getDefaultToolkit().sync(); // yaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy
            }

            @Override
            public void update(Graphics g) {
                paint(g);
            }
        };

        this.blank = DConsole.makeBlankCursor();
        this.images = new ImageLoader();

        this.robot = DConsole.makeRobot();

        this.environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.screen = this.environment.getDefaultScreenDevice();
        this.config = this.screen.getDefaultConfiguration();

        this.canvas.setSize(width, height);
        this.canvas.setBackground(Color.white);
        this.canvas.setFocusTraversalKeysEnabled(false);

        this.canvas.addComponentListener(this.component); // add listeners
        this.canvas.addFocusListener(this.focus);
        this.canvas.addKeyListener(this.key);
        this.canvas.addMouseListener(this.mouse);
        this.canvas.addMouseMotionListener(this.mouse);
        this.canvas.addMouseWheelListener(this.mouse);
        Thread.setDefaultUncaughtExceptionHandler(this.exception);

        this.frame.addWindowListener(this.window);
        this.frame.add(this.canvas);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        this.fix();

        this.close = DConsole.ON_CLOSE_EXIT;

        this.lockFocus = true;

        this.frame.setVisible(visible);

        // this.canvas.grabFocus();
        this.canvas.requestFocusInWindow();
    }

    private static Robot makeRobot() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return r;
    }

    private static Cursor makeBlankCursor() { // makes nice, blank cursor. moved here because ugly in constructor
        BufferedImage i = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Point p = new Point(0, 0);
        String n = "BLANK";
        return Toolkit.getDefaultToolkit().createCustomCursor(i, p, n);
    }

    private String initTitle() { //gets title from main's class name
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        String t = s[s.length - 1].getClassName();
        t = t.substring(t.lastIndexOf(".") + 1);
        return t.replaceAll( // http://stackoverflow.com/a/2560017
            String.format("%s|%s|%s",
               "(?<=[A-Z])(?=[A-Z][a-z])",
               "(?<=[^A-Z])(?=[A-Z])",
               "(?<=[A-Za-z])(?=[^A-Za-z])"
            ),
            " "
        );
    }

    private synchronized void fix() {
        Dimension dimensions = this.canvas.getSize();
        double width = Math.max(1, dimensions.getWidth());
        double height = Math.max(1, dimensions.getHeight());
        dimensions.setSize(width, height);

        VolatileImage tmpImage = this.config.createCompatibleVolatileImage(dimensions.width, dimensions.height);

        if (this.onscreen != null) {
            Graphics2D g = null;
            try {
                g = tmpImage.createGraphics();
                g.drawImage(this.onscreen, 0, 0, this.canvas);
            } finally {
                if (g != null) g.dispose();
            }
            this.onscreen.flush();
        }

        this.onscreen = tmpImage;

        this.setSize(dimensions.width, dimensions.height);

        this.frame.validate();
        this.canvas.validate();
    }

    //////////////////////////////////////////////////////////////////////////// CLEAR STUFF

    /**
     * Clears screen, and draws buffer.
     */

    public synchronized void redraw() {
        Graphics2D g = null;
        try {
            g = this.onscreen.createGraphics();
            g.drawImage(this.getImage(), 0, 0, this.canvas);
        } finally {
            g.dispose();
        }
        this.canvas.repaint();
    }

    /**
     * Draws an image specified by the filename on the screen at the specified coordinates.
     * <p>
     * GIF, JPEG and PNG files are guaranteed to be supported.
     *
     * @param filename the location of the file containing the image
     * @param x        the x coordinate of the origin
     * @param y        the y coordinate of the origin
     */

    public void drawImage(String filename, double x, double y) {
        if (!this.images.contains(filename))
            this.images.load(filename);
        this.drawImage(this.images.get(filename), x, y);
    }

    /**
     * Takes a snapshot of the DConsole and saves it to the specified file.
     *
     * @param filename the file to save to
     */

    public void saveImage(String filename) {
        final String ext = filename.substring(filename.lastIndexOf(".") + 1);

        try {
            ImageIO.write(this.onscreen.getSnapshot(), ext, new File(filename));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    //////////////////////////////////////////////////////////////////////////// WINDOW ATTRIBUTE GETTERS

    /**
     * Returns the pixel width of the drawable surface of the DConsole.
     *
     * @return the width of the drawable surface of the DConsole in pixels
     */

    public int getWidth() {
        return this.canvas.getWidth();
    }

    /**
     * Returns the pixel height of the drawable surface of the DConsole.
     *
     * @return the height of the drawable surface of the DConsole in pixels
     */

    public int getHeight() {
        return this.canvas.getHeight();
    }

    /**
     * Returns Dimensions of the drawable surface of the DConsole.
     *
     * @return the dimensions of the drawable surface of the DConsole
     */

    public Dimension getSize() {
        return this.canvas.getSize();
    }

    /**
     * Returns the title of the DConsole.
     *
     * @return the title of the DConsole
     */

    public String getTitle() {
        return this.frame.getTitle();
    }

    /**
     * Returns a list of icons used by the DConsole.
     *
     * @return the icons used by the DConsole
     */

    public Image[] getIcons() {
        java.util.List<Image> l = this.frame.getIconImages();
        Image[] r = new Image[l.size()];
        l.toArray(r);
        return r;
    }

    //////////////////////////////////////////////////////////////////////////// WINDOW ATTRIBUTE SETTERS

    /**
     * Sets the DCanvas's dimensions.
     * The underlying VolatileImage and Graphics context will be re-created.
     *
     * @param width  the width to set the canvas to in pixels
     * @param height the height to set the canvas to in pixels
     */

    public void setSize(int width, int height) {
        this.resize(width, height);
        this.setSize(new Dimension(width, height));
    }

    /**
     * Sets the height and width of the drawable surface of the DConsole.
     *
     * @param dimensions the Dimensions to set the DConsole to be
     */

    public void setSize(Dimension dimensions) {
        this.canvas.setSize(dimensions);
        this.frame.pack();
    }

    /**
     * Sets the minimum size of the window.
     * Unless undecorated, the window size will be slightly larger than the canvas size.
     *
     * @param width  the minimum pixel width the DConsole can be
     * @param height the minimum pixel height the DConsole can be
     */

    public void setMinimumSize(int width, int height) {
        this.setMinimumSize(new Dimension(width, height));
    }

    /**
     * Sets the minimum size of the window.
     * Unless undecorated, the window size will be slightly larger than the canvas size.
     *
     * @param dimensions the minimum Dimensions the DConsole can be
     */

    public void setMinimumSize(Dimension dimensions) {
        this.frame.setMinimumSize(dimensions);
    }

    /**
     * Sets the title of the DConsole.
     *
     * @param title the title to give the DConsole
     */

    public void setTitle(String title) {
        this.frame.setTitle(title);
    }

    /**
     * Gives the DConsole a list of possible icons to decorate the window with.
     *
     * @param images the image(s) to use as the icon for the DConsole
     */

    public void setIcon(Image... images) {
        this.frame.setIconImages(Arrays.asList(images));
    }

    //////////////////////////////////////////////////////////////////////////// COMPONENT GETTERS

    /**
     * Returns the window frame being drawn on.
     * Any operations applied to it may be overwritten by any method calls from the DConsole.
     *
     * @return the window Frame being drawn on
     */

    public Frame getFrame() {
        return this.frame;
    }

    /**
     * Returns the Canvas component being drawn on.
     * Any operations applied to it may be overwritten by any method calls from the DConsole.
     *
     * @return the Canvas component being drawn on
     */

    public Canvas getCanvas() {
        return this.canvas;
    }

    //////////////////////////////////////////////////////////////////////////// DO STUFF

    /**
     * Pauses operation of the program for a given amount of time.
     *
     * @param ms the length of time to pause the program in milliseconds
     */

    public static void pause(int ms) {
        DConsole.pause(ms, 0);
    }

    /**
     * Pauses operation of the program for a given amount of time.
     *
     * @param ms the length of time to pause the program in milliseconds
     * @param ns 0-999999 additional nanoseconds to sleep.
     */

    public static void pause(int ms, int ns) {
        try {
            Thread.sleep(ms, ns);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //////////////////////////////////////////////////////////////////////////// WINDOW ATTRIBUTE SETTERS

    /**
     * Sets what to do when the DConsole's close button is pressed.
     *
     * @param operation the close operation
     */

    public void setCloseOperation(int operation) {
        this.close = operation;
    }

    /**
     * Closes the DConsole and releases any system memory used by it.
     */

    @Override
    public void dispose() {
        super.dispose();
        this.frame.dispose();
        this.images.unloadAll();
    }

    /**
     * Sets if the DConsole should be visible on screen.
     *
     * @param inUse if the DConsole should be visible on-screen
     */

    public void setVisible(boolean inUse) {
        if (!this.fullscreen) {
            this.frame.setVisible(inUse);
        } else {
            this.preVisible = inUse;
        }
    }

    /**
     * Sets if the DConsole should be resizeable by user input.
     *
     * @param inUse if the DConsole should be resizeable by the user
     */

    public void setResizable(boolean inUse) {
        if (!this.fullscreen) {
            this.frame.setResizable(inUse);
        } else {
            this.preResize = inUse;
        }
    }

    /**
     * Sets if the DConsole is to be decorated by the window manager.
     *
     * @param inUse if the DConsole should be decorated by the window manager
     */

    public void setUndecorated(boolean inUse) {
        if (inUse == this.undecorated) return;
        this.undecorated = inUse;
        this.frame.dispose();
        this.frame.setUndecorated(inUse);
        this.frame.setVisible(true);
    }

    /**
     * Sets if the DConsole should be in exclusive full-screen mode.
     *
     * @param inUse if the DConsole should be in an exclusive full-screen view
     */

    public void setFullscreen(boolean inUse) {
        if (inUse && this.screen.getFullScreenWindow() != this.frame && this.screen.isFullScreenSupported()) {
            this.fullscreen = true;
            this.preSize = this.canvas.getSize();
            this.preResize = this.frame.isResizable();
            this.preVisible = this.frame.isVisible();
            this.frame.dispose();
            this.frame.setUndecorated(true);
            this.frame.setResizable(false);
            this.screen.setFullScreenWindow(this.frame);
            this.canvas.requestFocus();
        } else if (this.screen.getFullScreenWindow() == this.frame) {
            this.frame.dispose();
            this.frame.setUndecorated(this.undecorated);
            this.screen.setFullScreenWindow(null);
            this.canvas.setSize(this.preSize);
            this.frame.setResizable(this.preResize);
            this.frame.setVisible(this.preVisible);
            this.canvas.requestFocus();
        }
    }

    /**
     * Checks if a number of keys are simultaneously pressed.
     *
     * @param keycode the key(s) to check for
     *
     * @return if all the keys are pressed
     */

    public boolean isKeyPressed(int... keycode) {
        return this.key.isKey(keycode);
    }

    /**
     * Checks if a number of keys are simultaneously pressed.
     *
     * @param key the key(s) to check for
     *
     * @return if all the keys are pressed
     */

    public boolean isKeyPressed(char... key) {
        int[] k = new int[key.length];
        for (int i = 0; i < k.length; i++) k[i] = KeyEvent.getExtendedKeyCodeForChar(key[i]);
        return this.key.isKey(k);
    }

    /**
     * Checks if a key is pressed, if it is it removes the key from the list of currently pressed keys.
     *
     * @param keycode the key press to retrieve
     *
     * @return if the key was pressed
     */

    public boolean getKeyPress(int keycode) {
        return this.key.getKey(keycode);
    }

    /**
     * Checks if a key is pressed, if it is it removes the key from the list of currently pressed keys.
     *
     * @param key the key press to retrieve
     *
     * @return if the key was pressed
     */

    public boolean getKeyPress(char key) {
        return this.key.getKey(KeyEvent.getExtendedKeyCodeForChar(key));
    }

    /**
     * Returns a list of all keys currently pressed, in the order they were pressed.
     *
     * @return all keys currently pressed
     */

    public int[] getKeys() {
        return this.key.getKeys();
    }

    /**
     * Clears the list of keys currently pressed.
     */

    public void clearKeys() {
        this.key.clearKeys();
    }

    /**
     * Returns a Point representation of where the mouse cursor currently is over the DConsole.
     *
     * @return the current mouse cursor position represented by a Point
     */

    public Point getMousePosition() {
        return this.mouse.getMousePosition();
    }

    /**
     * Returns the X position of where the mouse cursor currently is over the DConsole.
     *
     * @return the current mouse cursor X position
     */

    public int getMouseXPosition() {
        return this.mouse.getMouseXPosition();
    }

    /**
     * Returns the Y position of where the mouse cursor currently is over the DConsole.
     *
     * @return the current mouse cursor Y position
     */

    public int getMouseYPosition() {
        return this.mouse.getMouseYPosition();
    }

    /**
     * Returns the current mouse scroll-wheel position, relative to when the DConsole was created or reset.
     *
     * @return the current mouse-wheel scroll position
     */

    public double getScrollPosition() {
        return this.mouse.getScrollPosition();
    }

    /**
     * Sets the mouse scroll-wheel position to 0.
     */

    public void resetScrollPosition() {
        this.mouse.resetScrollPosition();
    }

    /**
     * Checks if a number of mouse buttons are simultaneously pressed.
     *
     * @param button the mouse buttons to be checked for
     *
     * @return if all the mouse buttons are pressed
     */

    public boolean isMouseButton(int... button) {
        return this.mouse.isMouseButton(button);
    }

    /**
     * Checks if a mouse button is pressed, if it is it removes the key from the list of currently pressed buttons.
     *
     * @param button the mouse button press to be retrieved
     *
     * @return if the mouse button was pressed
     */

    public boolean getMouseButton(int button) {
        return this.mouse.getMouseButton(button);
    }

    /**
     * Returns a list of all mouse buttons currently pressed, in the order they were pressed.
     *
     * @return all mouse buttons currently pressed
     */

    public int[] getMouseButtons() {
        return this.mouse.getMouseButtons();
    }

    /**
     * Clears the list of mouse buttons currently pressed.
     */

    public void clearMouseButtons() {
        this.mouse.clearMouseButtons();
    }

    /**
     * Sets how the mouse cursor should behave when the DConsole is focused.
     *
     * @param mode the mouse mode to set to
     */

    public void setMouseMode(int mode) {
        switch (mode) {
        case CURSOR_NORMAL:
            setMouseVisibility(true);
            setMouseLock(false);
            break;
        case CURSOR_HIDDEN:
            setMouseVisibility(false);
            setMouseLock(false);
            break;
        case CURSOR_DISABLED:
            setMouseVisibility(false);
            setMouseLock(true);
            break;
        }
    }

    private void setMouseVisibility(boolean visible) {
        if (visible) {
            this.canvas.setCursor(Cursor.getDefaultCursor());
        } else {
            this.canvas.setCursor(this.blank);
        }
    }

    private void setMouseLock(boolean inUse) {
        this.lock = inUse;
        if (this.lockFocus) this.mouse.setMouseLock(inUse);
    }

    private Rectangle mouseLock(Point old) {
        Point position = this.canvas.getLocationOnScreen();
        Dimension size = new Dimension(this.getWidth(), this.getHeight());
        position.translate(size.width/2, size.height/2);
        this.robot.mouseMove(position.x, position.y);
        return new Rectangle(old.x-size.width/2, old.y-size.height/2, size.width, size.height);
    }

    /**
     * Adds the specified event listener to receive events from this console.
     * If listener adapter is null, no exception is thrown and no action is performed.
     *
     * @param adapter the event listener to add
     */

    public void addEventListener(EventAdapter adapter) {
        if (adapter != null) this.event.add(adapter);
    }

    /**
     * Returns all EventListeners currently registered with the DConsole, in the order they were added and are executed.
     *
     * @return all event listeners registered to the DConsole, in the order they are executed
     */

    public EventAdapter[] getEventListeners() {
        EventAdapter[] r = new EventAdapter[event.size()];
        this.event.toArray(r);
        return r;
    }

    /**
     * Removes the specified EventListener from the DConsole.
     *
     * @param adapter the event listener to remove
     */

    public void removeEventListener(EventAdapter adapter) {
        if (adapter != null) this.event.remove(adapter);
    }

    /**
     * Registers a font file with the graphics environment.
     *
     * @param file the font to load
     *
     * @return the font name
     */

    public String registerFont(String file) {
        File f = new File(file);
        return registerFont(f);
    }

    /**
     * Registers a font file with the graphics environment.
     *
     * @param file the font to load
     *
     * @return the font name
     */

    public String registerFont(File file) {
        try {
            Font f = Font.createFont(Font.TRUETYPE_FONT, file);
            environment.registerFont(f);
            return f.getName();
        } catch (Exception e) {
            System.out.println("Font file not true type font, or cannot file cannot be found.");
            System.exit(1);
        }
        return null;
    }

    private class ExceptionList implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            for (EventAdapter i: DConsole.this.event) i.uncaughtException(); // notify that closing (for clean-up)

            e.printStackTrace(); // basically handle by not handling and closing with a fail status
            System.exit(1);
        }
    }

    private class WindowList extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            for (EventAdapter i: DConsole.this.event) i.windowClosing(e); // notify that closing

            switch(DConsole.this.close) { // mimic what swing does
            case DConsole.ON_CLOSE_DO_NOTHING:
                break;
            case DConsole.ON_CLOSE_DISPOSE:
                DConsole.this.dispose();
                break;
            case DConsole.ON_CLOSE_HIDE:
                DConsole.this.setVisible(false);
                break;
            default:
                System.exit(0);
                break;
            }
        }
    }

    private class ComponentList extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            DConsole.this.fix(); // make drawing surface ready again
            for (EventAdapter i: DConsole.this.event) i.componentResized(e); // notify that screen size changed
        }
    }

    // /**
    //     This one might be fucky
    //  */

    private class FocusList extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            DConsole.this.lockFocus = false;
            DConsole.this.mouse.setMouseLock(false);
            for (EventAdapter i: DConsole.this.event) i.focusLost(e); // notify that focus changed
        }

        @Override
        public void focusGained(FocusEvent e) {
            DConsole.this.lockFocus = true;
            DConsole.this.mouse.setMouseLock(DConsole.this.lock);
            for (EventAdapter i: DConsole.this.event) i.focusGained(e); // notify that focus changed
        }
    }

    private class KeyList extends KeyAdapter {
        private ArrayList<KeyData> k = new ArrayList<>();
        @Override
        public void keyTyped(KeyEvent e) {
            for (EventAdapter i: DConsole.this.event) i.keyTyped(e);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            KeyData temp = new KeyData(e.getKeyCode());
            if (!this.k.contains(temp)) this.k.add(temp);
            for (EventAdapter i: DConsole.this.event) i.keyPressed(e);
        }
        @Override
        public void keyReleased(KeyEvent e) {
            KeyData temp = new KeyData(e.getKeyCode());
            if (this.k.contains(temp)) this.k.remove(temp);
            for (EventAdapter i: DConsole.this.event) i.keyReleased(e);
        }
        class KeyData {
            public final int keycode;
            public boolean active;
            public KeyData(int keycode) {
                this.keycode = keycode;
                this.active = true;
            }
            @Override
            public String toString() {
                return "[" + keycode + ((active)?'+':'-') + "]";
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
        public boolean isKey(int[] k) {
            for (int i: k) {
                int t = this.k.indexOf(new KeyData(i));
                if (t == -1 || this.k.get(t).active == false) return false;
            }
            return true;
        }
        public boolean getKey(int k) {
            int t = this.k.indexOf(new KeyData(k));
            if (t == -1 || this.k.get(t).active == false) return false;
            this.k.get(t).active = false;
            return true;
        }
        public int[] getKeys() {
            int l = 0;
            int i = 0;
            for (KeyData k: this.k) if (k.active == true) l++;
            int[] r = new int[l];
            for (KeyData k: this.k) if (k.active == true) r[i++] = k.keycode;
            return r;
        }
        public void clearKeys() {
            for (KeyData k: this.k) k.active = false;
        }
    }

    private class MouseList extends MouseAdapter {
        private ArrayList<Integer> b = new ArrayList<>();
        private double s;
        private int l;
        Point p = new Point(0,0);
        Point r = new Point(0,0);
        @Override
        public void mouseClicked(MouseEvent e) {
            this.mouseMove(e);
            for (EventAdapter i: DConsole.this.event) i.mouseClicked(e);
        }
        @Override
        public void mousePressed(MouseEvent e) {
            this.mouseMove(e);
            Integer temp = Integer.valueOf(e.getButton());
            if (!this.b.contains(temp)) this.b.add(temp);
            for (EventAdapter i: DConsole.this.event) i.mousePressed(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            this.mouseMove(e);
            Integer temp = Integer.valueOf(e.getButton());
            if (this.b.contains(temp)) this.b.remove(temp);
            for (EventAdapter i: DConsole.this.event) i.mouseReleased(e);
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            this.mouseMove(e);
            for (EventAdapter i: DConsole.this.event) i.mouseDragged(e);
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            this.mouseMove(e);
            for (EventAdapter i: DConsole.this.event) i.mouseMoved(e);
        }
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            this.mouseMove(e);
            this.s += e.getPreciseWheelRotation();
            for (EventAdapter i: DConsole.this.event) i.mouseWheelMoved(e);
        }
        private void mouseMove(MouseEvent e) {
            if (this.l != 0) {
                if (this.r.equals(e.getPoint())) return;
                Rectangle t = DConsole.this.mouseLock(e.getPoint());
                this.r = new Point(t.width,t.height);
                if (this.l == 2) {
                    this.p.translate(t.x, t.y);
                    return;
                } else {
                    this.l = 2;
                }
            }
            this.p = e.getPoint();

        }
        public Point getMousePosition() {
            Point r = new Point(this.p);
            if (DConsole.this.plane == DConsole.PLANE_CARTESIAN) {
                r.setLocation(r.x, DConsole.this.getHeight()-r.y);
            }
            return r;
        }
        public int getMouseXPosition() {
            return this.p.x;
        }
        public int getMouseYPosition() {
            if (DConsole.this.plane == DConsole.PLANE_CARTESIAN)
                return DConsole.this.getHeight()-p.y;
            return this.p.y;
        }
        public double getScrollPosition() {
            return this.s;
        }
        public void resetScrollPosition() {
            this.s = 0;
        }
        public boolean isMouseButton(int[] b) {
            for (int i: b) {
                if (this.b.indexOf(Integer.valueOf(i)) == -1) return false;
            }
            return true;
        }
        public boolean getMouseButton(int b) {
            boolean r = false;
            int t = this.b.indexOf(Integer.valueOf(b));
            if (t == -1) return false;
            this.b.remove(t);
            return true;
        }
        public int[] getMouseButtons() {
            int[] r = new int[this.b.size()];
            for (int i = 0; i < r.length; i++) r[i] = this.b.get(i);
            return r;
        }
        public void clearMouseButtons() {
            this.b.clear();
        }
        public void setMouseLock(boolean b) {
            this.l = b?1:0;
        }
    }
}
