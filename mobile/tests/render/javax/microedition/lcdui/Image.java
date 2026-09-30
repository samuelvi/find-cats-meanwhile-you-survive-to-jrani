package javax.microedition.lcdui;

/** Test-only raster surface used to run the production renderer headlessly. */
public final class Image {
    final java.awt.image.BufferedImage image;

    private Image(int width, int height) { image = new java.awt.image.BufferedImage(width, height, 2); }
    public static Image createImage(int width, int height) { return new Image(width, height); }
    public Graphics getGraphics() { return new Graphics(image); }
    public void save(java.io.File path) throws java.io.IOException { javax.imageio.ImageIO.write(image, "png", path); }
}
