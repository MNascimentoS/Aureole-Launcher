import os

renames = {
    'dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemBottomSheet': 'dev.mnascimentos.aureole.composable.appitem.AppItemBottomSheet',
    'dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemPopup': 'dev.mnascimentos.aureole.composable.appitem.AppItemPopup',
    'dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppPopupHorizontalActionsRow': 'dev.mnascimentos.aureole.composable.appitem.AppPopupHorizontalActionsRow',
    'dev.mnascimentos.aureole.feature.home.components.clock.ClockHeaderContent': 'dev.mnascimentos.aureole.composable.clock.ClockHeaderContent',
    'dev.mnascimentos.aureole.feature.home.components.widgets.AddWidgetButton': 'dev.mnascimentos.aureole.composable.widgets.AddWidgetButton'
}

for root, dirs, files in os.walk('app/src/test/java'):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            new_content = content
            for old, new in renames.items():
                new_content = new_content.replace(old, new)
            if new_content != content:
                with open(filepath, 'w') as f:
                    f.write(new_content)

