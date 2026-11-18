# Spec: botao-rapido

Sheet de aparência do Botão rápido (`feature/home/components/quickbutton/QuickButtonAppearance*.kt`).
**Base/depende de:** `pasta-form` (seletor de cor do tema/paletas e grade de ícones por categoria viram componentes
compartilhados lá) e `polimento-ui` (logo do título). **Protótipo:** `../prototipo-lote-17-11.html`, aba
"5 · botao-rapido" · **Decisões:** `DECISOES.md`

## Requisitos
- [ ] **RF-01** **Nome primeiro:** mesmo campo em pílula da `pasta-form` (RF-08 de lá): o nome fica num **campo em pílula** embaixo da prévia: fundo leve, borda fina e um botão
  redondo de lápis no fim, para ficar óbvio que se edita ali. Tocar na pílula ou no lápis foca o campo e abre o
  teclado; focado, a borda acende na cor de destaque, aparece o cursor e o lápis vira "×" (limpar). Vazio mostra
  "Dar um nome" (cor baixa). 48 dp de toque; TalkBack: "Nome, <valor>, editar". Abre sem teclado.
  Vazio = nome da ação do toque (ex.: "Todos os apps"). O nome vira o `contentDescription` do botão na home
  (TalkBack) em vez do texto genérico de hoje.
- [ ] **RF-02** "Mostrar nome na home" (desligado por padrão, D-01): mostra o nome embaixo do botão, na fonte do tema,
  com o mesmo estilo dos rótulos de app.
- [ ] **RF-02b** A sheet do Botão rápido **não mostra a logo** do Aureole no título (`onSettingsClick = null`) (Mateus, 17/11).
- [ ] **RF-03** Segmentado **Ícone | Cores** no topo da sheet: cada aba tem um scroll só e a sheet não vira uma
  rolagem gigante.
- [ ] **RF-04** **Ícone:** o catálogo único da `pasta-form` (`AureoleIconCatalogGrid`, RF-14/RF-15 de lá): Ações (os 28
  de hoje) + as 8 categorias da pasta, com chips de atalho e o nome da categoria grudando. Marcado = preenchido.
  `QuickButtonAppearance.icon` passa a guardar a chave do catálogo; o nome antigo do enum lido do JSON resolve para a
  chave nova (sem perder o ícone de quem já tem). Teste de leitura do JSON antigo.
- [ ] **RF-05** **Cores:** segmentado "Cor do ícone | Cor do fundo" e, abaixo, o seletor compartilhado da
  `pasta-form` (`AureoleThemeColorPicker`): "Do seu tema" (Fundo, Superfície, Variante, Contorno, Destaque) e "De outras paletas" (Frias, Frescas,
  Neutras, Quentes). Cor do tema é salva como papel e **acompanha a troca de tema**; cor de outra paleta fica fixa.
  A cor da Borda usa o mesmo seletor quando a Borda está ligada.
- [ ] **RF-06** Contraste: se ícone e fundo ficarem com contraste baixo (< 3:1), a prévia mostra um aviso curto e
  o ícone ganha contorno; não bloqueia.
- [ ] **RF-07** Modelo: `QuickButtonAppearance` ganha `label: String?` e `showLabel: Boolean`; `backgroundColor`,
  `iconColor` e `strokeColor` passam a aceitar papel do tema ou ARGB (o mesmo tipo de cor da `pasta-form`). Botões
  existentes migram com as cores ARGB de hoje (fixas), sem mudar de visual. Teste da migração e do JSON salvo.
- [ ] **RF-08** Paisagem: prévia à esquerda com o nome editável, sheet à direita; canhoto espelha.
- [ ] **RF-09** Testes: screenshot das duas abas, com nome mostrado e escondido, cor do tema em dois temas
  diferentes; unitário do agrupamento dos ícones (todo ícone em exatamente uma categoria) e da regra de contraste.

## Fora do escopo
- Mudar as ações (toque, segurar, deslizar).
