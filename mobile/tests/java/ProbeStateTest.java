public final class ProbeStateTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        ProbeState state = new ProbeState();
        state.resize(352, 416);
        int x = state.x;
        state.setDirection(ProbeState.RIGHT, true);
        state.tick(50);
        check(state.x == x, "Hidden probe must not advance");
        state.setVisible(true);
        state.setActive(true);
        state.setDirection(ProbeState.RIGHT, true);
        state.tick(50);
        check(state.x == x + 5, "Movement must use elapsed time");
        state.setDirection(ProbeState.RIGHT, false);
        state.tick(50);
        check(state.x == x + 5, "Key release must stop movement");

        state.setDirection(ProbeState.RIGHT, true);
        state.setVisible(false);
        state.tick(50);
        check(state.x == x + 5, "Losing visibility pauses movement");
        state.setVisible(true);
        state.tick(50);
        check(state.x == x + 5, "Visibility resume clears held keys");
        state.setDirection(ProbeState.RIGHT, true);
        state.setActive(false);
        state.setActive(true);
        state.tick(50);
        check(state.x == x + 5, "Lifecycle resume clears held keys");

        state.setDirection(ProbeState.RIGHT, true);
        state.togglePause();
        state.tick(50);
        check(state.x == x + 5, "Manual pause stops movement");
        state.setVisible(false);
        state.setVisible(true);
        check(state.isPaused(), "Foregrounding preserves manual pause");
        state.togglePause();
        state.tick(50);
        check(state.x == x + 5, "Manual resume clears held keys");

        state.setDirection(ProbeState.RIGHT, true);
        state.tick(5000);
        check(state.x == x + 15, "Long stalls cannot cause a movement jump");
        for (int i = 0; i < 1000; i++) state.tick(50);
        check(state.x == 352 - ProbeState.SPRITE_SIZE, "Right boundary clamps movement");
        state.setDirection(ProbeState.RIGHT, false);
        state.setDirection(ProbeState.LEFT, true);
        for (int i = 0; i < 1000; i++) state.tick(50);
        check(state.x == 0, "Left boundary clamps movement");
        state.setDirection(ProbeState.LEFT, false);
        state.setDirection(ProbeState.UP, true);
        for (int i = 0; i < 1000; i++) state.tick(50);
        check(state.y == state.playTop, "Sprite cannot overlap diagnostic text");
        state.setDirection(ProbeState.UP, false);
        state.setDirection(ProbeState.DOWN, true);
        for (int i = 0; i < 1000; i++) state.tick(50);
        check(state.y == 416 - ProbeState.SPRITE_SIZE, "Bottom boundary clamps movement");
        state.resize(176, 208);
        check(state.x >= 0 && state.x <= 176 - ProbeState.SPRITE_SIZE, "Resize keeps sprite visible");
        check(state.y <= 208 - ProbeState.SPRITE_SIZE, "Resize clamps vertical position");
        check(!state.isPaused(), "Active visible probe should run");
        System.out.println("PASS: " + checks + " probe state checks");
    }
}
