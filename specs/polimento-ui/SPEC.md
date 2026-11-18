# Spec: polimento-ui

**Base:** `release/0.9.44` (com `ajustes-redesign` e `editar-foto-fundo` concluídas).
**Protótipo:** `../prototipo-lote-17-11.html`, aba "3 · polimento-ui" · **Decisões:** `DECISOES.md`

## Definir plano de fundo
- [ ] **RF-01** Definir plano de fundo abre **dentro da sheet de Aparência**, como a Paleta e a Fonte (RF-14/D-09 da
  `ajustes-redesign`): o conteúdo troca, o título vira "Definir plano de fundo" **sem botão de voltar** (o voltar do sistema e o arrastar
  para baixo devolvem para Aparência, como na Paleta e na Fonte), e a
  prévia de cima mostra a cor ou a foto na hora. O editor de foto (`editar-foto-fundo`) segue em tela cheia por cima.
  Em paisagem, prévia à esquerda e conteúdo à direita.
- [ ] **RF-02** Espaçamento: o mesmo respiro antes de cada rótulo (TIPO, DO SEU TEMA, PALETAS, TONS) usado no RF-15 da
  `ajustes-redesign`, e espaço fixo entre rótulo e conteúdo. O switch "Usar também na tela de bloqueio" vira uma linha
  normal (sem a faixa de fundo), com margem própria abaixo dos cartões de tipo.
- [ ] **RF-03** Sai a seção **Vivas** (rótulo e cores). Quem tem uma cor das Vivas salva no fundo passa, numa migração
  única, para o **Fundo da paleta atual** (D-04). Teste da migração.

## Logo e cabeçalho das sheets
- [ ] **RF-04** Sheets abertas pelos Ajustes não mostram o atalho da logo (`onSettingsClick = null` e sem logo) (D-05).
- [ ] **RF-05** Nas sheets da home, a logo fica **discreta: ~16 dp** de largura (anel, quando houver, ~30 dp), com
  área de toque de 48 dp. Hoje ela parece caída porque o slot fica preso no `TopEnd` da sheet (com padding próprio) e
  não na linha do título. Agora ela entra **na mesma Row do título** e é centralizada na **altura da linha do título**
  (`Alignment.CenterVertically` sobre a caixa de texto, não sobre o cabeçalho com o espaço de baixo). O título continua
  centralizado na sheet (slot vazio do mesmo tamanho do outro lado); espelha no canhoto. `headerIcon` próprio segue o
  mesmo tamanho e posição. Screenshot test do cabeçalho com uma régua no centro do texto para pegar regressão.
- [ ] **RF-05b** Editores de item (pasta, botão rápido) não têm a logo; ela fica nas sheets de navegação da home
  (Personalizar, Escolher favoritos e as que abrem a partir delas).
- [ ] **RF-06** Animação **enquanto pressiona** (sem anel de progresso aqui): com o dedo na logo ela encolhe para
  ~0,86 e inclina ~-18° (180 ms, ease-out) e aparece um fundo circular leve; ao soltar volta com mola (leve overshoot)
  e os Ajustes abrem no soltar. Cancelar o toque (arrastar para fora) só volta, sem abrir. Sem animações do sistema:
  sem movimento, abre direto (D-09).
- [ ] **RF-07** A sheet Escolher favoritos aberta pelos Ajustes também perde o lápis "Personalizar home" (cf. favoritos).

## Ajustes › linhas
- [ ] **RF-08** Sai "Exibir indicador de widgets" (Comportamento › Tela inicial): `showWidgetDots` sai do
  `SettingsUiState`, do `SettingsToggleManager`, do `MainUiState` e da home; a string sai. A home usa só o controle de
  cada grupo (`widgetStackDots`).
- [ ] **RF-09** Migração única: se o global estava **desligado**, cada grupo existente (retrato e paisagem) é gravado
  desligado; depois a chave global é apagada (D-06). Teste da migração.
