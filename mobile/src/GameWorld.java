import java.util.Random;

/** Game rules only. Rendering, MIDlet lifecycle, audio and physical keys live outside. */
public final class GameWorld {
    public static final int EASY = 0, NORMAL = 1, HARD = 2, PRACTICE = 3;
    public static final int NONE = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4;
    public static final int PLAYING = 0, WON = 1, LOST = 2;
    public static final int CAT = 1, HIT = 2, KEY = 4, WIN = 8, STAIRS = 16, LOSE = 32;
    private static final int[] DX = {0, -1, 1, 0, 0}, DY = {0, 0, 0, -1, 1};
    private static final int[] LIVES = {8, 5, 3, 0}, SPEED = {30, 42, 56, 0};
    public final LevelData[] levels = {new LevelData(0), new LevelData(1), new LevelData(2)};
    public final Actor player = new Actor();
    public final Actor[] enemies = {new Actor(), new Actor(), new Actor()};
    public final int[][] cats = {new int[2], new int[2], new int[1]};
    public final boolean[][] taken = {new boolean[2], new boolean[2], new boolean[1]};
    public int floor, collected, lives, status, difficulty, invulnerableMillis;
    public boolean keySpawned, paused, chasing;
    private int direction, events;
    private final int[] path = new int[LevelData.WIDTH * LevelData.HEIGHT];

    public GameWorld(int mode, long seed) { restart(mode, seed); }

    public void restart(int mode, long seed) {
        if (mode < EASY || mode > PRACTICE) throw new IllegalArgumentException("difficulty");
        difficulty = mode;
        lives = LIVES[mode];
        floor = 0; collected = 0; status = PLAYING; direction = NONE; events = 0;
        keySpawned = false; paused = false; chasing = false; invulnerableMillis = 0;
        player.place(levels[0].startX, levels[0].startY);
        Random random = new Random(seed);
        int[] candidates = new int[path.length];
        for (int f = 0; f < 3; f++) {
            LevelData level = levels[f];
            enemies[f].place(level.enemyX, level.enemyY);
            level.fillDistances(level.startX, level.startY, path);
            int count = 0;
            for (int i = 0; i < path.length; i++)
                if (path[i] >= 0 && !level.special(i % LevelData.WIDTH, i / LevelData.WIDTH)) candidates[count++] = i;
            if (count < cats[f].length) throw new IllegalStateException("No reachable cat positions");
            for (int i = 0; i < cats[f].length; i++) {
                int selected = (random.nextInt() & 0x7fffffff) % count;
                cats[f][i] = candidates[selected];
                candidates[selected] = candidates[--count];
                taken[f][i] = false;
            }
        }
    }

    public boolean hasEnemy() { return difficulty != PRACTICE; }
    public void setDirection(int value) { direction = paused ? NONE : value; }
    public void setPaused(boolean value) { paused = value; direction = NONE; }
    public int takeEvents() { int result = events; events = 0; return result; }

    public void tick(int elapsed) {
        if (paused || status != PLAYING) return;
        int millis = Math.max(0, Math.min(100, elapsed));
        if (millis == 0) return;
        invulnerableMillis = Math.max(0, invulnerableMillis - millis);
        LevelData level = levels[floor];
        if (!player.moving() && direction != NONE
                && level.walkable(player.x + DX[direction], player.y + DY[direction]))
            player.start(DX[direction], DY[direction]);
        player.advance(millis, 80);
        if (hasEnemy()) {
            Actor enemy = enemies[floor];
            if (!enemy.moving()) {
                level.fillDistances(player.x, player.y, path);
                int best = path[enemy.y * LevelData.WIDTH + enemy.x];
                chasing = best >= 0 && best <= 18;
                if (chasing && best > 0) {
                    int chosen = NONE;
                    for (int d = LEFT; d <= DOWN; d++) {
                        int x = enemy.x + DX[d], y = enemy.y + DY[d];
                        if (level.walkable(x, y)) {
                            int distance = path[y * LevelData.WIDTH + x];
                            if (distance >= 0 && distance < best) { best = distance; chosen = d; }
                        }
                    }
                    if (chosen != NONE) enemy.start(DX[chosen], DY[chosen]);
                }
            }
            enemy.advance(millis, SPEED[difficulty]);
            if (invulnerableMillis == 0 && Math.abs(enemy.pixelX() - player.pixelX()) < 13
                    && Math.abs(enemy.pixelY() - player.pixelY()) < 13) {
                lives--;
                events |= HIT;
                if (lives == 0) { status = LOST; events |= LOSE; return; }
                player.place(level.startX, level.startY);
                enemy.place(level.enemyX, level.enemyY);
                direction = NONE;
                invulnerableMillis = 2000;
                return;
            }
        }
        if (player.moving()) return;
        for (int i = 0; i < cats[floor].length; i++) {
            if (!taken[floor][i] && cats[floor][i] == player.y * LevelData.WIDTH + player.x) {
                taken[floor][i] = true; collected++; events |= CAT;
            }
        }
        if (floor == 0 && collected == 5 && !keySpawned
                && player.x == LevelData.BASKET_X && player.y == LevelData.BASKET_Y) {
            keySpawned = true; events |= KEY;
        }
        if (floor == 0 && keySpawned && player.x == LevelData.KEY_X && player.y == LevelData.KEY_Y) {
            status = WON; events |= WIN; return;
        }
        if (floor < 2 && player.x == level.upX && player.y == level.upY) changeFloor(floor + 1, false);
        else if (floor > 0 && player.x == level.downX && player.y == level.downY) changeFloor(floor - 1, true);
    }

    private void changeFloor(int next, boolean arrivingFromAbove) {
        floor = next;
        LevelData level = levels[next];
        if (arrivingFromAbove) player.place(level.upX - 1, level.upY);
        else player.place(level.downX + 1, level.downY);
        direction = NONE;
        invulnerableMillis = 1500;
        chasing = false;
        events |= STAIRS;
    }
}
