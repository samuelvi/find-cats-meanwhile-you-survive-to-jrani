import java.util.Random;

/** Exercise actual game rules and complete a run through every floor. */
public final class GameWorldTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        layoutFits();
        allMapsReachable();
        movementAndPause();
        difficultiesAndDamage();
        winAndRestart();
        enemyNavigates();
        System.out.println("PASS: " + checks + " game checks (including complete three-floor run)");
    }

    private static void layoutFits() {
        int[][] sizes = {{352,416,15}, {352,376,19}, {240,320,15}, {176,208,12}};
        for (int i = 0; i < sizes.length; i++) {
            GameLayout l = new GameLayout(sizes[i][0], sizes[i][1], sizes[i][2]);
            check(l.tile >= 8, "Tiles remain legible");
            check(l.left >= 0 && l.top >= l.hudHeight, "Map below HUD");
            check(l.left + LevelData.WIDTH * l.tile <= sizes[i][0], "All columns fit");
            check(l.top + LevelData.HEIGHT * l.tile <= sizes[i][1] - l.hudHeight, "All rows fit above footer");
        }
    }

    private static void allMapsReachable() {
        for (int floor = 0; floor < 3; floor++) {
            LevelData level = new LevelData(floor);
            int[] distance = level.distances(level.startX, level.startY);
            int count = 0;
            for (int y = 0; y < LevelData.HEIGHT; y++) for (int x = 0; x < LevelData.WIDTH; x++) {
                if (level.walkable(x, y)) {
                    count++;
                    check(distance[y * LevelData.WIDTH + x] >= 0, "Every open tile is reachable: " + floor + ":" + x + "," + y);
                }
            }
            check(count >= 100, "Enough room to evade Jrani");
            check(level.walkable(level.enemyX, level.enemyY), "Enemy has clear spawn");
            if (floor < 2) check(level.walkable(level.upX, level.upY), "Upstairs is reachable");
            if (floor > 0) check(level.walkable(level.downX, level.downY), "Downstairs is reachable");
            for (int seed = 0; seed < 20; seed++) {
                GameWorld world = new GameWorld(GameWorld.PRACTICE, seed);
                check(world.cats[floor].length == (floor == 2 ? 1 : 2), "Cat distribution 2/2/1");
                for (int i = 0; i < world.cats[floor].length; i++) {
                    int tile = world.cats[floor][i];
                    check(distance[tile] >= 0 && !level.special(tile % 16, tile / 16), "Cat is reachable and clear of fixtures");
                    for (int j = 0; j < i; j++) check(tile != world.cats[floor][j], "Cats never overlap");
                }
            }
        }
    }

    private static void movementAndPause() {
        GameWorld world = new GameWorld(GameWorld.PRACTICE, 1);
        world.player.place(1, 1);
        world.setDirection(GameWorld.LEFT);
        for (int i = 0; i < 20; i++) world.tick(50);
        check(world.player.x == 1 && !world.player.moving(), "Cannot pass through boundary walls");
        world.setDirection(GameWorld.RIGHT);
        world.tick(50);
        int x = world.player.pixelX();
        world.setPaused(true);
        world.tick(50);
        check(world.player.pixelX() == x, "Pause freezes movement");
        world.setPaused(false);
        for (int i = 0; i < 20; i++) world.tick(50);
        check(world.player.x == 2 && !world.player.moving(), "Resume clears held input; finish only current step");
        world.player.place(1, 1);
        world.setDirection(GameWorld.RIGHT);
        world.tick(5000);
        check(world.player.pixelX() < 60, "Long stalls cannot teleport the player");
        // Furniture is solid just like walls.
        LevelData level = world.levels[0];
        boolean checked = false;
        for (int y = 1; y < 17 && !checked; y++) for (int col = 2; col < 15 && !checked; col++) {
            if (level.tile(col, y) > LevelData.WALL && level.walkable(col - 1, y)) {
                world.player.place(col - 1, y);
                world.setDirection(GameWorld.RIGHT);
                for (int i = 0; i < 10; i++) world.tick(50);
                check(world.player.x == col - 1, "Furniture blocks movement");
                checked = true;
            }
        }
        check(checked, "Furniture collision test ran");
    }

    private static void difficultiesAndDamage() {
        int[] lives = {8, 5, 3, 0};
        for (int difficulty = 0; difficulty < 4; difficulty++) {
            GameWorld world = new GameWorld(difficulty, 3);
            check(world.lives == lives[difficulty], "Difficulty lives");
            check(world.hasEnemy() == (difficulty != GameWorld.PRACTICE), "Practice has no enemies");
            world.enemies[0].place(world.player.x, world.player.y);
            world.tick(50);
            if (difficulty == GameWorld.PRACTICE) {
                check(world.lives == 0 && world.status == GameWorld.PLAYING, "Practice never loses lives");
            } else {
                check(world.lives == lives[difficulty] - 1, "Capture loses one life");
                world.enemies[0].place(world.player.x, world.player.y);
                world.tick(50);
                check(world.lives == lives[difficulty] - 1, "Grace period prevents repeated capture");
            }
        }
        GameWorld world = new GameWorld(GameWorld.HARD, 4);
        for (int i = 0; i < 3; i++) {
            world.invulnerableMillis = 0;
            world.enemies[0].place(world.player.x, world.player.y);
            world.tick(50);
        }
        check(world.status == GameWorld.LOST && world.lives == 0, "Last capture ends game");
        world.tick(50);
        check(world.lives == 0, "Ended game cannot lose more lives");
    }

    private static void winAndRestart() {
        GameWorld world = new GameWorld(GameWorld.PRACTICE, 42);
        walkTo(world, LevelData.BASKET_X, LevelData.BASKET_Y);
        check(!world.keySpawned, "Basket cannot spawn key before all cats");
        for (int floor = 0; floor < 3; floor++) {
            for (int i = 0; i < world.cats[floor].length; i++) {
                int tile = world.cats[floor][i];
                walkTo(world, tile % 16, tile / 16);
                int collected = world.collected;
                world.tick(50);
                check(world.collected == collected, "Cat cannot be collected twice");
            }
            if (floor < 2) {
                walkTo(world, world.levels[floor].upX, world.levels[floor].upY);
                check(world.floor == floor + 1, "Upstairs advances floor");
                for (int i = 0; i < 10; i++) world.tick(50);
                check(world.floor == floor + 1, "Arrival does not bounce between floors");
            }
        }
        check(world.collected == 5 && !world.keySpawned, "All cats still require basket delivery");
        while (world.floor > 0) {
            int before = world.floor;
            walkTo(world, world.levels[before].downX, world.levels[before].downY);
            check(world.floor == before - 1, "Downstairs returns to previous floor");
        }
        walkTo(world, LevelData.BASKET_X, LevelData.BASKET_Y);
        check(world.keySpawned && world.status == GameWorld.PLAYING, "Basket spawns key");
        walkTo(world, world.levels[0].upX, world.levels[0].upY);
        world.player.place(LevelData.KEY_X, LevelData.KEY_Y);
        world.tick(50);
        check(world.status == GameWorld.PLAYING, "Key cannot be collected on another floor");
        walkTo(world, world.levels[1].downX, world.levels[1].downY);
        walkTo(world, LevelData.KEY_X, LevelData.KEY_Y);
        check(world.status == GameWorld.WON, "Complete run wins on floor-one key");
        world.restart(GameWorld.NORMAL, 43);
        check(world.floor == 0 && world.collected == 0 && !world.keySpawned, "Restart resets progress");
        check(world.status == GameWorld.PLAYING && world.lives == 5, "Restart resets difficulty and result");
    }

    private static void enemyNavigates() {
        GameWorld world = new GameWorld(GameWorld.NORMAL, 2);
        world.player.place(3, 2);
        world.enemies[0].place(10, 2);
        world.invulnerableMillis = 20000;
        int initial = distance(world.player, world.enemies[0]);
        for (int i = 0; i < 200; i++) {
            world.tick(50);
            Actor enemy = world.enemies[0];
            check(world.levels[0].walkable(enemy.x, enemy.y), "Enemy never enters obstacle");
        }
        check(distance(world.player, world.enemies[0]) < initial, "Enemy routes around room walls");
    }

    private static int distance(Actor a, Actor b) {
        return Math.abs(a.pixelX() - b.pixelX()) + Math.abs(a.pixelY() - b.pixelY());
    }

    private static void walkTo(GameWorld world, int targetX, int targetY) {
        int originalFloor = world.floor;
        int guard = 0;
        while (world.floor == originalFloor && (world.player.x != targetX || world.player.y != targetY)) {
            if (++guard > 400) throw new AssertionError("Route did not finish");
            int[] distances = world.levels[world.floor].distances(targetX, targetY);
            int x = world.player.x, y = world.player.y;
            int direction = GameWorld.NONE;
            int best = distances[y * 16 + x];
            int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
            int[] dirs = {GameWorld.LEFT, GameWorld.RIGHT, GameWorld.UP, GameWorld.DOWN};
            for (int i = 0; i < 4; i++) {
                int xx = x + dx[i], yy = y + dy[i];
                if (world.levels[world.floor].walkable(xx, yy)) {
                    int d = distances[yy * 16 + xx];
                    if (d >= 0 && d < best) { best = d; direction = dirs[i]; }
                }
            }
            check(direction != GameWorld.NONE, "A path exists to target");
            world.setDirection(direction);
            world.tick(50);
            world.setDirection(GameWorld.NONE);
            for (int i = 0; i < 10 && world.player.moving() && world.floor == originalFloor; i++) world.tick(50);
        }
        world.setDirection(GameWorld.NONE);
        world.tick(50);
    }
}
