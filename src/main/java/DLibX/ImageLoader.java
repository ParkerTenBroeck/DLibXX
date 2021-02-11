package DLibX;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles the loading and temporary storage of images for quick access.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class ImageLoader {
    private final HashMap<Integer, ImageData> images;
    private final GraphicsConfiguration       config;

    /**
     * Constructs an empty ImageLoader
     */

    public ImageLoader() {
        this.config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        this.images = new HashMap<>();
    }

    /**
     * Checks if the ImageLoader contains an image associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be tested for
     *
     * @return If a VolatileImage associated with the key is currently loaded
     */

    public boolean contains(Object key) {
        Integer hash = key.hashCode();
        return this.images.containsKey(hash);
    }

    /**
     * Removes the image associated with the specified key from the ImageLoader and alleviates any system memory used by it.
     *
     * @param key The key whose associated VolatileImage is to be removed from this map
     *
     * @see #unloadAll()
     */

    public void unload(Object key) {
        Integer hash = key.hashCode();
        ImageData img = this.images.remove(hash);
        if (img != null) img.flush();
    }

    /**
     * Removes all images from the ImageLoader and alleviates any system memory used by them.
     *
     * @see #unload(Object)
     */

    public void unloadAll() {
        Set<Integer> key = this.images.keySet();
        for (Integer hash: key) {
            ImageData img = this.images.remove(hash);
            if (img != null) img.flush();
        }
    }

    /**
     * Returns the image associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be returned
     *
     * @return The VolatileImage associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     *
     * @see #get(Object, int)
     * @see #get(Object, int, int)
     * @see #get(Object, int, int, int)
     * @see #getMap(Object)
     */

    public VolatileImage get(Object key) {
        return this.get(key, 0, 0, 0);
    }

    /**
     * Returns the image associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be returned
     * @param frame The frame of the loaded image to be returned
     *
     * @return The frame of the VolatileImage associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     * @throws ArrayIndexOutOfBoundsException If specified frame does not exist
     *
     * @see #get(Object)
     * @see #get(Object, int, int)
     * @see #get(Object, int, int, int)
     * @see #getMap(Object)
     */

    public VolatileImage get(Object key, int frame) {
        return this.get(key, frame, 0, 0);
    }

    /**
     * Returns the image associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be returned
     * @param x The x index of the tile of the image to be returned
     * @param y The y index of the tile of the image to be returned
     *
     * @return The tile of VolatileImage associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     * @throws ArrayIndexOutOfBoundsException If specified tile does not exist
     *
     * @see #get(Object)
     * @see #get(Object, int)
     * @see #get(Object, int, int, int)
     * @see #getMap(Object)
     */

    public VolatileImage get(Object key, int x, int y) {
        return this.get(key, 0, x, y);
    }

    /**
     * Returns the image associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be returned
     * @param frame The frame of the loaded image to be returned
     * @param x The x index of the tile of the image to be returned
     * @param y The y index of the tile of the image to be returned
     *
     * @return The tile of the frame of the VolatileImage associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     * @throws ArrayIndexOutOfBoundsException If specified frame or tile does not exist
     *
     * @see #get(Object)
     * @see #get(Object, int)
     * @see #get(Object, int, int)
     * @see #getMap(Object)
     */

    public VolatileImage get(Object key, int frame, int x, int y) {
        return this.getImageData(key).get(frame, x, y);
    }

    /**
     * Returns the entire image map associated with the specified key.
     *
     * @param key The key whose associated VolatileImage is to be returned
     *
     * @return The tile of the frame of the VolatileImage associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     *
     * @see #get(Object)
     * @see #get(Object, int)
     * @see #get(Object, int, int)
     * @see #get(Object, int, int, int)
     */

    public VolatileImage[][][] getMap(Object key) {
        return this.getImageData(key).getMap();
    }

    /**
     * Returns the number of frames associated with the specified key.
     *
     * @param key The key whose number of loaded frames is to be returned
     *
     * @return The number of loaded frames associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     *
     * @see #getRows(Object)
     * @see #getColumns(Object)
     */

    public int getFrames(Object key) {
        return this.getImageData(key).getFrames();
    }

    /**
     * Returns the width of the tile map associated with the specified key.
     *
     * @param key The key whose tile map width is to be returned
     *
     * @return The width of the tile map associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     *
     * @see #getFrames(Object)
     * @see #getColumns(Object)
     */

    public int getRows(Object key) {
        return this.getImageData(key).getRows();
    }

    /**
     * Returns the height of the tile map associated with the specified key.
     *
     * @param key The key whose tile map height is to be returned
     *
     * @return The height of the tile map associated with the key
     *
     * @throws NoSuchElementException If the image has not yet been loaded yet
     *
     * @see #getFrames(Object)
     * @see #getRows(Object)
     */

    public int getColumns(Object key) {
        return this.getImageData(key).getColumns();
    }

    /**
     * Loads a copy of the image to be managed by the ImageLoader with default key.
     * The original image is used as the key.
     *
     * @param image The image to be managed by this image loader
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @see #load(String)
     * @see #load(File)
     * @see #load(Image, Object)
     * @see #load(String, Object)
     * @see #load(File, Object)
     */

    public int load(Image image) {
        return this.load(image, image);
    }

    /**
     * Loads the image from the specified path to be managed by the ImageLoader with default key.
     * The path to the image is used as the key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     *
     * @param image The path to the image to be managed by this image loader
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #load(Image)
     * @see #load(File)
     * @see #load(Image, Object)
     * @see #load(String, Object)
     * @see #load(File, Object)
     */

    public int load(String image) {
        return this.load(image, image);
    }

    /**
     * Loads the image from the specified file to be managed by the ImageLoader with default key.
     * The file pointing to the image is used as the key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     *
     * @param image File representation of the image to be managed by this image loader
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #load(Image)
     * @see #load(String)
     * @see #load(Image, Object)
     * @see #load(String, Object)
     * @see #load(File, Object)
     */

    public int load(File image) {
        return this.load(image, image);
    }

    /**
     * Loads a copy of the image to be managed by the ImageLoader with specified key.
     *
     * @param image The image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @see #load(Image)
     * @see #load(String)
     * @see #load(File)
     * @see #load(String, Object)
     * @see #load(File, Object)
     */

    public int load(Image image, Object key) {
        this.unload(key);

        ImageData t = new ImageData(toVolatileImage(image));
        int k = key.hashCode();
        this.images.put(k, t);
        return k;
    }

    /**
     * Loads the image from the specified path to be managed by the ImageLoader with specified key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     *
     * @param image The path to the image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #load(Image)
     * @see #load(String)
     * @see #load(File)
     * @see #load(Image, Object)
     * @see #load(File, Object)
     */

    public int load(String image, Object key) {
        File f = new File(image);
        return this.load(f, key);
    }

    /**
     * Loads the image from the specified file to be managed by the ImageLoader with specified key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     *
     * @param image File representation of the image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #load(Image)
     * @see #load(String)
     * @see #load(File)
     * @see #load(Image, Object)
     * @see #load(String, Object)
     */

    public int load(File image, Object key) {
        GifImage g = ImageLoader.loadGif(image, true);

        VolatileImage[] v = new VolatileImage[g.getSize()];
        for (int i = 0; i < v.length; i++) {
            v[i] = this.toVolatileImage(g.getFrame(i).getImage());
        }

        this.unload(key);

        int k = key.hashCode();
        ImageData t = new ImageData(v);
        this.images.put(k, t);

        return k;
    }

    /**
     * Loads a copy of the image to be managed by the ImageLoader as an image map with the default key.
     * The original image is used as the key.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image The image to be managed by this image loader
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @see #loadAsMap(String, int, int)
     * @see #loadAsMap(File, int, int)
     * @see #loadAsMap(Image, Object, int, int)
     * @see #loadAsMap(String, Object, int, int)
     * @see #loadAsMap(File, Object, int, int)
     */

    public int loadAsMap(Image image, int tileWidth, int tileHeight) {
        return this.loadAsMap(image, image, tileWidth, tileHeight);
    }

    /**
     * Loads the image from the specified path to be managed by the ImageLoader as an image map with the default key.
     * The path to the image is used as the key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image The path to the image to be managed by this image loader
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #loadAsMap(Image, int, int)
     * @see #loadAsMap(File, int, int)
     * @see #loadAsMap(Image, Object, int, int)
     * @see #loadAsMap(String, Object, int, int)
     * @see #loadAsMap(File, Object, int, int)
     */

    public int loadAsMap(String image, int tileWidth, int tileHeight) {
        return this.loadAsMap(image, image, tileWidth, tileHeight);
    }

    /**
     * Loads the image from the specified file to be managed by the ImageLoader as an image map with the default key.
     * The file pointing to the image is used as the key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image File representation of the image to be managed by this image loader
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #loadAsMap(Image, int, int)
     * @see #loadAsMap(String, int, int)
     * @see #loadAsMap(Image, Object, int, int)
     * @see #loadAsMap(String, Object, int, int)
     * @see #loadAsMap(File, Object, int, int)
     */

    public int loadAsMap(File image, int tileWidth, int tileHeight) {
        return this.loadAsMap(image, image, tileWidth, tileHeight);
    }

    /**
     * Loads a copy of the image to be managed by the ImageLoader as an image map with the specified key.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image The image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @see #loadAsMap(Image, int, int)
     * @see #loadAsMap(String, int, int)
     * @see #loadAsMap(File, int, int)
     * @see #loadAsMap(String, Object, int, int)
     * @see #loadAsMap(File, Object, int, int)
     */

    public int loadAsMap(Image image, Object key, int tileWidth, int tileHeight) {
        this.unload(key);

        ImageData t = new ImageData(tileWidth, tileHeight, toVolatileImage(image));
        int k = key.hashCode();
        this.images.put(k, t);
        return k;
    }

    /**
     * Loads the image from the specified path to be managed by the ImageLoader as an image map with the specified key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image The path to the image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #loadAsMap(Image, int, int)
     * @see #loadAsMap(String, int, int)
     * @see #loadAsMap(File, int, int)
     * @see #loadAsMap(Image, Object, int, int)
     * @see #loadAsMap(File, Object, int, int)
     */

    public int loadAsMap(String image, Object key, int tileWidth, int tileHeight) {
        File f = new File(image);
        return this.loadAsMap(f, key, tileWidth, tileHeight);
    }

    /**
     * Loads the image from the specified file to be managed by the ImageLoader as an image map with the specified key.
     * GIF images are loaded as multiple frames, preprocessed to appear as shown on screen.
     * The image is split into a map at regular interval with no spacing based on the tile width and tile height.
     *
     * @param image File representation of the image to be managed by this image loader
     * @param key The custom key to be used to retrieve the image
     * @param tileWidth The width of the tiles contained in the image
     * @param tileHeight The height of the tiles contained in the image
     *
     * @return The unique numerical key associated with the loaded image
     *
     * @throws RuntimeException If the specified file does not exist, cannot be read, or the file is not understood as an image
     *
     * @see #loadAsMap(Image, int, int)
     * @see #loadAsMap(String, int, int)
     * @see #loadAsMap(File, int, int)
     * @see #loadAsMap(Image, Object, int, int)
     * @see #loadAsMap(String, Object, int, int)
     */

    public int loadAsMap(File image, Object key, int tileWidth, int tileHeight) {
        GifImage g = ImageLoader.loadGif(image, true);

        VolatileImage[] v = new VolatileImage[g.getSize()];
        for (int i = 0; i < v.length; i++) {
            v[i] = this.toVolatileImage(g.getFrame(i).getImage());
        }

        this.unload(key);

        int k = key.hashCode();
        ImageData t = new ImageData(tileWidth, tileHeight, v);
        this.images.put(k, t);

        return k;
    }

    private ImageData getImageData(Object key) {
        Integer hash = key.hashCode();
        ImageData img = this.images.get(hash);
        if (img == null) throw new NoSuchElementException("Specified image has not been loaded.");
        return img;
    }

    private VolatileImage toVolatileImage(Image img) {
        if (img instanceof VolatileImage)
            return (VolatileImage)img;

        boolean a = img instanceof Transparency;
        VolatileImage v;
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        if (!a) {
            v = this.config.createCompatibleVolatileImage(w, h, Transparency.OPAQUE);
        } else {
            a = ((Transparency)img).getTransparency() != Transparency.OPAQUE;
            v = this.config.createCompatibleVolatileImage(w, h, ((Transparency)img).getTransparency());
        }

        Graphics2D g = null;
        try {
            g = v.createGraphics();

            if (a) ImageLoader.clear(g, w, h);

            g.drawImage(img, 0, 0, null);
        } finally {
            g.dispose();
        }

        return v;
    }

    private static int getAttribute(IIOMetadataNode n, String s) {
        return Integer.parseInt(n.getAttribute(s));
    }

    private static void clear(Graphics2D g, int width, int height) { // clear rect in image
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private static IIOMetadataNode getNode(Node n, String s) { // finds first node with name
        NodeList l = n.getChildNodes();
        for (int i = 0; i < l.getLength(); i++)
            if ((n = l.item(i)).getNodeName().equals(s))
                return (IIOMetadataNode)n;
        return null;
    }

    private class ImageData {
        final VolatileImage[][][] v;
        ImageData(VolatileImage... v) {
            this.v = new VolatileImage[v.length][1][1];
            for (int i = 0; i < v.length; i++)
                this.v[i][0][0] = v[i];
        }
        ImageData(int w, int h, VolatileImage... v) {
            int r = v[0].getWidth()/w;
            int c = v[0].getHeight()/h;
            this.v = new VolatileImage[v.length][r][c];
            for (int i = 0; i < v.length; i++)
                for (int j = 0; j < r; j++)
                    for (int k = 0; k < c; k++) {
                        VolatileImage t = ImageLoader.this.config.createCompatibleVolatileImage(w, h, v[i].getTransparency());
                        Graphics2D g = t.createGraphics();
                        ImageLoader.clear(g, w, h);
                        g.drawImage(v[i], -w*j, -h*k, null);
                        g.dispose();
                        this.v[i][j][k] = t;
                    }
        }
        VolatileImage get(int f, int x, int y) {
            return this.v[f][x][y];
        }
        int getFrames() {
            return this.v.length;
        }
        int getRows() {
            return this.v[0].length;
        }
        int getColumns() {
            return this.v[0][0].length;
        }
        void flush() {
            for (int i = 0; i < this.getFrames(); i++)
                for (int j = 0; j < this.getRows(); j++)
                    for (int k = 0; k < this.getColumns(); k++)
                        v[i][j][k].flush();
        }
        VolatileImage[][][] getMap() {
            return this.v;
        }
    }

    private final static int[] extension = new int[] {0x21, 0xFF, 0x0B, 0x4E, 0x45, 0x54, 0x53, 0x43, 0x41, 0x50, 0x45, 0x32, 0x2E, 0x30, 0x03, 0x01};

    public enum FrameDisposal {
        NONE, DO_NOT_DISPOSE, RESTORE_TO_BACKGROUND_COLOR, RESTORE_TO_PREVIOUS;
        private static FrameDisposal getEnum(String str) {
            switch (str) {
            case "none":                     return NONE;
            case "doNotDispose":             return DO_NOT_DISPOSE;
            case "restoreToBackgroundColor": return RESTORE_TO_BACKGROUND_COLOR;
            case "restoreToPrevious":        return RESTORE_TO_PREVIOUS;
            default:                         throw new IllegalArgumentException("No disposal method for '" + str + "'");
            }
        }
    }

    public static GifImage loadGif(File file, boolean process) {
        try (
            FileReader fileReader = new FileReader(file.getAbsolutePath());
        ) {
            fileReader.read(); // catch that file does not exist early
        } catch (IOException e) {
            throw new RuntimeException("The image specified cannot be loaded. The file either does not exist, or cannot be accessed. Path: " + file.getPath());
        }

        java.util.List<BufferedImage> images = new ArrayList<>(); // make storage containers
        java.util.List<Integer> delays = new ArrayList<>();
        java.util.List<FrameDisposal> disposals = new ArrayList<>();
        java.util.List<Integer> xs = new ArrayList<>();
        java.util.List<Integer> ys = new ArrayList<>();
        int width = 0;
        int height = 0;
        int loops = 0;

        try (
            ImageInputStream stream = ImageIO.createImageInputStream(file); // create stream
        ) {
            stream.mark(); // mark for later
            ImageReader reader = ImageIO.getImageReaders(stream).next(); // create reader
            reader.setInput(stream);

            if (reader.getNumImages(true) == 0) { // if there are no images
                throw new IOException("There are not images!");
            } else if (reader.getNumImages(true) == 1) { // if its not an animated gif
                BufferedImage image = reader.read(0);
                return new GifImage(image.getWidth(), image.getHeight(), 0, new GifFrame(image, 0, 0, 0, FrameDisposal.NONE));
            }

            images = new ArrayList<>(); // make storage containers
            delays = new ArrayList<>();
            disposals = new ArrayList<>();
            xs = new ArrayList<>();
            ys = new ArrayList<>();

            for (int i = 0; i < reader.getNumImages(true); i++) {
                IIOMetadata meta = reader.getImageMetadata(i); // get metadata
                Node root = meta.getAsTree(meta.getNativeMetadataFormatName());
                IIOMetadataNode extNode = getNode(root, "GraphicControlExtension");
                IIOMetadataNode dataNode = getNode(root, "ImageDescriptor");

                if (i == 0) { // find image size and make buffer
                    width = getAttribute(dataNode, "imageWidth");
                    height = getAttribute(dataNode, "imageHeight");
                }

                BufferedImage img = null;
                try {
                    img = reader.read(i); // there seems to be a bug that I have no control over
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("The GifReader is trying to read a gif file that is too compressed for the Java native gif ImageReader to read. This is a known bug, see: https://bugs.openjdk.java.net/browse/JDK-7132728\n");
                    e.printStackTrace();
                    System.exit(1);
                }

                images.add(img); // add image and metadata to array
                delays.add(getAttribute(extNode, "delayTime"));
                xs.add(getAttribute(dataNode, "imageLeftPosition"));
                ys.add(getAttribute(dataNode, "imageTopPosition"));
                disposals.add(FrameDisposal.getEnum(extNode.getAttribute("disposalMethod")));
            }

            try { // try to find the NETSCAPE 2.0 extension, to get the # of loops
                stream.reset();
                for (int i = 0; i < extension.length; i++) {
                    if (stream.readUnsignedByte() != extension[i]) i = -1;
                }
                loops = stream.readUnsignedByte() | (stream.readUnsignedByte() << 8); // 16 bit unsigned int, stored little-endian
                if (stream.readUnsignedByte() != 0) loops = 0;
            } catch (IOException e) {} // just kinda catch this one (it doesnt matter)
        } catch (Exception e) {
            throw new RuntimeException("The image specified cannot be loaded. The file exists, but is not understood as an image. Path: " + file.getPath());
        }

        GifFrame[] frames;

        if (process) { // merge frames that would be rendered together
            GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); // create buffer
            BufferedImage buffer = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            Graphics2D bufferGraphics = buffer.createGraphics();

            clear(bufferGraphics, width, height);

            java.util.List<BufferedImage> processedImages = new ArrayList<>(); // new frames
            java.util.List<Integer> processedDelays = new ArrayList<>();

            for (int i = 0; i < images.size(); i++) {
                BufferedImage temp = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT); // create image to draw to
                Graphics2D tempGraphics = temp.createGraphics();

                clear(tempGraphics, width, height);

                tempGraphics.drawImage(buffer, 0, 0, null); // draw image
                tempGraphics.drawImage(images.get(i), xs.get(i), ys.get(i), null);
                tempGraphics.dispose();
                images.get(i).flush(); // flush unprocessed image

                if (delays.get(i) != 0) { // if visible for a little while
                    processedImages.add(temp); // add to array
                    processedDelays.add(delays.get(i));
                }

                if (disposals.get(i) == FrameDisposal.RESTORE_TO_BACKGROUND_COLOR) { // restoration types
                    clear(bufferGraphics, width, height);
                } else if (disposals.get(i) != FrameDisposal.RESTORE_TO_PREVIOUS) {
                    bufferGraphics.drawImage(temp, 0, 0, null);
                }
            }
            bufferGraphics.dispose(); // flush buffer
            buffer.flush();

            frames = new GifFrame[processedImages.size()]; // create array to be returned
            for (int i = 0; i < frames.length; i++) { // fill array
                frames[i] = new GifFrame(processedImages.get(i), 0, 0, processedDelays.get(i), FrameDisposal.RESTORE_TO_BACKGROUND_COLOR);
            }
        } else {
            frames = new GifFrame[images.size()]; // create array to be returned
            for (int i = 0; i < frames.length; i++) { // fill array
                frames[i] = new GifFrame(images.get(i), xs.get(i), ys.get(i), delays.get(i), disposals.get(i));
            }
        }

        images.clear(); // clear arraylists
        delays.clear();
        disposals.clear();
        xs.clear();
        ys.clear();

        return new GifImage(width, height, loops, frames); // return
    }

    public static class GifImage implements Iterable<GifFrame> {
        private final ArrayList<GifFrame> frames;
        private final int width, height, loops;
        private GifImage(int width, int height, int loops, GifFrame... frames) {
            this.frames = new ArrayList<>(Arrays.asList(frames));
            this.width = width;
            this.height = height;
            this.loops = loops;
        }
        public Iterator<GifFrame> iterator() {
            return this.frames.iterator();
        }
        public int getWidth() {
            return this.width;
        }
        public int getHeight() {
            return this.height;
        }
        public int getLoops() {
            return this.loops;
        }
        public GifFrame getFrame(int index) {
            return this.frames.get(index);
        }
        public int getSize() {
            return this.frames.size();
        }
    }

    public static class GifFrame {
        private final BufferedImage frame;
        private final int x, y, delay;
        private final FrameDisposal disposal;
        private GifFrame(BufferedImage frame, int x, int y, int delay, FrameDisposal disposal) {
            this.frame = frame;
            this.x = x;
            this.y = y;
            this.delay = delay;
            this.disposal = disposal;
        }
        public BufferedImage getImage() {
            return this.frame;
        }
        public int getX() {
            return this.x;
        }
        public int getY() {
            return this.y;
        }
        public int getWidth() {
            return this.frame.getWidth();
        }
        public int getHeight() {
            return this.frame.getHeight();
        }
        public int getDelay() {
            return this.delay;
        }
        public FrameDisposal getDisposal() {
            return this.disposal;
        }
    }
}
