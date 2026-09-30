import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

/** Short, nonessential tones; a failed audio device never interrupts a game. */
public final class AudioManager {
    public void play(int events, boolean enabled) {
        if (!enabled || events == 0) return;
        int note = 76, duration = 90;
        if ((events & GameWorld.WIN) != 0) { note = 88; duration = 450; }
        else if ((events & GameWorld.LOSE) != 0) { note = 40; duration = 350; }
        else if ((events & GameWorld.HIT) != 0) { note = 46; duration = 180; }
        else if ((events & GameWorld.KEY) != 0) { note = 84; duration = 250; }
        else if ((events & GameWorld.STAIRS) != 0) { note = 64; duration = 70; }
        try { Manager.playTone(note, duration, 55); }
        catch (MediaException ignored) { }
        catch (SecurityException ignored) { }
        catch (IllegalArgumentException ignored) { }
    }
}
