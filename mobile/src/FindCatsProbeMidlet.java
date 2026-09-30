import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/** Hardware acceptance probe; the full game follows after device validation. */
public final class FindCatsProbeMidlet extends MIDlet {
    private ProbeCanvas canvas;

    protected void startApp() {
        if (canvas == null) canvas = new ProbeCanvas(this);
        Display.getDisplay(this).setCurrent(canvas);
        canvas.activate(true);
    }

    protected void pauseApp() {
        if (canvas != null) canvas.activate(false);
    }

    protected void destroyApp(boolean unconditional) {
        if (canvas != null) canvas.stop();
    }

    public void exit() {
        destroyApp(true);
        notifyDestroyed();
    }
}
