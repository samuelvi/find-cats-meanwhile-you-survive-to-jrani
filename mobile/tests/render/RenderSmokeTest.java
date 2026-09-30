import java.io.File;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Executes the production GameRenderer, with test-only Java2D primitive adapters. */
public final class RenderSmokeTest {
    public static void main(String[] args) throws Exception {
        File folder = new File(args[0]);
        int[] heights = {416, 376};
        for (int i = 0; i < heights.length; i++) {
            int h = heights[i];
            GameRenderer renderer = new GameRenderer();
            GameSession session = new GameSession(17);
            render(folder, "menu-" + h, renderer, session, h);
            session.selected = 4; session.select();
            render(folder, "help-" + h, renderer, session, h);
            session.back(); session.select();
            for (int floor = 0; floor < 3; floor++) {
                session.world.floor = floor;
                session.world.player.place(2, 14);
                session.message = "";
                render(folder, "floor" + (floor + 1) + "-" + h, renderer, session, h);
            }
            session.back();
            render(folder, "pause-" + h, renderer, session, h);
            session.select();
            session.world.status = GameWorld.WON; session.tick(50);
            render(folder, "win-" + h, renderer, session, h);
            session.world.status = GameWorld.LOST;
            render(folder, "lose-" + h, renderer, session, h);
        }
        System.out.println("PASS: 16 render snapshots; " + Graphics.textChecks + " text bounds checks");
    }

    private static void render(File folder, String name, GameRenderer renderer, GameSession session, int height) throws Exception {
        Image target = Image.createImage(352, height);
        renderer.draw(target.getGraphics(), session, 352, height, 0);
        target.save(new File(folder, name + ".png"));
    }
}
