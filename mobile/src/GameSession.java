/** Menu, pause and result transitions, independent of handset UI classes. */
public final class GameSession {
    public static final int MENU = 0, PLAY = 1, PAUSE = 2, HELP = 3, RESULT = 4;
    public int screen = MENU, selected = 1;
    public GameWorld world;
    public boolean sound = true, exitRequested;
    public String message = "";
    private int held, lastDirection, messageMillis, pendingEvents;
    private long seed;

    public GameSession(long initialSeed) { seed = initialSeed; }

    public void press(int direction) {
        if (screen == PLAY) {
            held |= 1 << direction;
            lastDirection = direction;
            world.setDirection(direction);
        } else if (screen != HELP && (direction == GameWorld.UP || direction == GameWorld.DOWN)) {
            int count = screen == MENU ? 7 : screen == PAUSE ? 5 : 2;
            selected = (selected + (direction == GameWorld.UP ? count - 1 : 1)) % count;
        }
    }

    public void release(int direction) {
        held &= ~(1 << direction);
        if (screen != PLAY) return;
        if ((held & (1 << lastDirection)) == 0) {
            lastDirection = GameWorld.NONE;
            for (int d = GameWorld.LEFT; d <= GameWorld.DOWN; d++) if ((held & (1 << d)) != 0) lastDirection = d;
        }
        world.setDirection(lastDirection);
    }

    public void select() {
        if (screen == MENU) {
            if (selected < 4) start(selected);
            else if (selected == 4) screen = HELP;
            else if (selected == 5) toggleSound();
            else exitRequested = true;
        } else if (screen == PAUSE) {
            if (selected == 0) resume();
            else if (selected == 1) start(world.difficulty);
            else if (selected == 2) menu();
            else if (selected == 3) toggleSound();
            else exitRequested = true;
        } else if (screen == RESULT) {
            if (selected == 0) start(world.difficulty);
            else menu();
        } else if (screen == HELP) menu();
    }

    private void start(int difficulty) {
        if (world == null) world = new GameWorld(difficulty, seed++);
        else world.restart(difficulty, seed++);
        screen = PLAY;
        selected = 0;
        clearInput();
        message = "Encuentra los 5 gatos";
        messageMillis = 1800;
        pendingEvents = 0;
    }

    private void clearInput() { held = 0; lastDirection = GameWorld.NONE; if (world != null) world.setDirection(GameWorld.NONE); }
    private void menu() { screen = MENU; selected = 1; clearInput(); }
    private void resume() { world.setPaused(false); screen = PLAY; clearInput(); }

    public void back() {
        if (screen == PLAY) hide();
        else if (screen == PAUSE) resume();
        else if (screen == HELP || screen == RESULT) menu();
    }

    /** A foreground callback never resumes a game without a deliberate user action. */
    public void hide() {
        clearInput();
        if (screen == PLAY) { world.setPaused(true); screen = PAUSE; selected = 0; }
    }

    public void toggleSound() { sound = !sound; }

    public void tick(int elapsed) {
        if (screen != PLAY) return;
        world.tick(elapsed);
        messageMillis = Math.max(0, messageMillis - Math.min(100, elapsed));
        int events = world.takeEvents();
        pendingEvents |= events;
        if ((events & GameWorld.KEY) != 0) message = "La llave ha aparecido arriba!";
        else if ((events & GameWorld.HIT) != 0) message = "Jrani te atrapo. Escapa!";
        else if ((events & GameWorld.CAT) != 0) message = "Gato rescatado!";
        else if ((events & GameWorld.STAIRS) != 0) message = world.levels[world.floor].name;
        if (events != 0) messageMillis = 1500;
        if (messageMillis == 0) message = "";
        if (world.status != GameWorld.PLAYING) { screen = RESULT; selected = 0; clearInput(); }
    }

    public int takeAudioEvents() { int value = pendingEvents; pendingEvents = 0; return value; }
}
