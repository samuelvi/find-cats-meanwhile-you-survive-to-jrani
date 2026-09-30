package javax.microedition.lcdui;

/** Test-only Java2D adapter. Never included in the phone JAR. */
public final class Font {
    public static final int FACE_SYSTEM = 0, STYLE_PLAIN = 0, STYLE_BOLD = 1, SIZE_SMALL = 8, SIZE_LARGE = 16;
    final java.awt.Font awt;
    final java.awt.FontMetrics metrics;

    private Font(int style, int size) {
        awt = new java.awt.Font("DejaVu Sans", style, size == SIZE_SMALL ? 14 : 24);
        java.awt.Graphics g = new java.awt.image.BufferedImage(1, 1, 2).getGraphics();
        metrics = g.getFontMetrics(awt);
        g.dispose();
    }

    public static Font getFont(int face, int style, int size) { return new Font(style, size); }
    public int getHeight() { return metrics.getHeight(); }
}
