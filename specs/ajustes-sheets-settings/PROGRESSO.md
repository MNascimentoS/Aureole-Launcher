# Progresso: ajustes de sheets e Ajustes

Atualizado ao fim de cada fase. "Feita" marca o commit da fase; Gates e Visual só fecham na fase 4 (gates rodam uma
vez, no fim). Aparelho é o teste do Mateus.

| # | Fase | Modelo | Estimativa | Feita | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|---|
| 1 | Polimento visual: Personalizar, cabeçalho, Ajustes, Avançado, coach, ícones Add New e Home | Sonnet | 1,5–2,5 h | [x] | [ ] | [ ] | [ ] |
| 2 | Salvar ao fechar + Desfazer nas sheets de edição | Sonnet | 2–3,5 h | [ ] | [ ] | [ ] | [ ] |
| 3 | Movimento: sheet sobe e estica, checklist → pílula, lista pela borda ou por baixo | Opus | 2,5–4 h | [ ] | [ ] | [ ] | [ ] |
| 4 | Gates, correções e revisão visual | Sonnet + Opus | 1–2 h | [ ] | [ ] | [ ] | [ ] |

## Notas por fase

(cada fase escreve aqui: o que mudou, testes da fase, o que ficou faltando)

### Fase 1: polimento visual

- Personalizar: `Spacer(large)` no topo de `CustomizeAddSection`; tile Configurações com `AureoleIcons.Logo` (largura `iconHuge`, 128:73).
- Cabeçalho das sheets: `titleLarge` bold e `top = small` (todas, D-01).
- Ajustes raiz: coluna com `heightIn(min = maxHeight)` e `Spacer(weight(1f))`, "Muito obrigado!" a `xLarge` do fundo; paisagem em dois painéis (D-02) via `settingsLayoutFor` (`SettingsLayout.kt`). Avançado, Aparência e Comportamento centralizados com `settingsContentMaxWidth = 480.dp`.
- Avançado: nova ordem, links em `bodyMedium` com alvo de 48 dp; strings "What's new"/"Novidades" e "Default launcher"/"Launcher padrão".
- Coach: texto "Press back to close" / "Toque em voltar para fechar"; mão da borda com `edgeScrubPoint` + `EdgeScrubPath` (pull 56 dp, pausa, "L" em dois segmentos, loop 2300 ms); `HAND_EDGE_INSET` removido.
- Ícone `ic_aureole_add_new` e `AureoleIcons.AddNew` só em `ContainerAddFolderButton` (D-06).
- Testes (passam): `SettingsLayoutTest`, `EdgeScrubPointTest`, `SettingsAdvancedScreenTest` (ordem), `SettingsRootScreenTest` (retrato e paisagem), `CustomizeBottomSheetTest`, `GestureCoachComponentTest`. Screenshot tests novos (não gerados): `SettingsLandscapeScreenshotTest`, `ContainerAddButtonScreenshotTest`.
- Ficou faltando: nada. Em paisagem o Avançado/Aparência/Comportamento só limitam largura (sem centralizar na vertical).
