import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/** Single game thread, event-driven key states, explicit resume after interruptions. */
public final class GameCanvasView extends GameCanvas implements Runnable, CommandListener {
    private final FindCatsMidlet owner;
    private final GameSession session = new GameSession(System.currentTimeMillis());
    private final GameRenderer renderer = new GameRenderer();
    private final AudioManager audio = new AudioManager();
    private final Command pause = new Command("Pausa / volver", Command.SCREEN, 1);
    private final Command exit = new Command("Salir", Command.EXIT, 2);
    private volatile boolean running = true, active;
    private boolean started;

    public GameCanvasView(FindCatsMidlet midlet) {
        super(false);
        owner = midlet;
        setFullScreenMode(true);
        addCommand(pause); addCommand(exit); setCommandListener(this);
    }

    public void activate(boolean value) {
        synchronized (session) {
            active = value;
            if (!value) session.hide();
            if (value && !started) { started = true; new Thread(this).start(); }
        }
    }

    public void stop() { running = false; activate(false); }
    protected void hideNotify() { synchronized (session) { session.hide(); } }

    private int action(int code) {
        try { return getGameAction(code); }
        catch (IllegalArgumentException unknownKey) { return 0; }
    }

    private int direction(int code) {
        if (code == KEY_NUM4) return GameWorld.LEFT;
        if (code == KEY_NUM6) return GameWorld.RIGHT;
        if (code == KEY_NUM2) return GameWorld.UP;
        if (code == KEY_NUM8) return GameWorld.DOWN;
        switch (action(code)) {
            case LEFT: return GameWorld.LEFT;
            case RIGHT: return GameWorld.RIGHT;
            case UP: return GameWorld.UP;
            case DOWN: return GameWorld.DOWN;
            default: return GameWorld.NONE;
        }
    }

    protected void keyPressed(int code) {
        synchronized (session) {
            if (code == KEY_NUM0) session.back();
            else if (code == KEY_POUND) session.toggleSound();
            else if (code == KEY_NUM5 || action(code) == FIRE) session.select();
            else {
                int d = direction(code);
                if (d != GameWorld.NONE) session.press(d);
            }
        }
    }

    protected void keyRepeated(int code) {
        synchronized (session) {
            int d = direction(code);
            if (session.screen != GameSession.PLAY && (d == GameWorld.UP || d == GameWorld.DOWN)) session.press(d);
        }
    }

    protected void keyReleased(int code) {
        synchronized (session) {
            int d = direction(code);
            if (d != GameWorld.NONE) session.release(d);
        }
    }

    public void commandAction(Command command, Displayable display) {
        if (command == exit) owner.exit();
        else if (command == pause) synchronized (session) { session.back(); }
    }

    public void run() {
        Graphics g = getGraphics();
        long previous = System.currentTimeMillis();
        int animation = 0;
        while (running) {
            long now = System.currentTimeMillis();
            int elapsed = (int) Math.max(0L, Math.min(100L, now - previous));
            previous = now;
            boolean quitting, enabled;
            int events;
            synchronized (session) {
                if (active && isShown()) session.tick(elapsed);
                quitting = session.exitRequested;
                enabled = session.sound && active && isShown();
                events = session.takeAudioEvents();
                if (active && isShown()) renderer.draw(g, session, getWidth(), getHeight(), animation++);
            }
            if (quitting) { owner.exit(); break; }
            if (active && isShown()) flushGraphics();
            audio.play(events, enabled);
            long wait = (active && isShown() ? 50L : 250L) - (System.currentTimeMillis() - now);
            if (wait > 0) {
                try { Thread.sleep(wait); }
                catch (InterruptedException stopped) { running = false; }
            }
        }
    }
}
