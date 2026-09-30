/** Tile-aligned movement with fixed-point interpolation; no floating point required. */
public final class Actor {
    public int x, y;
    private int dx, dy, progress;

    public void place(int column, int row) { x = column; y = row; dx = 0; dy = 0; progress = 0; }
    public boolean moving() { return dx != 0 || dy != 0; }
    public void start(int horizontal, int vertical) { dx = horizontal; dy = vertical; progress = 0; }

    public void advance(int millis, int pixelsPerSecond) {
        if (!moving()) return;
        progress += millis * pixelsPerSecond;
        if (progress >= LevelData.CELL * 1000) place(x + dx, y + dy);
    }

    public int pixelX() { return x * LevelData.CELL + LevelData.CELL / 2 + dx * progress / 1000; }
    public int pixelY() { return y * LevelData.CELL + LevelData.CELL / 2 + dy * progress / 1000; }
}
