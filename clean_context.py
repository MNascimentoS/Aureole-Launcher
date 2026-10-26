import os
import re

for root, dirs, files in os.walk('app/src/test/java'):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            
            # replace two or more consecutive context declarations with one
            new_content = re.sub(
                r'(val context = ApplicationProvider\.getApplicationContext<Context>\(\)\n\s*){2,}',
                r'val context = ApplicationProvider.getApplicationContext<Context>()\n        ',
                content
            )
            
            if new_content != content:
                with open(filepath, 'w') as f:
                    f.write(new_content)

