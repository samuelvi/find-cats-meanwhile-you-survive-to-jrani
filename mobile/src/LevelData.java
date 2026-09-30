/** Three connected, portrait-first floor plans. All geometry uses a 16 x 18 grid. */
public final class LevelData {
    public static final int WIDTH = 16, HEIGHT = 18, CELL = 20;
    public static final int FLOOR = 0, WALL = 1, BED = 2, SOFA = 3, TABLE = 4, DRAWER = 5, PLANT = 6;
    public static final int BASKET_X = 2, BASKET_Y = 15, KEY_X = 3, KEY_Y = 1;
    public final int index;
    public final int startX = 2, startY = 14;
    public final int upX = 14, upY = 2, downX = 1, downY = 15;
    public int enemyX = 12, enemyY = 6;
    public final String name;
    private final byte[] tiles = new byte[WIDTH * HEIGHT];
    private final int[] queue = new int[WIDTH * HEIGHT];
    private static final int[] DX = {-1, 1, 0, 0}, DY = {0, 0, -1, 1};

    public LevelData(int floor) {
        index = floor;
        name = floor == 0 ? "La casa" : floor == 1 ? "El salon" : "El desvan";
        rect(0, 0, WIDTH, 1, WALL);
        rect(0, HEIGHT - 1, WIDTH, 1, WALL);
        rect(0, 0, 1, HEIGHT, WALL);
        rect(WIDTH - 1, 0, 1, HEIGHT, WALL);
        if (floor == 0) {
            rect(7, 1, 1, 7, WALL); rect(7, 4, 1, 2, FLOOR);
            rect(1, 9, 14, 1, WALL); rect(3, 9, 2, 1, FLOOR); rect(11, 9, 2, 1, FLOOR);
            rect(9, 10, 1, 7, WALL); rect(9, 13, 1, 2, FLOOR);
            rect(2, 3, 2, 2, BED); rect(5, 2, 1, 1, DRAWER);
            rect(10, 4, 2, 2, TABLE); rect(11, 11, 3, 1, SOFA);
            rect(4, 11, 2, 2, TABLE); rect(1, 6, 1, 1, PLANT);
        } else if (floor == 1) {
            rect(5, 1, 1, 6, WALL); rect(5, 3, 1, 2, FLOOR);
            rect(1, 8, 14, 1, WALL); rect(6, 8, 2, 1, FLOOR); rect(12, 8, 2, 1, FLOOR);
            rect(10, 9, 1, 8, WALL); rect(10, 13, 1, 2, FLOOR);
            rect(2, 2, 2, 1, SOFA); rect(8, 3, 2, 2, TABLE);
            rect(2, 11, 1, 2, DRAWER); rect(12, 11, 2, 3, BED);
            rect(7, 12, 1, 1, PLANT); rect(2, 5, 1, 1, PLANT);
        } else {
            rect(8, 1, 1, 7, WALL); rect(8, 4, 1, 2, FLOOR);
            rect(1, 10, 14, 1, WALL); rect(4, 10, 2, 1, FLOOR); rect(11, 10, 2, 1, FLOOR);
            rect(3, 3, 2, 3, BED); rect(11, 3, 2, 1, DRAWER);
            rect(7, 13, 2, 2, TABLE); rect(11, 13, 3, 1, SOFA);
            rect(2, 8, 1, 1, PLANT); rect(13, 7, 1, 1, PLANT);
            enemyY = 7;
        }
        // Stairs are dead ends: crossing a hallway cannot accidentally change floors.
        if (floor < 2) { rect(14, 1, 1, 1, WALL); rect(14, 3, 1, 1, WALL); }
        if (floor > 0) { rect(1, 14, 1, 1, WALL); rect(1, 16, 1, 1, WALL); }
    }

    private void rect(int x, int y, int width, int height, int value) {
        for (int yy = y; yy < y + height; yy++) for (int xx = x; xx < x + width; xx++)
            tiles[yy * WIDTH + xx] = (byte) value;
    }

    public int tile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return WALL;
        return tiles[y * WIDTH + x];
    }

    public boolean walkable(int x, int y) { return tile(x, y) == FLOOR; }

    public boolean special(int x, int y) {
        return (x == startX && y == startY) || (x == enemyX && y == enemyY)
            || (index < 2 && y == upY && x >= upX - 1)
            || (index > 0 && y == downY && x <= downX + 1)
            || (index == 0 && ((x == BASKET_X && y == BASKET_Y) || (x == KEY_X && y == KEY_Y)));
    }

    public int[] distances(int x, int y) {
        int[] result = new int[WIDTH * HEIGHT];
        fillDistances(x, y, result);
        return result;
    }

    /** Reuses the queue and caller-owned distance buffer during enemy navigation. */
    public void fillDistances(int x, int y, int[] result) {
        for (int i = 0; i < result.length; i++) result[i] = -1;
        if (!walkable(x, y)) return;
        int head = 0, tail = 0;
        queue[tail++] = y * WIDTH + x;
        result[y * WIDTH + x] = 0;
        while (head < tail) {
            int cell = queue[head++];
            for (int i = 0; i < 4; i++) {
                int xx = cell % WIDTH + DX[i], yy = cell / WIDTH + DY[i];
                if (walkable(xx, yy)) {
                    int next = yy * WIDTH + xx;
                    if (result[next] < 0) { result[next] = result[cell] + 1; queue[tail++] = next; }
                }
            }
        }
    }
}
