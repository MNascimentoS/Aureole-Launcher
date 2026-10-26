import os
import re

filepath = 'app/src/test/java/dev/mnascimentos/aureole/feature/home/grid/ScrollViewChildEditTest.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_content = content.replace('"Component"', 'context.getString(R.string.home_grid_scroll_component)')

if new_content != content:
    with open(filepath, 'w') as f:
        f.write(new_content)

