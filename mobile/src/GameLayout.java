/** Fits the entire floor between one HUD line and one objective line. */
public final class GameLayout {
    public final int tile, left, top, hudHeight;

    public GameLayout(int width, int height, int fontHeight) {
        hudHeight = fontHeight + 6;
        tile = Math.max(1, Math.min(width / LevelData.WIDTH, (height - 2 * hudHeight) / LevelData.HEIGHT));
        left = (width - LevelData.WIDTH * tile) / 2;
        top = (height - LevelData.HEIGHT * tile) / 2;
    }
}
