# Favoritos: gerenciar e ordem (Fase 6c)
Regra nova: **sem favoritos configurados, a Home mostra todos os apps.** Com 1+ favoritos, só eles aparecem.

Código:
- Remover o switch "Mostrar todos os apps na Home" (flag showAll) e a ação "Ativar todos". Fonte da verdade: `favorites.isEmpty() -> allApps` (função pura `visibleApps(all, favorites)` + teste: vazio, 1 favorito, favorito removido do device).
- Lista da Home/gaveta usa `visibleApps`; migração: se showAll estava true, limpar favoritos salvos (comportamento equivalente).
- "Gerenciar aplicativos e ordem": subtítulo na tela anterior = "N favoritos, o resto fica oculto" ou "Sem favoritos: mostra todos os apps".
- Tela: dica fixa "Segure e arraste o botão para ordenar" (só com 2+ favoritos); banner "Sem favoritos: a Home mostra todos os N apps" quando vazio; cada linha = número + ícone + nome + ✕ (40 dp) + alça de 6 pontos (44x38 dp, destaca ao arrastar); "Remover todos" com Desfazer; "Outros aplicativos (N)" com botão + no ícone. Alça no lado da mão (espelha no canhoto). Acessibilidade: ações "mover para cima/baixo" no TalkBack.
