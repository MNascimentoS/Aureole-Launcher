import os
import re

files_to_fix = [
    'app/src/test/java/dev/mnascimentos/aureole/feature/home/components/content/HomeScreenScrollContentTest.kt',
    'app/src/test/java/dev/mnascimentos/aureole/feature/home/grid/EditContainerDialogSectionsTest.kt',
    'app/src/test/java/dev/mnascimentos/aureole/feature/home/grid/EditGridItemDialogTest.kt',
    'app/src/test/java/dev/mnascimentos/aureole/feature/home/grid/ScrollViewChildEditTest.kt',
    'app/src/test/java/dev/mnascimentos/aureole/feature/settings/components/SettingsDialogComponentsTest.kt'
]

for filepath in files_to_fix:
    with open(filepath, 'r') as f:
        content = f.read()
    
    new_content = content
    
    if 'import dev.mnascimentos.aureole.R' not in new_content:
        new_content = new_content.replace('import org.junit.Test', 'import dev.mnascimentos.aureole.R\nimport org.junit.Test')
    
    # ensure ApplicationProvider is imported
    if 'import androidx.test.core.app.ApplicationProvider' not in new_content:
        new_content = new_content.replace('import org.junit.Test', 'import androidx.test.core.app.ApplicationProvider\nimport android.content.Context\nimport org.junit.Test')

    # ensure context variable exists in all tests
    lines = new_content.split('\n')
    out_lines = []
    for i, line in enumerate(lines):
        out_lines.append(line)
        if '@Test' in line:
            # check if next line is fun
            if i+1 < len(lines) and 'fun ' in lines[i+1]:
                pass # will handle next line
        elif 'fun ' in line and i > 0 and '@Test' in lines[i-1]:
            # This is a test function declaration. Let's find the {
            if '{' in line:
                out_lines.append('        val context = ApplicationProvider.getApplicationContext<Context>()')
    
    # but wait, some tests have { on the next line or already have context.
    # A regex is better.
    
    new_content = re.sub(r'(@Test\s+fun[^{]+\{\s*)(?!val context =)', r'\1val context = ApplicationProvider.getApplicationContext<Context>()\n        ', new_content)
    
    if new_content != content:
        with open(filepath, 'w') as f:
            f.write(new_content)

