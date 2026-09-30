"""Normalize the preverified archive and derive its descriptor from final bytes."""
from pathlib import Path
import sys
import zipfile

from verify_package import metadata_for, attributes


def package(jar):
    jar = Path(jar)
    expected_metadata = metadata_for(jar)
    with zipfile.ZipFile(jar) as source:
        files = {name: source.read(name) for name in source.namelist() if not name.endswith("/")}
    manifest = attributes(files["META-INF/MANIFEST.MF"])
    for key, expected in expected_metadata.items():
        if manifest.get(key) != expected:
            raise ValueError("Invalid manifest " + key)
    temporary = jar.with_suffix(".jar.tmp")
    try:
        with zipfile.ZipFile(temporary, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as target:
            for name in sorted(files):
                entry = zipfile.ZipInfo(name, (1980, 1, 1, 0, 0, 0))
                entry.compress_type = zipfile.ZIP_DEFLATED
                entry.create_system = 3
                entry.external_attr = 0o100644 << 16
                target.writestr(entry, files[name], compresslevel=9)
        temporary.replace(jar)
    finally:
        temporary.unlink(missing_ok=True)
    metadata = {key: manifest[key] for key in expected_metadata}
    metadata["MIDlet-Jar-URL"] = jar.name
    metadata["MIDlet-Jar-Size"] = str(jar.stat().st_size)
    jar.with_suffix(".jad").write_bytes(
        ("\r\n".join(key + ": " + value for key, value in metadata.items()) + "\r\n").encode("utf-8")
    )
    print("Packaged %s (%s bytes)" % (jar.name, jar.stat().st_size))


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit("Usage: package_midlet.py path/to/FindCats.jar (or FindCatsProbe.jar)")
    package(sys.argv[1])
