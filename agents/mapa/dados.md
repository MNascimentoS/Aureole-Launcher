# Mapa: dados

Caminhos relativos a `app/src/main/java/dev/mnascimentos/aureole/` (`M/`). Não há DataStore: tudo é `SharedPreferences` (`MODE_PRIVATE`) mais Room.

## Repositórios (`core/data/repository/`)
| Arquivo | Guarda / faz |
|---|---|
| AppRepository | Apps instalados (Room `app_info`, DAO e `IconCache` injetados pelo Koin; defaults só para testes); `appsFlow`, `syncApps`, `syncPackage`, `launchApp` |
| ContainerRepository | Containers e itens (apps, pastas, quick buttons) no Room |
| FavoriteContainerRepository | Favoritos por container no Room |
| FolderRepository | Pastas no Room; migra o JSON legado das prefs |
| GridRepository | Layout da grade em JSON por orientação (prefs) |
| ClockConfigRepository | Config de relógio por item (JSON por `itemId`); `configs` é StateFlow |
| SettingsRepository | ~50 configurações (`var` com getter/setter), wallpaper custom, reset de fábrica, `updateSecretBestScore` |
| WidgetRepository | IDs de widgets, altura da linha, dots por stack |
| OnboardingPreferences | Progresso do onboarding (recebe `prefsProvider`; mesmo arquivo das configurações) |
| ReleaseNotesRepository | `object` com notas de versão (`ReleaseNotesData*.kt`) |
| WallpaperUndoStash, LockScreenWallpaper, SystemWallpaper | Desfazer de wallpaper, wallpaper da tela de bloqueio, interface sobre `WallpaperManager` |
- Auxiliares: `AppsListMigration`, `ClockConfigMigration`, `SettingsRepositoryMigrations`, `ContainerItemMapper`.
- Fluxo de preferência: o ViewModel lê o repositório e põe no UiState (não há Flow direto da pref).

## Room (`core/data/db/`)
- `AppDatabase`: version 4, `exportSchema = true`, arquivo `aureole_app_database.db`, `RoomConverters`. Schemas em `app/schemas/` (só `3.json` e `4.json`).
- Entities: `AppInfoEntity`, `FavoriteContainerEntity`, `ContainerFavoriteItemEntity` (em db/); `ContainerEntity`, `ContainerItemEntity`, `FolderEntity`, `FolderItemEntity` (em `core/data/model/`).
- Tabelas: `app_info`, `containers`, `container_items`, `favorite_containers`, `container_favorite_items`, `folders`, `folder_items`.
- DAOs: `AppInfoDao`, `ContainerDao`, `FavoriteContainerDao`, `FolderDao`.
| Migration (`AppDatabaseMigrations.kt`) | Muda |
|---|---|
| 2→3 | `container_items` ganha `quick_button_id` e `quick_button_json` |
| 3→4 | `folders` ganha `icon_type` (padrão 'ICON') e `emoji` |
- Sem 1→2: versões abaixo de 2 caem em `fallbackToDestructiveMigration(dropAllTables = true)`.

