# Spec: ajustes-onboarding

**Base:** `release/0.9.44` (com `ajustes-redesign` e `editar-foto-fundo` concluídas). **Depende de:** `polimento-ui` (logo no título e
`AureoleLogoRing`), que roda antes. **Protótipo:** `../prototipo-lote-17-11.html`, aba
"1 · ajustes-onboarding" · **Decisões:** `DECISOES.md`

## Requisitos

### Personalizar em modo vitrine
- [ ] **RF-01** No passo `GestureCoachStep.CUSTOMIZE`, tanto pelo toque e segure quanto pelo "Mostrar para mim", a sheet
  Personalizar abre em modo **vitrine**: nenhum tile responde, não arrasta para fechar, toque fora e voltar do sistema
  são ignorados.
- [ ] **RF-02** Sem barra e sem texto extra: a **logo do título** (mesma posição e tamanho do RF-05 da `polimento-ui`)
  ganha o `AureoleLogoRing` (RF-12 da `polimento-ui`), que fecha de 0 a 100% em **3 s** (D-01), linear. Ao fechar,
  a sheet desce sozinha e o tour segue (coach "Isso aí!" → checklist). Na vitrine a logo não é clicável.
- [ ] **RF-03** O modo vitrine é só um parâmetro da sheet (ex.: `isShowcase`), sem duplicar o Personalizar. Fora do
  onboarding nada muda. Com "remover animações" ligado, a sheet fecha sem animar, no mesmo tempo.
- [ ] **RF-04** O passo é marcado como feito ao abrir (como hoje); o fechamento automático limpa `succeededStep`.
  Teste do reducer: abrir → passo feito; tempo → sheet fechada; voltar durante a vitrine → nada muda.

### Paleta e fonte: fechar = padrão
- [ ] **RF-05** Fechar o seletor de paleta sem "Aplicar" (arrastar, tocar fora, voltar) e sem ter tocado em nada aplica
  **Frostbite**, conclui `CHOOSE_PALETTE` e devolve o checklist, como o "Aplicar". Fonte: **Istok Web** e `CHOOSE_FONT`.
- [ ] **RF-06** Se o usuário tocou numa paleta/fonte (prévia) e fechou sem aplicar, **fica a da prévia** e a etapa
  conta como feita com ela (D-02). Só sem nenhum toque vale Frostbite / Istok Web.
- [ ] **RF-07** Nenhum aviso explica isso no seletor (sem texto novo). A linha concluída mostra o valor ("Frostbite", "Istok Web"), como quando o usuário aplica.
  `dismissAppearancePicker()` passa a chamar o mesmo caminho do confirmar, com o nome padrão. Testes JVM do ViewModel.

### Relógio do onboarding
- [ ] **RF-08** Em retrato, o relógio criado pelo checklist (`placeSilently` → `placeOnboardingItem`) usa o tamanho e
  a posição do relógio do layout padrão (`GridDefaults`, 20×6 na linha 0), igual ao "Não mostrar mais". Se esse
  lugar estiver ocupado, segue a regra de hoje (primeiro espaço livre, depois o maior que couber).
- [ ] **RF-09** A config é a mesma padrão (`giveNewClockItsConfig`); o texto acompanha a célula, então fica com o mesmo
  tamanho do layout padrão (D-03). Paisagem não muda (menor tamanho do tipo, como hoje).
- [ ] **RF-10** `onboardingDefaultSize` perde `CLOCK_COLS/ROWS`; teste atualizado em `OnboardingItemPlacement`.

### Container do onboarding
- [ ] **RF-11** O container criado pelo checklist nasce com config salva: `isBackgroundEnabled = false`,
  `showFolderLabels = false`, `isGridFolderEnabled = true`. O container do layout padrão e o do Personalizar não mudam.
- [ ] **RF-12** Screenshot test do container nesse estado (com 4 pastas) e teste de que o `placeSilently` grava a config.

### Espiada da home depois de adicionar um item
- [ ] **RF-13** Quando uma tarefa de item termina (relógio, widgets, favoritos, container) e a home fica sozinha
  (`isHomePeeking`), aparece embaixo, no lugar da pílula do checklist, uma bolha de vidro com a **logo e o
  `AureoleLogoRing`** (`polimento-ui` RF-12). O anel fecha de 0 a 100% durante a espiada; ao fechar, a bolha **se abre na lista
  de Primeiros passos como shared element** (container transform): a bolha cresce, muda raio e posição até virar o
  cartão da lista, a logo viaja até o canto do título do cartão (o anel some no caminho) e o conteúdo da lista entra
  com fade depois de ~45% do movimento. ~550 ms, `FastOutSlowIn`. Em Compose: `SharedTransitionLayout` com
  `sharedBounds` para bolha↔cartão e `sharedElement` para a logo (ou o `OnboardingChecklistMorphComponent` de hoje
  estendido com o estado "bolha"). Se a lista abre minimizada (pílula), a bolha vira a pílula pelo mesmo caminho.
  Com "remover animações": troca direta, sem movimento. O item recém-adicionado ganha um contorno suave que some
  junto. A bolha não é clicável e não bloqueia a home.
- [ ] **RF-14** A espiada passa de 900 ms para **1,4 s** (D-04), para dar tempo de ver o item e o anel;
  com "remover animações" segue 300 ms, sem anel animado (logo parada). `ONBOARDING_HOME_PEEK_MS` muda; testes de tempo
  atualizados. Também vale para a falha de tarefa que hoje já espia (sem o contorno do item).
- [ ] **RF-15** Screenshot da bolha a 0%, 50% e 100%, e teste de que tocar na home durante a espiada não abre nada novo
  (como hoje).

### Ajustar layout: o Salvar não pode cobrir o item
- [ ] **RF-16** O `OnboardingEditHintComponent` sai do topo (onde cobre o relógio, que nasce na linha 0) e vai para
  **baixo**, acima da barra de navegação, na zona do polegar. Começa como hoje (texto de ajuda + "Salvar").
- [ ] **RF-17** Depois de **3 s** ou do primeiro arraste/redimensionamento (o que vier antes), o texto some e sobra só
  a **pílula "✓ Salvar"** centralizada (48 dp de altura). O texto não volta na mesma sessão.
- [ ] **RF-18** **Durante** um arraste ou redimensionamento, a pílula fica com ~25% de opacidade e deixa o toque passar
  (não intercepta o gesto); volta ao soltar.
- [ ] **RF-19** Se o item sendo editado (ou qualquer item) ocupar a faixa de baixo onde a pílula fica, ela vai para o
  topo; a regra (função pura: faixa com menos sobreposição) é testada em JVM. Paisagem: pílula no canto do polegar
  (direita; esquerda no canhoto).
- [ ] **RF-20** TalkBack: a pílula continua anunciando "Salvar" e o texto de ajuda vira a descrição dela.

## Fora do escopo
- Mudar o tamanho dos outros itens do onboarding (widgets, lista de apps).
- Mudar o Personalizar fora do onboarding.
