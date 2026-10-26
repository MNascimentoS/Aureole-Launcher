import os
import re

replacements = {
    '"Componente"': "context.getString(R.string.home_grid_scroll_component)",
    '"Componentes Filhos (0)"': "context.getString(R.string.home_container_edit_child_components, 0)",
    '"Remover Componente"': "context.getString(R.string.home_grid_remove_component)",
    '"Personalizar Relógio"': "context.getString(R.string.home_grid_customize_clock)",
    '"Tamanho atual: 3 x 4 células"': "context.getString(R.string.home_grid_current_size, 3, 4)",
    '"Adicionar Componente"': "context.getString(R.string.home_grid_add_component)",
    '"Cancelar"': "context.getString(R.string.core_common_cancel)",
    '"Posição do Botão de Configurações"': "context.getString(R.string.settings_dialogs_settings_button_position_title)",
    '"Posição do Ícone de Busca"': "context.getString(R.string.settings_dialogs_search_icon_position_title)"
}

for root, dirs, files in os.walk('app/src/test/java'):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements.items():
                new_content = new_content.replace(old, new)
            
            if new_content != content:
                # Add context import and var if needed
                if "val context =" not in new_content and "ApplicationProvider.getApplicationContext" not in new_content:
                    if "import androidx.test.core.app.ApplicationProvider" not in new_content:
                        new_content = new_content.replace("import org.junit.Test", "import androidx.test.core.app.ApplicationProvider\nimport android.content.Context\nimport org.junit.Test")
                    
                    new_content = re.sub(r'(@Test\s+fun[^{]+\{)', r'\1\n        val context = ApplicationProvider.getApplicationContext<Context>()', new_content)
                
                with open(filepath, 'w') as f:
                    f.write(new_content)

