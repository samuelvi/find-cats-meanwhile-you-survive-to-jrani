"""Negative tests against the actual generated MIDlet, not a mock JAR."""
import importlib.util
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]


class PackageTest(unittest.TestCase):
    jar_name = "FindCatsProbe.jar"
    entry_point = "FindCatsProbeMidlet.class"
    logic_class = "ProbeState.class"
    icon = "probe.png"

    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.jar = Path(self.temp.name) / self.jar_name
        self.jad = self.jar.with_suffix(".jad")
        for target in (self.jar, self.jad):
            shutil.copyfile(ROOT.parent / "dist" / target.name, target)
        spec = importlib.util.spec_from_file_location("verify_package", ROOT / "tools/verify_package.py")
        self.verifier = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(self.verifier)

    def rewrite(self, transform):
        with zipfile.ZipFile(self.jar) as source:
            files = {name: source.read(name) for name in source.namelist()}
        transform(files)
        with zipfile.ZipFile(self.jar, "w", zipfile.ZIP_DEFLATED) as target:
            for name, content in files.items():
                target.writestr(name, content)
        lines = self.jad.read_text().splitlines()
        self.jad.write_text("\n".join(
            "MIDlet-Jar-Size: " + str(self.jar.stat().st_size)
            if line.startswith("MIDlet-Jar-Size:") else line for line in lines
        ) + "\n")

    def test_generated_package_is_valid(self):
        self.verifier.verify(self.jar)

    def test_incorrect_jar_size_is_rejected(self):
        text = self.jad.read_text()
        self.jad.write_text(text.replace(str(self.jar.stat().st_size), "1"))
        with self.assertRaisesRegex(ValueError, "size"):
            self.verifier.verify(self.jar)

    def test_wrong_profile_is_rejected(self):
        self.jad.write_text(self.jad.read_text().replace("MIDP-2.0", "MIDP-3.0"))
        with self.assertRaisesRegex(ValueError, "MicroEdition-Profile"):
            self.verifier.verify(self.jar)

    def test_missing_entry_point_is_rejected(self):
        self.rewrite(lambda files: files.pop(self.entry_point))
        with self.assertRaisesRegex(ValueError, "entry point"):
            self.verifier.verify(self.jar)

    def test_missing_png_is_rejected(self):
        self.rewrite(lambda files: files.pop(self.icon))
        with self.assertRaisesRegex(ValueError, self.icon):
            self.verifier.verify(self.jar)

    def test_desktop_class_version_is_rejected(self):
        def newer_bytecode(files):
            name = self.entry_point
            value = bytearray(files[name])
            value[6:8] = (52).to_bytes(2, "big")
            files[name] = bytes(value)
        self.rewrite(newer_bytecode)
        with self.assertRaisesRegex(ValueError, "version"):
            self.verifier.verify(self.jar)

    def test_unpreverified_classes_are_rejected(self):
        def remove_preverification(files):
            for name in files:
                if name.endswith(".class"):
                    files[name] = files[name].replace(b"StackMap", b"NotAMap!")
        self.rewrite(remove_preverification)
        with self.assertRaisesRegex(ValueError, "StackMap"):
            self.verifier.verify(self.jar)

    def test_bundled_platform_classes_are_rejected(self):
        self.rewrite(lambda files: files.update({"java/lang/Object.class": b"bad"}))
        with self.assertRaisesRegex(ValueError, "platform"):
            self.verifier.verify(self.jar)

    def test_partially_unpreverified_package_is_rejected(self):
        def remove_one_class_maps(files):
            files[self.logic_class] = files[self.logic_class].replace(b"StackMap", b"NotAMap!")
        self.rewrite(remove_one_class_maps)
        with self.assertRaisesRegex(ValueError, "StackMap"):
            self.verifier.verify(self.jar)

    def test_unknown_runtime_reference_is_rejected(self):
        def replace_reference(files):
            for name in files:
                if name.endswith(".class"):
                    files[name] = files[name].replace(b"java/lang/String", b"java/lang/Absent")
        self.rewrite(replace_reference)
        with self.assertRaisesRegex(ValueError, "reference"):
            self.verifier.verify(self.jar)

    def test_packaging_is_byte_for_byte_repeatable(self):
        before = self.jar.read_bytes(), self.jad.read_bytes()
        subprocess.run(["python3", str(ROOT / "tools/package_midlet.py"), str(self.jar)], check=True)
        self.assertEqual(before, (self.jar.read_bytes(), self.jad.read_bytes()))

    def test_compiler_rejects_desktop_api(self):
        source = Path(self.temp.name) / "DesktopLeak.java"
        source.write_text("class DesktopLeak { java.io.File forbidden; }")
        result = subprocess.run([
            "javac", "-source", "1.3", "-target", "1.2", "-bootclasspath",
            "/opt/j2me/cldcapi11.jar:/opt/j2me/midpapi20.jar", str(source)
        ], capture_output=True, text=True)
        self.assertNotEqual(0, result.returncode)
        self.assertIn("java.io", result.stderr)
        self.assertIn("File", result.stderr)


class GamePackageTest(PackageTest):
    jar_name = "FindCats.jar"
    entry_point = "FindCatsMidlet.class"
    logic_class = "GameWorld.class"
    icon = "icon.png"

    def test_game_package_contains_game_instead_of_probe(self):
        with zipfile.ZipFile(self.jar) as archive:
            self.assertIn("GameWorld.class", archive.namelist())
            self.assertIn("GameCanvasView.class", archive.namelist())
            self.assertNotIn("FindCatsProbeMidlet.class", archive.namelist())
            self.assertNotIn("ProbeCanvas.class", archive.namelist())


if __name__ == "__main__":
    unittest.main()
