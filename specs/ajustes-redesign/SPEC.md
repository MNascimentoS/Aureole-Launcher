# Spec: ajustes-redesign (direção B)

Reorganizar os Ajustes na linguagem das sheets, **sem remover nada**: a raiz vira cartões limpos e Aparência,
Comportamento e Avançado abrem como bottom sheet sobre a raiz.

**Base:** branch com `ajustes-sheets-settings` concluída (usa `AureoleEditOverlay` que sobe de baixo,
`AureoleAutoSaveOverlay`, `AureoleHoldToConfirm`, ícone Home novo, paisagem em dois painéis). **Protótipo:**
`prototipo.html`, aba "B · Cartões ✓" · **Decisões:** `DECISOES.md` (todas respondidas) · **Execução:**
`../EXECUTAR-AJUSTES-E-FUNDO.md` · **Contexto:** `IDEIA.md` (inventário) e `PROPOSTAS-IA.md` (crítica da fase 5).

## Requisitos

### Raiz (fase 1)

- [ ] **RF-01** Raiz: logo, "Aureole" e versão em cima (toque abre as notas, como hoje); Aparência, Comportamento e
  Avançado como **cartões** (fundo `card`, raio grande, ícone + título + subtítulo), **sem tags e sem setinha**;
  "Muito obrigado!" preso embaixo. O "Deslocamento do cabeçalho" continua valendo para a raiz. (fase 1)
- [ ] **RF-02** Subtítulos: Aparência "Cores, fundo, fonte e vidro"; Comportamento "Gestos, lista e tela inicial";
  Avançado **"Sistema, sobre o app e créditos"** (EN: "Colors, background, font, and glass"; "Gestures, list, and home
  screen"; "System, about, and credits"). (fase 1)
- [ ] **RF-03** Paisagem: continua em dois painéis (marca à esquerda, cartões à direita). (fase 1)

### Conteúdo das sheets (fase 1)

- [ ] **RF-04** Aparência, em grupos com rótulo: **Tema** (Paleta de cores, Cores personalizadas, Alterar fonte, Ícones
  temáticos) · **Fundo** (Definir plano de fundo, Escala da imagem e Restaurar fundo, estes dois só com foto própria,
  como hoje) · **Cartões e vidro** (Alterar borda com "20 dp", Efeito de vidro com o nível). Subtítulo com o valor
  atual onde já existe (paleta, fonte, tipo de fundo). (fase 1)
- [ ] **RF-05** Comportamento: **Gestos e lista de apps** (Home abre todos os apps, Todos os apps do lado esquerdo,
  Desativar rolagem alfabética) · **Tela inicial** (Exibir indicador de widgets, Deslocamento do cabeçalho com a roda
  9 / 10 / 11 %). "Verificar atualizações" sai daqui (D-03). (fase 1)
- [ ] **RF-06** Avançado, nesta ordem: **Sistema** (Informações do app, Novidades, Launcher padrão, Verificar
  atualizações) · **Sobre** (Colaboradores, Política de privacidade, Termos de serviço, Licenças de código aberto, com
  ícone e chevron, não mais em texto apagado) · **Zona de cuidado** no fim (Redefinir launcher e Desinstalar launcher,
  em vermelho, os dois com **"Segure para confirmar"**). (fase 1)

### Navegação por sheets (fase 2)

- [ ] **RF-07** Tocar num cartão abre a área como bottom sheet (`AureoleEditOverlay`) sobre a raiz: sobe de baixo,
  título centralizado, sem botão de voltar; fecha por voltar, toque fora e arrastar. Aparência tem a mini-home de
  prévia em cima (`AureoleMiniHome`); Comportamento e Avançado não têm prévia (D-04). (fase 2)
- [ ] **RF-08** Item que abre outra sheet (Paleta, Cores personalizadas, Fonte, Definir plano de fundo, Cartões e
  vidro, Novidades, Colaboradores) **troca no lugar**: a sheet da área desce, a outra sobe; ao fechar a de dentro, a
  da área volta, na mesma rolagem (D-01). O salvar ao fechar + Desfazer das sheets de edição continua igual. (fase 2)
- [ ] **RF-09** Colaboradores abre como sheet (lista centralizada); Licenças de código aberto continua tela cheia
  (D-02). Política e Termos continuam abrindo o navegador. (fase 2)
- [ ] **RF-10** Sheet do Avançado é alta (passa de 60%): usa a mesma regra do Personalizar (repouso alto, rolagem
  interna, elástico). Em paisagem, as sheets têm largura máxima `settingsContentMaxWidth` e ficam centralizadas. (fase 2)
- [ ] **RF-11** Entradas de hoje continuam: o tile Configurações do Personalizar, o botão de ajustes das pastas e da
  pilha de widgets abrem a raiz. Voltar com uma sheet aberta fecha a sheet; na raiz, sai dos Ajustes. Canhoto espelha. (fase 2)

### Ajustes do teste no aparelho (fase 3, 17/11)

- [ ] **RF-12** Aparência: **sai "Cores personalizadas"** (pedido do Mateus, D-08). A cor base continua acessível por
  Definir plano de fundo → Cor sólida, que já gera a paleta Custom. O inventário passa a ter 26 itens. (fase 3)
- [ ] **RF-13** **Ícones temáticos** muda a prévia na hora: com o switch ligado, os ícones da mini-home em cima da sheet
  aparecem com a cor temática (a mesma regra de tint que a home usa); desligado, com as cores originais. (fase 3)
- [ ] **RF-14** **Paleta de cores** e **Alterar fonte** abrem **dentro da própria sheet de Aparência**: o conteúdo da
  sheet troca para o seletor (com título "Paleta de cores" / "Fonte"), a mesma prévia em cima mostra a escolha ao
  vivo, e voltar (gesto do sistema) devolve a lista de Aparência na mesma rolagem. Fechar a sheet salva, como hoje,
  com Desfazer. Substitui a troca de sheet (D-01) só para esses dois itens (D-09). (fase 3)
- [ ] **RF-15** Mais espaço antes de cada rótulo de seção (TEMA, FUNDO, CARTÕES E VIDRO, GESTOS E LISTA DE APPS,
  TELA INICIAL, SISTEMA, SOBRE, ZONA DE CUIDADO) em **todas** as telas e sheets de Ajustes: `large` (24 dp) acima do
  rótulo, menos no primeiro. (fase 3)
- [ ] **RF-16** **Paisagem com prévia**: sheets que têm prévia (Aparência, e dentro dela Paleta e Fonte; Definir plano
  de fundo; as demais sheets de edição com prévia) passam a dividir a tela: prévia à esquerda, conteúdo da sheet à
  direita, os dois centralizados na vertical e sem cortar. Hoje a prévia ocupa o topo e a sheet fica espremida
  embaixo. Retrato fica como está. (fase 3)

## Fora do escopo

- Mover Paleta/Fonte para o Personalizar (proposta F da IA), Ajustes como sheet sobre a home (proposta E) e busca.
- Remover qualquer item além do "Cores personalizadas" (D-08): o checklist do `IDEIA.md` tem de fechar.
