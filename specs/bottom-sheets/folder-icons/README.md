# Ícones de pasta (Fase 6b — aprovada: só salvar)
Origem: Material Symbols Rounded (google/material-design-icons, Apache 2.0). 80 ícones em 8 categorias, escolhidos por significado (ver `folder-icons-contact-sheet.png`).

Como aplicar (Claude Code, `/fase 6b`):
1. Copiar `drawable/ic_folder_*.xml` -> `core/designsystem/src/main/res/drawable/` (vetores, sem tint fixo; o app tinge).
2. Copiar `AureoleFolderIcons.kt` para o pacote de ícones do design system e ajustar o `package`/`R`; adicionar `strings-folder-icons.xml` em values (+ pt-BR).
3. `FolderIconRegistry`: manter as chaves antigas por compatibilidade (lock, cart, play, share, warning…) e mapear para os novos: lock->lock, cart->shopping_cart, play->play_circle, share->share, warning->warning, info->info, etc.; chave desconhecida -> folder.
4. Seletor da pasta: grade de 5 colunas + chips de categoria (usar `byCategory`), item selecionado destacado. Teste unitário: toda chave antiga resolve e `resFor(null)` é nulo.
Licença: incluir NOTICE com Apache 2.0 / Google.
