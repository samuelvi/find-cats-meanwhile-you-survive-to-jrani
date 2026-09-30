import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/** The playable game entry point, separate from the hardware diagnostic MIDlet. */
public final class FindCatsMidlet extends MIDlet {
    private GameCanvasView canvas;

    protected void startApp() {
        if (canvas == null) canvas = new GameCanvasView(this);
        Display.getDisplay(this).setCurrent(canvas);
        canvas.activate(true);
    }

    protected void pauseApp() { if (canvas != null) canvas.activate(false); }
    protected void destroyApp(boolean unconditional) { if (canvas != null) canvas.stop(); }
    public void exit() { destroyApp(true); notifyDestroyed(); }
}
