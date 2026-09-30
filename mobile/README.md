# Find Cats for Nokia N80

The playable Java ME game is **`dist/FindCats.jar` + `dist/FindCats.jad`**.
`FindCatsProbe.jar` is the separate hardware diagnostic, not the game.
The owner confirmed the diagnostic works on their N80; the new game still needs
its own final device play-through and performance check.

## Build and test

Run from the repository root with Docker Desktop running:

```sh
docker compose build build
docker compose run --rm build
docker compose run --rm test
```

Build produces both the playable game and the diagnostic. Tests run game rules,
menu/lifecycle transitions, layout, real renderer snapshots, and negative package
checks. A complete simulated Practice run collects all cats on all three floors,
returns to the basket, obtains the key, wins, and restarts.

The renderer tests execute the production `GameRenderer` through **test-only
Java2D adapters**, saving images to `dist/preview/` at 352x416 and 352x376. They
check text bounds, but are not a MIDP emulator or a substitute for device tests.
Java2D, test adapters, build tools and platform classes are never shipped in the
phone JAR. To regenerate only the visual previews:

```sh
docker compose run --rm test test-render
```

## Install the game

1. Transfer `dist/FindCats.jar` and `dist/FindCats.jad` together by Bluetooth,
   USB mass storage or memory card. Preserve their filenames.
2. Open `FindCats.jad` in the phone's file manager. If the transfer method only
   offers the JAR, open `FindCats.jar` instead.
3. Accept the unsigned application prompt if shown.
4. Launch **Find Cats**, not **Find Cats Probe**.
5. Choose a difficulty with the D-pad or 2/8, then press centre/5.

No network connection or external resource folder is needed by the game.

## Controls and rules

| Control | Action |
| --- | --- |
| D-pad or 2/4/6/8 | Move; up/down selects menu items |
| Centre or 5 | Confirm a menu selection |
| 0 | Pause/resume; return from help/results |
| # | Toggle sound |
| `Pausa / volver` command | Pause/back, via handset softkey menu |
| `Salir` command | Exit |

Movement is tile-aligned, smoothly interpolated. Releasing a direction finishes
the current tile, then stops; turns happen at tile centres. Interruption or
backgrounding clears held input and opens Pause. Returning to the foreground
does not automatically resume. There is no save-game persistence between launches.

Collect **five cats**, distributed **2/2/1** across three floors. Step onto the
blue/green stairs to change floors. Once all cats are collected, return to the
basket near the bottom-left of floor 1. The golden key appears near the top-left
of floor 1; collect it to win. Jrani follows reachable routes around furniture.

| Difficulty | Lives | Enemy speed |
| --- | --- | --- |
| Easy | 8 | 30 logical pixels/sec |
| Normal | 5 | 42 logical pixels/sec |
| Hard | 3 | 56 logical pixels/sec |
| Practice | Unlimited (`--`) | No enemy |

Player speed is 80 logical pixels/sec. Capture resets player/enemy positions on
the current floor and grants two seconds of invulnerability. Stair arrivals
grant 1.5 seconds. Cat progress survives both captures and floor changes.
The pause and result screens support replay and return to the main menu.

## Portrait-first scenarios

The three maps are redesigned, not compressed copies of the desktop's 800x600
surface. Each is a **16-column x 18-row grid** with different rooms, furniture
and passages: the house, lounge and attic. All open cells are connected.

At 352x416 with a small system font, the map uses 20-pixel cells (320x360),
centred between a one-line HUD and a one-line objective. The renderer reads the
actual Canvas dimensions and font height and reduces tile size if the OS leaves
a smaller viewport. No camera scrolling or runtime bitmap scaling is needed.
Sprites are drawn at the chosen tile size; the static floor is cached once per
floor/size change. No desktop-sized textures or per-frame image allocation.

Stairs sit in dead ends so ordinary hallway traversal cannot unexpectedly change
floors. Arrivals are adjacent to the stairs, preventing immediate bounce-back.
Cats are sampled without replacement from reachable empty cells, excluding
stairs, arrivals, the basket, key and spawn positions. There are no unbounded
random placement loops. The key exists only on floor 1.

## Architecture

- `LevelData`: compact map geometry and allocation-free BFS navigation.
- `Actor`: tile movement and fixed-point visual interpolation.
- `GameWorld`: collisions, cats, enemy AI, difficulties and win/lose rules.
- `GameSession`: menus, held input, pause/resume and game-result transitions.
- `GameLayout`, `GameRenderer`, `GameArt`: viewport fitting, floor cache and art.
- `GameCanvasView`, `FindCatsMidlet`: hardware input, 20 FPS loop and lifecycle.
- `AudioManager`: short MIDP tones; audio failures do not interrupt play.

The existing Python/Pygame game remains available as a desktop reference. Its
missing background/chase MIDI tracks are not required. Mobile sound effects
use built-in tones and can be disabled at any time.

## Toolchain

| Component | Version | Source |
| --- | --- | --- |
| OpenJDK 6 | Azul Zulu 6.22.0.3 / 6u119 | cdn.azul.com |
| Ant | 1.9.16 | Maven Central |
| CLDC 1.1 / MIDP 2.0 stubs | MicroEmulator artifacts 2.0.4 | Maven Central |
| CLDC preverifier | ProGuard 5.3.3, `-microedition` | Maven Central |

Compile uses source 1.3, class version 46 (target 1.2), and only the CLDC/MIDP
boot classpath. ProGuard preverifies against those APIs; unresolved references
fail the build. The final JAR is normalized before JAD size generation. Package
checks reject desktop APIs/bytecode, missing resources, metadata mismatches and
missing CLDC StackMap attributes on methods with control flow/exception handlers.

The Debian base image is pinned by digest; Java tool downloads have pinned
versions and SHA-256 hashes in `docker/j2me/dependencies.sha256`. Debian support
packages use Bookworm repositories. Upstream tools retain their upstream licenses
in the container; they are not redistributed inside the game.

Compose uses `linux/amd64` (Docker x86 emulation on Apple Silicon), project name
`findcats-nokia-n80-game`, and a bind mount of the current root/worktree at
`/workspace`. Only image construction accesses the network. Build/test containers
are offline, expose no ports and are removed with `--rm`. No persistent stack is
needed. `mobile/build/` and `dist/` are generated and ignored by Git.

## Device acceptance

On the actual N80, verify all map borders, HUD and footer are visible; movement,
stairs and menus work with D-pad and numeric keys; all four difficulties start;
Practice can be completed; normal play can lose and restart; 0, backgrounding,
resuming, sound toggle and exit behave correctly. Run a 20-minute session to
check responsiveness and stability. The 20 FPS cap is a target, not a measured
device-performance claim. Report firmware, error text and a photo if a viewport
or installation problem remains.
