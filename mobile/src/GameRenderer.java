import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Uses the actual Canvas size; caches only the static floor, never an 800x600 surface. */
public final class GameRenderer {
    private final Font small = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    private final Font title = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
    private static final String[] MAIN = {"Facil", "Normal", "Dificil", "Practica", "Como jugar", "Sonido", "Salir"};
    private static final String[] PAUSED = {"Reanudar", "Reiniciar", "Menu principal", "Sonido", "Salir"};
    private static final String[] RESULT = {"Jugar otra vez", "Menu principal"};
    private Image background;
    private int cachedFloor = -1, cachedTile;
    private GameLayout layout;
    private int width, height;

    public void draw(Graphics g, GameSession session, int w, int h, int animation) {
        if (width != w || height != h || layout == null) {
            width = w; height = h;
            layout = new GameLayout(w, h, small.getHeight());
            background = null;
        }
        g.setColor(0x18262e); g.fillRect(0, 0, w, h);
        g.setFont(small);
        if (session.screen == GameSession.MENU) { mainMenu(g, session); return; }
        if (session.screen == GameSession.HELP) { help(g); return; }
        game(g, session, animation);
        if (session.screen == GameSession.PAUSE) panel(g, "PAUSA", PAUSED, session);
        else if (session.screen == GameSession.RESULT)
            panel(g, session.world.status == GameWorld.WON ? "HAS ESCAPADO!" : "JRANI TE ATRAPO", RESULT, session);
    }

    private void floor(Graphics g, LevelData level) {
        int tile = layout.tile;
        if (background == null || cachedFloor != level.index || cachedTile != tile) {
            background = Image.createImage(LevelData.WIDTH * tile, LevelData.HEIGHT * tile);
            Graphics bg = background.getGraphics();
            for (int y = 0; y < LevelData.HEIGHT; y++) for (int x = 0; x < LevelData.WIDTH; x++) {
                int xx = x * tile, yy = y * tile;
                bg.setColor((x + y) % 2 == 0 ? 0xa67e56 : 0xad855b);
                bg.fillRect(xx, yy, tile, tile);
                bg.setColor(0x956e4c); bg.drawLine(xx, yy + tile - 1, xx + tile - 1, yy + tile - 1);
                if (level.tile(x, y) == LevelData.WALL) {
                    bg.setColor(0x566068); bg.fillRect(xx, yy, tile, tile);
                    bg.setColor(0xa1a7a0); bg.fillRect(xx, yy, tile, Math.max(2, tile / 5));
                    bg.setColor(0x343f47); bg.drawLine(xx, yy + tile - 1, xx + tile - 1, yy + tile - 1);
                }
            }
            for (int y = 1; y < 17; y++) for (int x = 1; x < 15; x++) {
                int type = level.tile(x, y);
                if (type <= LevelData.WALL || level.tile(x - 1, y) == type || level.tile(x, y - 1) == type) continue;
                int cols = 1, rows = 1;
                while (level.tile(x + cols, y) == type) cols++;
                while (level.tile(x, y + rows) == type) rows++;
                GameArt.furniture(bg, x * tile, y * tile, cols * tile, rows * tile, type);
            }
            // Windows set into the external wall give each floor a house-like silhouette.
            bg.setColor(0x85b4be);
            bg.fillRect(tile * 3, 2, tile * 2, Math.max(2, tile - 5));
            bg.fillRect(tile * 11, 2, tile * 2, Math.max(2, tile - 5));
            bg.setColor(0xd6e7db);
            bg.drawLine(tile * 4, 2, tile * 4, tile - 4);
            bg.drawLine(tile * 12, 2, tile * 12, tile - 4);
            if (level.index < 2) GameArt.stairs(bg, level.upX * tile, level.upY * tile, tile, true);
            if (level.index > 0) GameArt.stairs(bg, level.downX * tile, level.downY * tile, tile, false);
            if (level.index == 0) GameArt.basket(bg, LevelData.BASKET_X * tile, LevelData.BASKET_Y * tile, tile);
            cachedFloor = level.index; cachedTile = tile;
        }
        g.drawImage(background, layout.left, layout.top, Graphics.TOP | Graphics.LEFT);
    }

