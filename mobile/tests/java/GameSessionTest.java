public final class GameSessionTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        GameSession session = new GameSession(7);
        check(session.screen == GameSession.MENU, "Start at menu");
        session.select();
        check(session.screen == GameSession.PLAY && session.world.difficulty == GameWorld.NORMAL, "Normal selected by default");
        session.press(GameWorld.RIGHT);
        session.tick(50);
        int before = session.world.player.pixelX();
        session.hide();
        session.tick(1000);
        check(session.screen == GameSession.PAUSE && session.world.player.pixelX() == before, "Background pauses game");
        session.select();
        check(session.screen == GameSession.PLAY, "Explicit resume returns to play");
        for (int i = 0; i < 20; i++) session.tick(50);
        int resumed = session.world.player.pixelX();
        session.tick(50);
        check(resumed == session.world.player.pixelX(), "Resume does not restore held keys");
        session.back();
        session.press(GameWorld.DOWN);
        session.select();
        check(session.screen == GameSession.PLAY && session.world.collected == 0 && session.world.lives == 5, "Pause restart starts fresh");
        session.back();
        session.press(GameWorld.DOWN);
        session.press(GameWorld.DOWN);
        session.select();
        check(session.screen == GameSession.MENU, "Pause menu returns to main menu");
        session.selected = 3;
        session.select();
        check(session.world.difficulty == GameWorld.PRACTICE && !session.world.hasEnemy(), "Practice is playable from menu");
        session.world.status = GameWorld.WON;
        session.tick(50);
        check(session.screen == GameSession.RESULT, "Win shows result screen");
        session.select();
        check(session.screen == GameSession.PLAY && session.world.status == GameWorld.PLAYING, "Result supports replay");
        session.world.status = GameWorld.LOST;
        session.tick(50);
        check(session.screen == GameSession.RESULT, "Loss shows result screen");
        session.press(GameWorld.DOWN);
        session.select();
        check(session.screen == GameSession.MENU, "Result supports main menu");
        session.selected = 4;
        session.select();
        check(session.screen == GameSession.HELP, "Help opens");
        session.back();
        check(session.screen == GameSession.MENU, "Help closes");
        session.selected = 5;
        session.select();
        check(!session.sound, "Menu toggles audio");
        session.toggleSound();
        check(session.sound, "Shortcut toggles audio");
        session.selected = 6;
        session.select();
        check(session.exitRequested, "Exit has explicit action");
        System.out.println("PASS: " + checks + " menu/lifecycle checks");
    }
}
