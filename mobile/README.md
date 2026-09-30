# Nokia N80 port

## Milestone and hardware gate

This branch starts with the build pipeline and an installable device probe.
The full game port is gated on a successful installation and probe run on the
owner's Nokia N80. A successful desktop build does not establish device compatibility.

Target: Symbian S60 3rd Edition, Java ME CLDC 1.1 / MIDP 2.0, physical screen
352 x 416. The probe reports the actual Canvas dimensions, which may differ.

## Build

Run from the repository/worktree root with Docker Desktop running:

```sh
docker compose build build
docker compose run --rm build
docker compose run --rm test
```

Outputs: `dist/FindCatsProbe.jar` and `dist/FindCatsProbe.jad`.
Source, API stubs, and build tools are not installed on the host. Compose uses
`linux/amd64`, including on Apple Silicon (Docker x86 emulation must be enabled).
Only the image build downloads dependencies. Compilation and tests run offline.
One-off containers are removed by `--rm`; no service ports or persistent stack
are required. The project name is `findcats-nokia-n80-port` and `/workspace` is
mounted from the worktree. Generated `mobile/build/` and `dist/` are ignored.

Toolchain:

| Component | Version | Source |
| --- | --- | --- |
| OpenJDK 6 | Azul Zulu 6.22.0.3 / 6u119 | cdn.azul.com |
| Ant | 1.9.16 | Maven Central |
| CLDC 1.1 and MIDP 2.0 API stubs | MicroEmulator artifacts 2.0.4 | Maven Central |
| CLDC preverifier | ProGuard 5.3.3, `-microedition` | Maven Central |

The base image is pinned by digest; downloaded Java tools are pinned by version
and SHA-256 in `docker/j2me/dependencies.sha256`. Debian support packages use the
Bookworm package repositories. JDK and tools retain their upstream licenses in
the container archives/JARs; do not bundle them into the phone application.

Java is compiled with source 1.3, class version 46 (target 1.2), and **only** the
CLDC/MIDP boot classpath. ProGuard generates CLDC `StackMap` attributes without
shrinking, optimization, or obfuscation. The build fails on unresolved references.
Python 3 normalizes ZIP metadata, generates the JAD from the final JAR, and checks
package metadata, resources, class versions, runtime references, and presence of
CLDC preverification. Neither changing a class version nor a JAR extension alone
makes an application compatible with Java ME.

The classic Wireless Toolkit native preverifier is replaced with ProGuard to
avoid obsolete 32-bit binaries. No GUI emulator is bundled at this milestone;
device testing below is mandatory.

## Install and test on the N80

1. Transfer both files to the phone using a working Bluetooth file transfer,
   USB mass-storage connection, or memory-card reader. Keep their names unchanged
   and place them together when using the phone's file manager.
2. Open `FindCatsProbe.jad` to install. If the transfer method only offers the
   JAR, open `FindCatsProbe.jar` instead and report which route worked.
3. Accept the unsigned application prompt if shown. Launch **Find Cats Probe**.
4. Record the platform string, Canvas dimensions, free/total Java heap, PNG
   status, reported FPS, and whether the moving marker is visible.
5. Hold/release every direction using both the D-pad and 2/4/6/8. The marker
   should stop immediately on release and remain inside the play area.
6. Press 5 or the centre key: a short tone should play. The status is only an
   API result; confirm that you actually hear it. Test the phone's sound profile
   if playback succeeds but is inaudible.
7. Press 0 to pause/resume. Switch to another application and return. Movement
   must remain stopped until a direction is pressed again. A manually paused
   probe must stay paused across a background/foreground cycle.
8. Use the **Salir** menu command and relaunch. Repeat after an interruption.
9. Run for several minutes and record whether FPS or free memory keeps falling.

Send back: phone firmware, installation method/result, screenshot or transcribed
diagnostic lines, key results, audible tone result, pause/resume result, and any
error text. Never claim hardware acceptance from package tests alone.

## Next milestones after hardware acceptance

1. First-floor vertical slice: movement, camera, wall/furniture collisions,
   cats, one enemy, lives, and HUD.
2. Complete three-floor game: cat counts 2/2/1; Easy/Normal/Hard lives 8/5/3;
   Practice without enemies; basket and key on floor 1; win/lose/restart.
3. Export procedural Pygame sprites, tile textures, port sound effects, and
   measure performance on the phone (initial target 20 FPS).
4. Run full rule tests and hardware acceptance, including a 20-minute session.

Reference behavior is in `../main.py` and `../src/`. Preserve elapsed-time speed
when replacing the original 60 FPS loop. Spawn cats in reachable positions clear
of walls and furniture with bounded retries. Keep the key on floor 1. Original
`background.mid` and `chase.mid` assets are absent; music is not an existing asset.
