import json
import urllib.request
import xml.etree.ElementTree as ET
import os
import re

mapping = {
    "187:16623": "ic_figma_info",
    "200:6563": "ic_figma_refresh",
    "166:12637": "ic_figma_undo",
    "114:1830": "ic_figma_delete",
    "196:21661": "ic_figma_home",
    "115:2496": "ic_figma_edit",
    "136:10393": "ic_figma_star",
    "196:21736": "ic_figma_dots",
    "114:2201": "ic_figma_settings",
    "200:6717": "ic_figma_mail",
    "211:7593": "ic_figma_glass",
    "200:5889": "ic_figma_corners",
    "185:15750": "ic_figma_color_fill",
    "187:15839": "ic_figma_stroke_color"
}

with open("figma_svg_urls.json", "r") as f:
    data = json.load(f)

images = data.get("images", {})
res_dir = "/home/mateus/AndroidStudioProjects/AureoleLauncher/app/src/main/res/drawable"

for node_id, filename in mapping.items():
    url = images.get(node_id)
    if not url:
        print(f"Skipping {node_id}, no URL")
        continue

    # Download SVG content
    req = urllib.request.urlopen(url)
    svg_data = req.read().decode('utf-8')

    # Simple SVG to Android VectorDrawable conversion
    # Extract width, height, viewBox, paths
    try:
        root = ET.fromstring(svg_data)
        width_str = root.attrib.get('width', '24')
        height_str = root.attrib.get('height', '24')

        # strip 'px' or 'dp'
        width_val = re.sub(r'[^\d.]', '', width_str) or '24'
        height_val = re.sub(r'[^\d.]', '', height_str) or '24'

        viewbox = root.attrib.get('viewBox', f'0 0 {width_val} {height_val}').split()
        if len(viewbox) == 4:
            viewport_w = viewbox[2]
            viewport_h = viewbox[3]
        else:
            viewport_w = width_val
            viewport_h = height_val

        vector_xml = f'''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{width_val}dp"
    android:height="{height_val}dp"
    android:viewportWidth="{viewport_w}"
    android:viewportHeight="{viewport_h}">
'''
        # Extract all path elements
        for elem in root.iter():
            if elem.tag.endswith('path'):
                path_d = elem.attrib.get('d')
                fill = elem.attrib.get('fill', '#FFFFFF')
                stroke = elem.attrib.get('stroke')
                stroke_width = elem.attrib.get('stroke-width', '1')

                if path_d:
                    vector_xml += f'  <path\n      android:pathData="{path_d}"\n'
                    if stroke and stroke != 'none':
                        vector_xml += f'      android:strokeColor="#FFFFFF"\n      android:strokeWidth="{stroke_width}"\n'
                    if fill and fill != 'none':
                        vector_xml += f'      android:fillColor="#FFFFFF"\n'
                    vector_xml += '/>\n'

        vector_xml += '</vector>\n'

        target_path = os.path.join(res_dir, f"{filename}.xml")
        with open(target_path, "w") as out_f:
            out_f.write(vector_xml)
        print(f"Successfully created {filename}.xml")

    except Exception as e:
        print(f"Error processing {node_id} ({filename}): {e}")