- [ ] **RF-10** Cor do título: **uma cor só para todo título de linha**, com ou sem subtítulo, linha de navegação ou
  `AureoleSwitchRow`: `onSurfaceMedium` (subtítulo segue `onSurfaceLow`; destrutiva segue `error`) (D-07, revisto
  17/11 10:46: "Personalizar home" e "Verificar atualizações" estavam mais claros que as vizinhas). Regra única em
  `composable/menu/` (`menuRowTitleColor(isDestructive)`), teste unitário e screenshots das sheets afetadas.
- [ ] **RF-11** Linha destrutiva sem o quadrado atrás do ícone (`MenuIconColumn` perde o `tileModifier`); ícone e
  título seguem `error`, "Segure para confirmar" fica (D-08: A).

## Anel da logo (componente)
- [ ] **RF-12** Novo `AureoleLogoRing(progress: Float?)` em `core/designsystem/components`, extraído do anel do
  onboarding: `null` = indeterminado (gira), número = progresso. Respeita "remover animações" (estático) e tem
  semântica de progresso para o TalkBack. Screenshot: 0, 50%, 100%, indeterminado.
- [ ] **RF-13** Aplicado (fase 1, D-10) onde há espera ou progresso de verdade: vitrine do Personalizar no onboarding (anel
  fecha em 3 s, ver `ajustes-onboarding` RF-02) e fim do onboarding (o anel fecha e a celebração
  de hoje começa). A raiz dos Ajustes NÃO usa o anel: a logo grande fica parada, como antes da fase 3 (por pedido do dono, D-15).
- [ ] **RF-14** Cabeçalho do checklist "Primeiros passos": o ícone de dentro cresce (~40 dp na largura); o halo fica igual.

## "Personalizar home" em todas as sheets de componente
- [ ] **RF-18** A linha "Personalizar home" (`customizeHomeEntry`, mesmo ícone, título e subtítulo) entra também:
  no menu do app ao segurar (`AppItemMenus`, antes do grupo destrutivo), na sheet da Pilha de widgets (visível sem
  rolar, depois de "Mostrar indicador de posição") e na sheet do Container (`EditContainerMainContent`, no fim de
  OPÇÕES). Tocar fecha a sheet (salvando o que estiver em edição, como no `EditContainerDialog`) e abre o Personalizar.
- [ ] **RF-19** Título da sheet com a logo: o título tem slots de 48 dp dos dois lados e corta com reticências; não
  passa por baixo da logo ("Configurações do container" encostava nela). Título da sheet do container: "Container".

## Pasta aberta embaixo da barra de navegação (bug)
- [ ] **RF-15** A pasta aberta em lista (`OpenedFolderPopup`) não pode passar da barra de navegação. Hoje
  `calculateClampedTopDp` limita o topo a "altura da tela − 300 dp" e ignora a barra de baixo, mas o popup chega a
  400 dp (`FolderPopupMetrics.HeightMax`) + título, então a pasta perto do fim da tela entra embaixo da barra.
- [ ] **RF-16** Regra nova (função pura, testada em JVM): espaço útil = tela − barra de status − barra de navegação
  (`WindowInsets.navigationBars`) − 16 dp de margem. A altura do popup é o mínimo entre o conteúdo, 400 dp e o espaço
  útil. Se a pasta cabe abaixo do ícone, abre para baixo alinhada a ele; se não, sobe até caber (o fundo do popup
  encosta na margem acima da barra); se nem assim, ocupa o espaço útil e a lista rola. Vale para retrato, paisagem,
  canhoto, navegação por gestos e por 3 botões. A pasta em grade (centralizada) usa o mesmo limite de altura.
- [ ] **RF-17** Testes: unitário da regra (ícone no topo, no meio, no fim; barra de 48 dp e de gestos) e screenshot
  com a pasta aberta a partir do último item do container.

## Fora do escopo (fase 2 da logo-anel)
- Anel em aplicar fundo / tela de bloqueio / cópia com desfoque e na primeira carga das listas.
