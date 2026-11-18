# teste-aparelho-17-11: feito

Fases 1 (implementação, f694f933) e 2 (gates e prints).

## Gates (fase 2)
| # | Gate | Resultado |
|---|---|---|
| 1 | detekt --auto-correct | passou (0 issues, após correções) |
| 2 | lintDebug | **pulado a pedido** do dono (uma execução anterior apontou ComposableNaming em `ManageItemsEditorPreview`, já corrigido no código; não reverificado) |
| 3 | testDebugUnitTest | passou (2029 testes) |
| 4 | jacocoTestCoverageVerification | passou (branches de lógica 76,15%) |
| 5 | updateDebugScreenshotTest | passou (182 referências alteradas) |
| 6 | validateDebugScreenshotTest | passou |
| 7 | assembleDebug | passou |

## Correções da fase 2
- detekt: `OverlaySheetHeader` (extraído `headerPadding`), `EditContainerScreen` (LongMethod/LongParameterList: `EditContainerSheetActions`, helpers), `ManageItemsEditorPreview` (sem return Boolean; `showsItemPreview`), constantes no preview do pager, formatação.
- Teste `HomeScreenFolderOverlaysTest`: título da sheet da pasta agora é o nome da pasta (D-01).

## Pendente
- Verificação no aparelho: RF-12 (rotação) e RF-11 (pager em paisagem com widgets reais).
- RF-13: conferir recuo nos prints. Prints em `specs/lote-17-11-prints/teste-aparelho/`.