## Preferências (constantes `KEY_*` no `companion object` de cada repositório)
| Arquivo de prefs | Dono | Chaves (resumo) |
|---|---|---|
| aureole_settings_prefs | SettingsRepository | left_handed_mode, container_*, clock_* (style = chave de `ClockLayout`, message, greeting, alignment, font_family, time/date_format, text_color, text_color_ref, background_color, background_color_ref), show_folder_labels, favorite_apps (CSV), home_opens_all_apps, widget_row_enabled, wallpaper (is_custom_wallpaper_set, custom_wallpaper_path, wallpaper_scale_type, wallpaper_adjustments JSON, apply_to_lock_screen, lock_screen_notice_seen), tema (use_wallpaper_colors, manual_seed_color, selected_theme, selected_font), haze_enabled/opacity/blur_level, corner_radius_dp, use_themed_app_icons, disable_alphabet_scrubber, swipe_down_notifications, swipe_up_all_apps (padrão true; deslizar para baixo abre as notificações), settings_button_position, search_icon_position, show_*_in_all_apps, in_app_update_enabled, header_offset_percent, secret_game_best_score, secret_game_played (padrão: recorde > 0), secret_game_sound_on (padrão true), last_seen_version_code |
| aureole_settings_prefs | OnboardingPreferences | onboarding_stage, onboarding_completed_steps/tasks, onboarding_skipped_tasks (CSV), onboarding_checklist_collapsed |
| aureole_settings_prefs | migrações | show_all_apps_on_home, show_widget_dots, vivid_background_migrated |
| aureole_grid_prefs | GridRepository | grid_items_json (legado), grid_items_portrait_json, grid_items_landscape_json |
| aureole_prefs | WidgetRepository | top_widget_ids_list, widget_ids_stack_<id>, widget_row_height, widget_dots_stack_<key> |
| aureole_clock_prefs | ClockConfigRepository | clock_config_<itemId> (JSON; `style` = chave de `ClockLayout`, `message`; estilo antigo lido por `withMigratedLayout`), migrated_from_globals |
| aureole_folders_prefs | FolderRepository | folders_list (legado, removido após migrar) |
- `performFactoryReset` limpa os 5 arquivos de prefs e chama `clearAllTables()`. Fora das prefs: `filesDir/wallpaper_blur.key`.

## Koin (`di/AppModule.kt`, módulo único `appModule`)
| Tipo | O que registra |
|---|---|
| single | `AppDatabase`, os 4 DAOs e `IconCache`; App, Widget, Settings, Folder, FavoriteContainer, Container, Grid, ClockConfig repositories; `DefaultTelemetryLogger` (bind `TelemetryLogger`), `SpkiPinValidator`, `ApiClient` |
| factory | `HomeRepositories` (agrupa os 8 repositórios), `UndoController` |
| viewModel | `HomeViewModel`, `SettingsViewModel`, `WidgetPickerViewModel` |
- `AureoleApplication.onCreate` chama `startKoin` (se `GlobalContext` nulo), configura Crashlytics (coleta off em DEBUG) e Timber: `DebugTree` em DEBUG e `util/WarnLogTree` (só warning e error no logcat) em release.

## Lifecycle observers (`core/lifecycle/`)
| Arquivo | O que faz | Registrado em |
|---|---|---|
| MainLifecycleObserver | onStart: orientação; onResume: recarrega settings/apps/grid, onboarding, release notes; onPause: `onHomeHidden` | MainActivity |
| UpdateLifecycleObserver | Listener de install state, checa update in-app no onResume | MainActivity |
| WidgetLifecycleObserver | `appWidgetHost.startListening/stopListening` | MainActivity |
| SettingsLifecycleObserver | onResume: `checkDefaultLauncher` e `loadSettings` | SettingsActivity |
- `OrientationChange.kt` (`OrientationSync`) não é observer. `SecretGameOverlay.kt` registra outro observer.

## Outros
| Onde | O que |
|---|---|
| core/data/receiver/PackageReceiver | Receiver (manifest) de PACKAGE_ADDED/REMOVED/CHANGED/REPLACED; chama `syncPackage`/`removePackage` |
| core/data/cache/IconCache | LruCache de 100 + PNG em `cacheDir`; criado pelo `AppRepository` |
| core/image/ | `BlurredWallpaperCache`, `StackBlur`, `WallpaperPreviewBlur`, `PreviewBlurMath`, `WallpaperRenderer` |
| core/network/ | `ApiClient` (TLS pinning), `SpkiPinValidator`, `model/TlsPinConfig.kt` |
| core/telemetry/TelemetryLogger | Interface + `DefaultTelemetryLogger` |
| core/search/SearchRanking | `normalizeForSearch`, `rankByQuery` |
| core/coroutines/LatestJobRunner | Mantém só o job mais recente |
| core/undo/UndoController | Ver `componentes.md` |
