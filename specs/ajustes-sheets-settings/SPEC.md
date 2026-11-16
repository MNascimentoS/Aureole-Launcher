# Spec: ajustes-sheets-settings

Polir o Personalizar e as telas de Ajustes, dar um limite elástico à sheet e trocar Aplicar/Cancelar por
"salva ao fechar + Desfazer".

**Execução:** `EXECUTAR.md` · **Base:** `main` em `c0b2ee8` (versão 0.9.36.327) · **Protótipo:** `specs/ajustes-sheets-settings/prototipo.html`
**Referências:** os 6 prints do aparelho anexados na conversa de 16/11 (Personalizar, Personalizar expandido,
Ajustes retrato, Ajustes paisagem, Avançado, Definir plano de fundo).

## Requisitos

### Personalizar e cabeçalho das sheets (fase 1, Sonnet)

- [ ] **RF-01** Personalizar: espaço maior entre "Ajustar" e "Adicionar à home" (`large`, 24 dp, entre a fileira de
  botões e o rótulo da seção, no lugar dos 12 dp de hoje). (fase 1)
- [ ] **RF-02** Cabeçalho das sheets: título em `titleLarge` (20 sp, hoje `titleMedium` 16 sp) e respiro `small`
  (12 dp) acima do título, depois da faixa de gesto. Vale para todas as sheets do `OverlaySheetBody` (D-01). (fase 1)
- [ ] **RF-03** Personalizar: o tile "Configurações" troca a engrenagem pelo logo do Aureole (`AureoleIcons.Logo`),
  com a proporção do logo (128:73) dentro da caixa de 40 dp, mesma cor dos outros ícones. O rótulo continua
  "Configurações". (fase 1)

- [ ] **RF-20** Botão de adicionar dentro do container (home): o ícone atual (`ic_aureole_add`, quadrado arredondado
  com +) dá lugar ao **Icon Add New** do Figma (quatro cantos e um + no meio). Vetor pronto em
  `specs/ajustes-sheets-settings/assets/ic_aureole_add_new.xml`. Só no botão do container (D-06); os outros botões de adicionar ficam como estão. (fase 1)

### Telas de Ajustes (fase 1, Sonnet)

- [ ] **RF-04** Ajustes (raiz): "Muito obrigado!" preso embaixo, a `xLarge` (32 dp) da borda inferior da área útil.
  Quando o conteúdo é maior que a tela (fonte grande, paisagem), ele vem logo depois das opções, sem sobrepor nada. (fase 1)
- [ ] **RF-05** Ajustes em paisagem: dois painéis (D-02, decidido): cabeçalho (logo, nome, versão) à esquerda e
  opções à direita, os dois centralizados na vertical e visíveis sem rolar; "Muito obrigado!" embaixo do painel das opções. As telas
  Avançado, Aparência e Comportamento também centralizam o conteúdo em paisagem com largura máxima. (fase 1)
- [ ] **RF-06** Avançado: links do rodapé (Colaboradores, Política de privacidade, Termos de serviço, Licenças de
  código aberto) em `bodyMedium` (16 sp, hoje `bodySmall` 12 sp) com área de toque de 48 dp. (fase 1)
- [ ] **RF-07** Avançado: nova ordem — Informações do app, Novidades, Launcher padrão, Redefinir launcher,
  Desinstalar launcher. Textos mais curtos: "What's new (Release notes)" vira "What's new" / "Novidades" e
  "Set as default launcher" vira "Default launcher" / "Launcher padrão". (fase 1)

- [ ] **RF-21** Ícone "Home" novo (casa de traço com porta, do Figma) no item **Launcher padrão** do Avançado. Vetor
  pronto em `specs/ajustes-sheets-settings/assets/ic_aureole_home.xml`. Troca o desenho de `AureoleIcons.Home`, que só
  aparece em dois lugares, os dois com o mesmo sentido: Launcher padrão (Avançado) e "Definir como app da home"
  (checklist do onboarding). (fase 1)

### Onboarding e lista de apps

- [ ] **RF-17** Coach "Deslize para cima": depois de abrir a lista, o texto passa a ser "Seus apps estão aqui. Toque em
  voltar para fechar." / "Your apps live here. Press back to close." (hoje fala em tocar fora). Tocar fora continua
  fechando; o passo conclui por qualquer fechamento, como hoje. (fase 1)
