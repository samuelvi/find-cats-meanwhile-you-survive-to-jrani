package javax.microedition.lcdui;

/** Only adapts drawing primitives; this is not a MIDP emulator. */
public final class Graphics {
    public static final int HCENTER = 1, VCENTER = 2, LEFT = 4, RIGHT = 8, TOP = 16, BOTTOM = 32;
    private final java.awt.Graphics2D g;
    private final int width, height;
    private Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
    public static int textChecks;

    Graphics(java.awt.image.BufferedImage image) {
        width = image.getWidth(); height = image.getHeight();
        g = image.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void setColor(int rgb) { g.setColor(new java.awt.Color(rgb)); }
    public void setFont(Font value) { font = value; g.setFont(value.awt); }
    public void fillRect(int x, int y, int w, int h) { g.fillRect(x,y,w,h); }
    public void drawRect(int x, int y, int w, int h) { g.drawRect(x,y,w,h); }
    public void drawLine(int x, int y, int xx, int yy) { g.drawLine(x,y,xx,yy); }
    public void fillArc(int x, int y, int w, int h, int start, int arc) { g.fillArc(x,y,w,h,start,arc); }

    public void drawImage(Image image, int x, int y, int anchor) {
        if ((anchor & HCENTER) != 0) x -= image.image.getWidth() / 2;
        if ((anchor & RIGHT) != 0) x -= image.image.getWidth();
        if ((anchor & VCENTER) != 0) y -= image.image.getHeight() / 2;
        if ((anchor & BOTTOM) != 0) y -= image.image.getHeight();
        g.drawImage(image.image, x, y, null);
    }

    public void drawString(String value, int x, int y, int anchor) {
        int size = font.metrics.stringWidth(value);
        if ((anchor & HCENTER) != 0) x -= size / 2;
        if ((anchor & RIGHT) != 0) x -= size;
        if (x < 0 || x + size > width || y < 0 || y + font.getHeight() > height)
            throw new AssertionError("Clipped text on " + width + "x" + height + ": " + value);
        textChecks++;
        g.setFont(font.awt);
        g.drawString(value, x, y + font.metrics.getAscent());
    }
}
