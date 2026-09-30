"""Generate a tiny RGB cat icon using only the Python standard library."""
from pathlib import Path
import struct
import sys
import zlib

PIXELS = (
    "................", "................", "...oo......oo...", "...opo....opo...",
    "...opoooooopo...", "...oooooooooo...", "...oooooooooo...", "...ooXooooXoo...",
    "...ooXooooXoo...", "...oooooooooo...", "....oooPPooo....", "....oooooooo....",
    ".....oooooo.....", "....ooo..ooo....", "................", "................",
)
PALETTE = {".": (34, 56, 66), "o": (244, 174, 67), "p": (251, 212, 162), "X": (32, 42, 52), "P": (155, 73, 62)}


def chunk(kind, data):
    return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data) & 0xffffffff)


def generate(path):
    raw = bytearray()
    for row in PIXELS:
        for _ in range(2):
            raw.append(0)
            for pixel in row:
                raw.extend(PALETTE[pixel] * 2)
    data = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", 32, 32, 8, 2, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")
    Path(path).write_bytes(data)


if __name__ == "__main__":
    generate(sys.argv[1])
