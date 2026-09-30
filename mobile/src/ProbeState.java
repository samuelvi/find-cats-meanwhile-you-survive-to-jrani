/** Platform-independent diagnostic movement and lifecycle state. */
public final class ProbeState {
    public static final int LEFT = 1, RIGHT = 2, UP = 4, DOWN = 8;
    public static final int SPRITE_SIZE = 16;
    public int x, y, playTop;

    private int width, height, directions;
    private int fineX, fineY;
    private boolean active, visible, manualPause;

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        playTop = height * 2 / 3;
        clamp();
    }

    public void setVisible(boolean value) {
        visible = value;
        directions = 0;
    }

    public void setActive(boolean value) {
        active = value;
        directions = 0;
    }

    public void setDirection(int direction, boolean pressed) {
        if (pressed && !isPaused()) directions |= direction;
        else directions &= ~direction;
    }

    public void togglePause() {
        manualPause = !manualPause;
        directions = 0;
    }

    public boolean isPaused() {
        return !active || !visible || manualPause;
    }

    public void tick(int elapsed) {
        if (isPaused()) return;
        // Fixed-point pixels at 100 pixels/second, with a 100 ms stall cap.
        elapsed = Math.max(0, Math.min(100, elapsed));
        int distance = elapsed * 100;
        if ((directions & LEFT) != 0) fineX -= distance;
        if ((directions & RIGHT) != 0) fineX += distance;
        if ((directions & UP) != 0) fineY -= distance;
        if ((directions & DOWN) != 0) fineY += distance;
        clamp();
    }

    private void clamp() {
        fineX = Math.max(0, Math.min(Math.max(0, width - SPRITE_SIZE) * 1000, fineX));
        fineY = Math.max(playTop * 1000, Math.min(Math.max(playTop, height - SPRITE_SIZE) * 1000, fineY));
        x = fineX / 1000;
        y = fineY / 1000;
    }
}
