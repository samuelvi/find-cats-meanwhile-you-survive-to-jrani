# Find Cats and Survive Jrani

A three-floor cat-rescue game for desktop and **Nokia N80**.

## Nokia N80

Build and validate the Java ME game with Docker:

```sh
docker compose build build
docker compose run --rm build
docker compose run --rm test
```

Install **`dist/FindCats.jar`** with its **`dist/FindCats.jad`** descriptor.
Launch **Find Cats** on the phone. `FindCatsProbe` is only the diagnostic app.

The game has three portrait-first maps, five cats, Jrani, furniture, floor
transitions, a basket/key escape objective, four difficulties, pause and replay.
Use the D-pad or 2/4/6/8 to move, centre/5 to select, 0 to pause, and # for sound.

See [mobile/README.md](mobile/README.md) for installation, rules, architecture,
toolchain details, visual previews and device acceptance checks.

## Desktop reference

The original Python/Pygame version lives in `main.py`, `src/` and `assets/`.
The phone version uses Java ME CLDC 1.1 / MIDP 2.0 and requires no Python on the
device.
