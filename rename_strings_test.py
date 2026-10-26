import os
import re
import glob

# Read all kotlin files
kt_files = []
for root, dirs, files in os.walk('app/src/test/java'):
    for file in files:
        if file.endswith('.kt'):
            kt_files.append(os.path.join(root, file))

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Define all renames
    renames = {
        'common_cancel': 'core_common_cancel',
        'common_save': 'core_common_save',
        'common_delete': 'core_common_delete',
        'common_confirm': 'core_common_confirm',
        'common_create': 'core_common_create',
        'common_understood': 'core_common_understood',
        'clock_': 'home_clock_',
        'folder_': 'home_folder_',
        'apps_in_folder': 'home_folder_apps_in_folder',
        'selected_apps_count': 'home_folder_selected_apps_count',
        'no_apps_in_folder': 'home_folder_no_apps_in_folder',
        'no_apps_found': 'core_common_no_apps_found',
        'other_apps_count': 'home_folder_other_apps_count',
        'all_apps_added': 'home_folder_all_apps_added',
        'search_app_placeholder': 'core_common_search_app_placeholder',
        'app_search_placeholder': 'home_apps_search_placeholder',
        'qb_': 'home_quickbutton_',
        'colors_recommended': 'settings_appearance_colors_recommended',
        'settings_title': 'settings_root_title',
        'settings_general': 'settings_root_general',
        'customize_': 'home_customize_',
        'update_': 'settings_advanced_update_',
        'release_notes_': 'settings_advanced_release_notes_',
        'app_menu_': 'home_app_menu_',
        'favorites_': 'home_favorites_',
        'widget_': 'home_widget_',
        'edit_container_': 'home_container_edit_',
        'add_container_': 'home_container_add_',
        'color_picker_': 'settings_appearance_color_picker_',
        'grid_widget_': 'home_grid_widget_'
    }

    new_content = content
    for old, new in renames.items():
        if old.endswith('_'):
            new_content = re.sub(r'R\.string\.' + old + r'([a-zA-Z0-9_]+)', r'R.string.' + new + r'\1', new_content)
        else:
            new_content = re.sub(r'R\.string\.' + old + r'\b', 'R.string.' + new, new_content)
    
    if new_content != content:
        with open(filepath, 'w') as f:
            f.write(new_content)

for f in kt_files:
    process_file(f)
