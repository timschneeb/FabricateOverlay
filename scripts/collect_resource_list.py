import os
import xml.etree.ElementTree as ET

IGNORED_TAGS = {
    "declare-styleable",
    "attr",
    "style",
    "id",
    "array",
    "plurals",
    "layout"
}

IGNORED_PREFIXES = {
    "abc_",
    "androidx_",
    "m3expressive_",
    "m3_",
    "mtrl_",
}

def collect_resources(root_dir):
    """
    Walks root_dir recursively and collects (type, name) pairs.
    """
    resources = set()

    for dirpath, _, filenames in os.walk(root_dir):
        if "values.xml" not in filenames:
            continue

        xml_path = os.path.join(dirpath, "values.xml")

        try:
            tree = ET.parse(xml_path)
            root = tree.getroot()
        except ET.ParseError:
            continue

        for elem in root:
            tag = elem.tag

            if tag in IGNORED_TAGS:
                continue

            if tag == "item" and elem.attrib.get("type") in IGNORED_TAGS:
                continue
            if tag == "item":
                tag = elem.attrib.get("type")

            name = elem.attrib.get("name")
            if name and name not in IGNORED_PREFIXES:
                resources.add((tag, name))

    return sorted(resources)


def discover_top_level_dirs(base_dir):
    """
    Returns immediate subdirectories of base_dir.
    """
    return sorted(
        os.path.join(base_dir, d)
        for d in os.listdir(base_dir)
        if os.path.isdir(os.path.join(base_dir, d))
    )


def write_output(base_dir, output_file):
    top_level_dirs = discover_top_level_dirs(base_dir)

    with open(output_file, "w", encoding="utf-8") as f:
        for module_dir in top_level_dirs:
            module_name = os.path.basename(module_dir)

            resources = collect_resources(module_dir)
            if not resources:
                continue  # skip empty modules

            f.write(f"# {module_name}\n")
            for r_type, r_name in resources:
                f.write(f"{r_type},{r_name}\n")
            f.write("\n")


if __name__ == "__main__":
    BASE_DIR = "."
    OUTPUT_FILE = "android_resources.txt"

    write_output(BASE_DIR, OUTPUT_FILE)
