import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

/** Exercises actual MIDP graphics, keys, lifecycle and tone APIs on the phone. */
public final class ProbeCanvas extends GameCanvas implements Runnable, CommandListener {
    private final FindCatsProbeMidlet owner;
    private final ProbeState state = new ProbeState();
    private final Command exit = new Command("Salir", Command.EXIT, 1);
    private final Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    private final String platform = System.getProperty("microedition.platform");
    private final String[] lines = new String[11];
    private Image image;
    private String pngStatus = "OK";
    private String audioStatus = "pulsa 5";
    private String fatal;
    private int lastKey, frames, fps, drawMillis;
    private int lifecycleResumes;
    private boolean toneRequested, toneHeld;
    private volatile boolean running = true;
    private boolean started;

    public ProbeCanvas(FindCatsProbeMidlet midlet) {
        super(false);
        owner = midlet;
        setFullScreenMode(true);
        addCommand(exit);
        setCommandListener(this);
        try {
            image = Image.createImage("/probe.png");
        } catch (IOException error) {
            pngStatus = "ERROR PNG";
        }
        synchronized (state) {
            state.resize(getWidth(), getHeight());
            refreshLines();
        }
    }

    public void activate(boolean active) {
        synchronized (state) {
            state.setActive(active);
            toneRequested = false;
            toneHeld = false;
            if (active) lifecycleResumes++;
            if (!started && active) {
                started = true;
                new Thread(this).start();
            }
        }
    }

    public void stop() {
        running = false;
        activate(false);
    }

    protected void showNotify() {
        synchronized (state) {
            state.setVisible(true);
            toneHeld = false;
        }
    }

    protected void hideNotify() {
        synchronized (state) {
            state.setVisible(false);
            toneRequested = false;
            toneHeld = false;
        }
    }

    protected void sizeChanged(int width, int height) {
        // Some implementations invoke this callback during Canvas construction.
        if (state != null) synchronized (state) { state.resize(width, height); }
    }

    protected void keyPressed(int code) {
        synchronized (state) {
            lastKey = code;
            if (code == KEY_NUM0) state.togglePause();
            else if (isFire(code)) {
                if (!toneHeld && !state.isPaused()) toneRequested = true;
                toneHeld = true;
            } else state.setDirection(direction(code), true);
        }
    }

    protected void keyReleased(int code) {
        synchronized (state) {
            if (isFire(code)) toneHeld = false;
            state.setDirection(direction(code), false);
        }
    }

    private int gameAction(int code) {
        try { return getGameAction(code); }
        catch (IllegalArgumentException unsupportedKey) { return 0; }
    }

    private boolean isFire(int code) {
        return code == KEY_NUM5 || gameAction(code) == FIRE;
    }

    private int direction(int code) {
        if (code == KEY_NUM4) return ProbeState.LEFT;
        if (code == KEY_NUM6) return ProbeState.RIGHT;
        if (code == KEY_NUM2) return ProbeState.UP;
        if (code == KEY_NUM8) return ProbeState.DOWN;
        switch (gameAction(code)) {
            case LEFT: return ProbeState.LEFT;
            case RIGHT: return ProbeState.RIGHT;
            case UP: return ProbeState.UP;
            case DOWN: return ProbeState.DOWN;
            default: return 0;
        }
    }

    private String playTone() {
        try {
            Manager.playTone(76, 200, 60);
            return "API OK (escucha)";
        } catch (MediaException error) {
            return "ERROR multimedia";
        } catch (SecurityException error) {
            return "ERROR permiso";
        } catch (IllegalArgumentException error) {
            return "ERROR parametros";
        }
    }

    public void run() {
        long previous = System.currentTimeMillis();
        long sample = previous;
        Graphics graphics = getGraphics();
        try {
            while (running) {
                long now = System.currentTimeMillis();
                int elapsed = (int) Math.max(0L, Math.min(100L, now - previous));
                previous = now;
                boolean sound;
                synchronized (state) {
                    state.tick(elapsed);
                    sound = toneRequested && !state.isPaused();
                    toneRequested = false;
                }
                if (sound) {
                    String result = playTone();
                    synchronized (state) { audioStatus = result; }
                }
                if (isShown()) {
                    synchronized (state) {
                        if (now - sample >= 1000) {
                            fps = (int) (frames * 1000L / (now - sample));
                            frames = 0;
                            sample = now;
                            refreshLines();
                        }
                        long beforeDraw = System.currentTimeMillis();
                        draw(graphics);
                        flushGraphics();
                        drawMillis = (int) (System.currentTimeMillis() - beforeDraw);
                        frames++;
                    }
                } else {
                    frames = 0;
                    sample = now;
                }
                long remaining = (isShown() ? 50L : 250L) - (System.currentTimeMillis() - now);
                if (remaining > 0) Thread.sleep(remaining);
            }
        } catch (InterruptedException stopped) {
            running = false;
        } catch (RuntimeException error) {
            synchronized (state) {
                fatal = "ERROR: " + error.toString();
                draw(graphics);
                flushGraphics();
            }
            running = false;
        }
    }

    private void refreshLines() {
        Runtime runtime = Runtime.getRuntime();
        lines[0] = "Find Cats - prueba N80 0.1.0";
        lines[1] = platform == null ? "Plataforma: desconocida" : platform;
        lines[2] = "Canvas: " + getWidth() + " x " + getHeight();
        lines[3] = "Heap KB: " + runtime.freeMemory() / 1024 + "/" + runtime.totalMemory() / 1024;
        lines[4] = "PNG: " + pngStatus;
        lines[5] = "FPS: " + fps + " dibujo ms: " + drawMillis;
        lines[6] = "Tecla: " + lastKey + " inicios: " + lifecycleResumes;
        lines[7] = "Sonido: " + audioStatus;
        lines[8] = "Mover: cruceta / 2 4 6 8";
        lines[9] = "5: tono | 0: pausa";
        lines[10] = "Menu > Salir";
    }

    private void draw(Graphics graphics) {
        graphics.setColor(0x17212b);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setFont(font);
        graphics.setColor(0xffffff);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null) graphics.drawString(lines[i], 4, i * font.getHeight(), Graphics.TOP | Graphics.LEFT);
        }
        graphics.setColor(0x41556a);
        graphics.drawLine(0, state.playTop - 1, getWidth(), state.playTop - 1);
        if (image != null) graphics.drawImage(image, getWidth() - image.getWidth(), state.playTop, Graphics.TOP | Graphics.LEFT);
        graphics.setColor(0xff9933);
        graphics.fillRect(state.x, state.y, ProbeState.SPRITE_SIZE, ProbeState.SPRITE_SIZE);
        graphics.setColor(0xffffff);
        if (state.isPaused()) graphics.drawString("PAUSA", getWidth() / 2, getHeight() - font.getHeight(), Graphics.TOP | Graphics.HCENTER);
        if (fatal != null) graphics.drawString(fatal, 0, state.playTop, Graphics.TOP | Graphics.LEFT);
    }

    public void commandAction(Command command, Displayable display) {
        if (command == exit) owner.exit();
    }
}
