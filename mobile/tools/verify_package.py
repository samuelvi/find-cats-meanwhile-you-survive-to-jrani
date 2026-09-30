"""Check the generated probe package. Actual CLDC verification is done by ProGuard."""
from pathlib import Path
import re
import struct
import sys
import zipfile

METADATA = {
    "MIDlet-Name": "Find Cats Probe",
    "MIDlet-Version": "0.1.0",
    "MIDlet-Vendor": "TeteSam",
    "MIDlet-1": "Find Cats Probe, /probe.png, FindCatsProbeMidlet",
    "MicroEdition-Configuration": "CLDC-1.1",
    "MicroEdition-Profile": "MIDP-2.0",
}


def attributes(data):
    """Read JAD or manifest attributes, including manifest continuation lines."""
    result = {}
    previous = None
    for line in data.decode("utf-8").splitlines():
        if line.startswith(" ") and previous:
            result[previous] += line[1:]
        elif line:
            if ": " not in line:
                raise ValueError("Invalid metadata line")
            key, value = line.split(": ", 1)
            if key in result:
                raise ValueError("Duplicate metadata: " + key)
            result[key] = value
            previous = key
    return result


class Reader:
    def __init__(self, data):
        self.data = data
        self.offset = 0

    def take(self, count):
        end = self.offset + count
        if end > len(self.data):
            raise ValueError("Truncated class file")
        result = self.data[self.offset:end]
        self.offset = end
        return result

    def u1(self):
        return self.take(1)[0]

    def u2(self):
        return struct.unpack(">H", self.take(2))[0]

    def u4(self):
        return struct.unpack(">I", self.take(4))[0]


def has_branches(bytecode):
    """Walk instructions, rather than mistaking operand bytes for jump opcodes."""
    two_bytes = {0x10, 0x12, 0x15, 0x16, 0x17, 0x18, 0x19,
                 0x36, 0x37, 0x38, 0x39, 0x3A, 0xA9, 0xBC}
    three_bytes = {0x11, 0x13, 0x14, 0x84, 0xB2, 0xB3, 0xB4,
                   0xB5, 0xB6, 0xB7, 0xB8, 0xBB, 0xBD, 0xC0, 0xC1}
    offset = 0
    while offset < len(bytecode):
        opcode = bytecode[offset]
        if 0x99 <= opcode <= 0xA8 or opcode in (0xAA, 0xAB, 0xC6, 0xC7, 0xC8, 0xC9):
            return True
        if opcode == 0xC4:  # wide load/store/ret or wide iinc
            if offset + 1 >= len(bytecode):
                raise ValueError("Truncated wide instruction")
            length = 6 if bytecode[offset + 1] == 0x84 else 4
        elif opcode in two_bytes:
            length = 2
        elif opcode in three_bytes:
            length = 3
        elif opcode == 0xB9:
            length = 5
        elif opcode == 0xC5:
            length = 4
        else:
            length = 1
        offset += length
        if offset > len(bytecode):
            raise ValueError("Truncated bytecode instruction")
    return False


def class_info(data):
    reader = Reader(data)
    if reader.u4() != 0xCAFEBABE:
        raise ValueError("Invalid class file signature")
    minor, major = reader.u2(), reader.u2()
    if (minor, major) != (0, 46):
        raise ValueError("Unsupported class version: %d.%d" % (major, minor))
    count = reader.u2()
    strings, class_indices = {}, []
    index = 1
    while index < count:
        tag = reader.u1()
        if tag == 1:
            strings[index] = reader.take(reader.u2()).decode("utf-8", errors="replace")
        elif tag == 7:
            class_indices.append(reader.u2())
        elif tag == 8:
            reader.take(2)
        elif tag in (3, 4, 9, 10, 11, 12):
            reader.take(4)
        elif tag in (5, 6):
            reader.take(8)
            index += 1
        else:
            raise ValueError("Unsupported constant pool tag: " + str(tag))
        index += 1

    references = set()
    for index in class_indices:
        name = strings[index]
        if not name.startswith("["):
            references.add(name)
    for value in strings.values():
        references.update(re.findall(r"L([a-zA-Z0-9_$/]+);", value))

    def read_attributes(source):
        values = []
        for _ in range(source.u2()):
            name = strings[source.u2()]
            values.append((name, source.take(source.u4())))
        return values

    reader.take(6)  # access flags, this class, superclass
    reader.take(reader.u2() * 2)  # interfaces
    for _ in range(reader.u2()):
        reader.take(6)
        read_attributes(reader)
    stack_maps = 0
    for _ in range(reader.u2()):
        reader.take(6)
        for name, body in read_attributes(reader):
            if name != "Code":
                continue
            code = Reader(body)
            code.take(4)  # stack and locals
            bytecode = code.take(code.u4())
            exception_count = code.u2()
            code.take(exception_count * 8)
            method_maps = 0
            for attribute, _ in read_attributes(code):
                if attribute == "StackMapTable":
                    raise ValueError("Desktop StackMapTable instead of CLDC StackMap")
                if attribute == "StackMap":
                    stack_maps += 1
                    method_maps += 1
            if (exception_count or has_branches(bytecode)) and not method_maps:
                raise ValueError("Missing CLDC StackMap in method with branches or exception handlers")
    read_attributes(reader)
    return references, stack_maps


def verify(jar):
    jar = Path(jar)
    jad = attributes(jar.with_suffix(".jad").read_bytes())
    if jad.get("MIDlet-Jar-Size") != str(jar.stat().st_size):
        raise ValueError("JAD size does not match final JAR size")
    if jad.get("MIDlet-Jar-URL") != jar.name:
        raise ValueError("JAD URL does not match JAR filename")

    with zipfile.ZipFile(jar) as archive:
        names = archive.namelist()
        if len(names) != len(set(names)):
            raise ValueError("Duplicate JAR entries")
        manifest = attributes(archive.read("META-INF/MANIFEST.MF"))
        for key, expected in METADATA.items():
            if manifest.get(key) != expected or jad.get(key) != expected:
                raise ValueError("Invalid or inconsistent " + key)
        if "FindCatsProbeMidlet.class" not in names:
            raise ValueError("Missing MIDlet entry point")
        if "probe.png" not in names or not archive.read("probe.png").startswith(b"\x89PNG\r\n\x1a\n"):
            raise ValueError("Missing or invalid probe.png")
        if any(name.startswith(("java/", "javax/")) for name in names):
            raise ValueError("Bundled platform classes are forbidden")

        allowed = {name[:-6] for name in names if name.endswith(".class")}
        for library in ("cldcapi11.jar", "midpapi20.jar"):
            with zipfile.ZipFile(Path("/opt/j2me") / library) as api:
                allowed.update(name[:-6] for name in api.namelist() if name.endswith(".class"))
        total_maps = 0
        for name in names:
            if name.endswith(".class"):
                references, maps = class_info(archive.read(name))
                unknown = references - allowed
                if unknown:
                    raise ValueError("Unknown runtime reference: " + ", ".join(sorted(unknown)))
                total_maps += maps
        if total_maps == 0:
            raise ValueError("Missing CLDC StackMap preverification attributes")
    return total_maps


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit("Usage: verify_package.py path/to/FindCatsProbe.jar")
    try:
        count = verify(sys.argv[1])
        print("PASS: MIDP 2.0 / CLDC 1.1 package; %d CLDC StackMap attributes" % count)
    except (ValueError, OSError, KeyError, zipfile.BadZipFile) as error:
        sys.exit("Package validation failed: " + str(error))