    private void game(Graphics g, GameSession session, int animation) {
        GameWorld world = session.world;
        floor(g, world.levels[world.floor]);
        int tile = layout.tile;
        for (int i = 0; i < world.cats[world.floor].length; i++) if (!world.taken[world.floor][i]) {
            int position = world.cats[world.floor][i];
            GameArt.cat(g, layout.left + position % 16 * tile, layout.top + position / 16 * tile, tile);
        }
        if (world.floor == 0 && world.keySpawned)
            GameArt.key(g, layout.left + LevelData.KEY_X * tile, layout.top + LevelData.KEY_Y * tile, tile);
        if (world.hasEnemy()) actor(g, world.enemies[world.floor], true, animation);
        if (world.invulnerableMillis == 0 || animation % 4 < 2) actor(g, world.player, false, animation);
        g.setFont(small);
        g.setColor(world.chasing ? 0xffbc83 : 0xe9e6d5);
        String lives = world.hasEnemy() ? Integer.toString(world.lives) : "--";
        g.drawString("P" + (world.floor + 1) + "  V" + lives + "  G" + world.collected + "/5", 5, 3, Graphics.TOP | Graphics.LEFT);
        g.setColor(0xa8c7bc);
        g.drawString(session.sound ? "S:on" : "S:off", width - 5, 3, Graphics.TOP | Graphics.RIGHT);
        String objective = world.keySpawned ? "Llave en piso 1" : world.collected == 5 ? "Vuelve al cesto del piso 1" : "Busca gatos | 0: pausa";
        if (session.message.length() > 0) objective = session.message;
        g.setColor(0xefe2b9);
        centered(g, objective, height - small.getHeight() - 3);
    }

    private void actor(Graphics g, Actor actor, boolean enemy, int animation) {
        int size = layout.tile;
        int x = layout.left + actor.pixelX() * size / LevelData.CELL - size / 2;
        int y = layout.top + actor.pixelY() * size / LevelData.CELL - size / 2;
        GameArt.person(g, x, y, size, enemy, actor.moving() ? animation % 2 : 0);
    }

    private void mainMenu(Graphics g, GameSession session) {
        g.setColor(0x223842); g.fillRect(8, 8, width - 16, height - 16);
        g.setColor(0xf1ca75); g.setFont(title); centered(g, "FIND CATS", 18);
        g.setFont(small); g.setColor(0xdbe4d8); centered(g, "Sobrevive a Jrani", 22 + title.getHeight());
        int artSize = Math.min(44, height / 10);
        GameArt.cat(g, width / 2 - artSize / 2, 30 + title.getHeight() + small.getHeight(), artSize);
        int start = 36 + title.getHeight() + small.getHeight() + artSize;
        int step = Math.min(32, (height - start - small.getHeight() - 16) / MAIN.length);
        options(g, MAIN, session, start, step);
        g.setColor(0x9eb9b0); centered(g, "2/8: elegir   5: aceptar", height - small.getHeight() - 13);
    }

    private void options(Graphics g, String[] options, GameSession session, int y, int step) {
        g.setFont(small);
        for (int i = 0; i < options.length; i++) {
            if (i == session.selected) {
                g.setColor(0xd6af69); g.fillRect(width / 8, y + i * step, width * 3 / 4, step - 2);
                g.setColor(0x203139);
            } else g.setColor(0xe0e5d5);
            String text = options[i];
            if (text.equals("Sonido")) text = session.sound ? "Sonido: SI" : "Sonido: NO";
            centered(g, text, y + i * step + Math.max(0, (step - small.getHeight() - 2) / 2));
        }
    }

    private void panel(Graphics g, String heading, String[] choices, GameSession session) {
        int step = small.getHeight() + 12;
        int boxHeight = (choices.length + 3) * step;
        int top = Math.max(10, (height - boxHeight) / 2);
        g.setColor(0x152a34); g.fillRect(8, top, width - 16, boxHeight);
        g.setColor(0xc3a16b); g.drawRect(8, top, width - 17, boxHeight);
        g.setFont(small); g.setColor(0xf6d786); centered(g, heading, top + 12);
        if (session.screen == GameSession.RESULT) {
            g.setColor(0xc9dacb);
            centered(g, session.world.status == GameWorld.WON ? "Los cinco gatos estan a salvo." : "Prueba otra ruta y vuelve a jugar.", top + 12 + step);
        }
        options(g, choices, session, top + 2 * step, step);
    }

    private void help(Graphics g) {
        g.setFont(title); g.setColor(0xf1ca75); centered(g, "COMO JUGAR", 16);
        g.setFont(small); g.setColor(0xe0e5d5);
        String[] lines = {"Encuentra 5 gatos en 3 pisos.", "Llevalos al cesto del piso 1.", "Recoge la llave para escapar.", "", "Cruceta o 2/4/6/8: moverte", "Pisa las escaleras para subir/bajar.", "Evita a Jrani y conserva tus vidas.", "0: pausa / reanudar", "#: activar o silenciar sonido", "Practica: explora sin enemigos."};
        int top = title.getHeight() + 32;
        int step = Math.min(small.getHeight() + 9, (height - top - 36) / lines.length);
        for (int i = 0; i < lines.length; i++) centered(g, lines[i], top + i * step);
        g.setColor(0xf1ca75); centered(g, "5 / 0: volver", height - small.getHeight() - 12);
    }

    private void centered(Graphics g, String text, int y) {
        g.drawString(text, width / 2, y, Graphics.TOP | Graphics.HCENTER);
    }
}