- [ ] **RF-19** Coach "Deslize na borda": a mão de demonstração precisa mostrar com clareza que o gesto começa
  **entrando pela borda**. O dedo nasce colado à borda (meio fora da tela), anda ~56 dp para dentro (hoje 32 dp, quase
  invisível) com aceleração suave, faz uma pausa curta e só então desce. O rastro desenha o "L" inteiro (horizontal
  e depois vertical); hoje só desenha a parte vertical. Espelhado no canhoto. (fase 1)
- [ ] **RF-18** A lista de apps entra de acordo com o atalho: puxar pela borda (scrubber) faz a lista vir **da borda
  puxada** (direita no destro, esquerda no canhoto) e sair por ela; qualquer outro atalho (deslizar para cima, botão
  Início, botão rápido, atalho "Todos os apps" em container/pasta, coach do onboarding) faz a lista vir **de baixo** e
  descer ao fechar. Hoje só o botão Início vem de baixo. (fase 3)

### Sheet elástica (fase 3, Opus)

- [ ] **RF-08** Quando todo o conteúdo da sheet já aparece na altura de repouso, puxar para cima estica com
  resistência (elástico, no máximo ~64 dp) e, ao soltar, volta para o repouso com mola. Não expande, não vibra o tic
  de detent, a prévia não esmaece. Fling para cima e toque duplo na faixa de gesto fazem um "quique" curto e voltam. (fase 3)
- [ ] **RF-09** Quando há só um pouco de conteúdo abaixo, a sheet expande só até a altura do conteúdo (nunca além de
  90%), sem a faixa vazia entre conteúdo e borda (print 2). Com muito conteúdo, continua indo a 90% como hoje. (fase 3)
- [ ] **RF-10** Nada do que existe quebra: arrastar para baixo fecha, rolagem aninhada (listas e grades), sheets com
  `LazyColumn`, sheets com teclado (`bottomExtra`), canhoto, `customActions` do TalkBack (a ação "Expandir" some
  quando a sheet não pode expandir) e as sheets com prévia. (fase 3)

- [ ] **RF-16** Abrir uma sheet: o fundo escuro esmaece e a sheet sobe de baixo da tela até o repouso, com mola
  (sem quique). A prévia acima da sheet acompanha o topo da sheet e esmaece junto. Fechar faz o inverso: a sheet desce
  a partir da altura em que está (inclusive no meio de um arraste) e o fundo some. Hoje tudo entra e sai com fade.
  Vale para todas as sheets do `AureoleEditOverlay`. Com animações desligadas, aparece direto. (fase 3)

### Checklist "Primeiros passos" (fase 3, Opus)

- [ ] **RF-15** Minimizar o checklist (botão "Minimizar" ou toque no fundo escuro) anima o cartão até virar a pílula
  de baixo, como um shared element: o contorno do cartão encolhe e arredonda até a pílula, o halo de progresso do
  cabeçalho viaja até o halo da pílula e "0 de 9 passos" vira o texto da pílula. A lista some antes de o cartão
  encolher, e o fundo escuro esmaece junto. Tocar na pílula faz o caminho inverso. Com animações desligadas no
  sistema, troca sem animar. (fase 3)

### Salvar ao fechar com Desfazer (fase 2, Sonnet)

- [ ] **RF-11** As sheets de edição não têm mais Aplicar/Cancelar/Salvar/Confirmar/Pronto. A prévia mostra a escolha na
  hora; fechar a sheet (tocar fora, voltar do sistema e arrastar para baixo, D-04) salva. (fase 2)
- [ ] **RF-12** Se algo mudou, a tela de baixo mostra o snackbar de 4 s (`AureoleUndoSnackbar`) com uma mensagem da
  mudança e "Desfazer", que devolve o valor anterior. Se nada mudou, fecha sem snackbar. (fase 2)
- [ ] **RF-13** Telas convertidas: Definir plano de fundo, Paleta, Fonte, Seletor de cor (cor de destaque),
  Aparência do botão rápido, Cartões e vidro, Editar pasta e Apps da pasta (D-05). "Imagem personalizada"
  abre o seletor de fotos ao tocar no cartão; o desfazer da imagem reaproveita o `wallpaperStash`. (fase 2)
- [ ] **RF-14** Continuam com botão: confirmações destrutivas (`AureoleConfirmDialog`: redefinir, desinstalar, com
  segurar) e Criar pasta, que fica só com o botão "Criar" (D-03). (fase 2)

## Fora do escopo

- Barra Salvar/Cancelar do modo de edição do layout (grade) e o "Salvar" da dica do onboarding: são modos de tela, não
  sheets. Fica para outra spec se fizer sentido.
- Tema claro novo, tablet.
- Mudar a duração do snackbar ou empilhar mais de um desfazer (o `UndoController` segue com uma oferta por vez).
