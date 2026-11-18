# Lote 17/11: fase G concluída (gates)

Os 7 gates passaram em release/0.9.44: detekt (0 issues), lintDebug (0 erros, 0 avisos), testDebugUnitTest
(1995 testes, 0 falhas), jacocoTestCoverageVerification, updateDebugScreenshotTest, validateDebugScreenshotTest e assembleDebug.

Commits da fase G: ver `git log --oneline 7cb24179..HEAD`. Prints em `specs/lote-17-11-prints/<spec>/`.
Revisão visual ainda não feita (outro agente).

Decisões da fase G: polimento-ui D-15 (raiz dos Ajustes sem anel, pedido do dono).
Mudanças de projeto que valem registro: `LocalGridGestureTracker` virou `HomeScreenActions.gridGestureTracker`
(lint ComposeCompositionLocalUsage); `AureoleLogoRing` perdeu o parâmetro `size` (o tamanho vem do modifier);
`AureoleMiniHome` recebe `layout: MiniHomeFavoritesLayout`; `QuickButtonGridItem` recebe `callbacks` e `style`;
`SetBackgroundBottomSheet` removida; `home_quickbutton_icon` removida; `home_folder_all_icons_count` virou plural.
