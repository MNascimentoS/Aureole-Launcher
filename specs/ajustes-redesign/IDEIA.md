# ajustes-redesign (exploração, sem código)

As telas de Ajustes (raiz, Aparência, Comportamento, Avançado) ficaram com a cara antiga, enquanto as sheets ganharam
seções em versalete, tiles, linhas com coluna de ícone, prévia viva e o lado do polegar. A ideia é ver como os Ajustes
ficariam reorganizados na mesma linguagem, **sem remover nada do app**. Por enquanto é só para olhar: protótipos aqui
(`prototipo.html`) e, depois da execução da spec `ajustes-sheets-settings`, as propostas da própria IA
(`PROPOSTAS-IA.md`, gerado pela fase 5 de lá).

## Inventário (tudo isto tem de continuar existindo)

**Raiz:** logo + "Aureole" + versão (toque abre as notas da versão) · Aparência · Comportamento · Avançado · "Muito obrigado!"

**Aparência:** Paleta de cores · ~~Cores personalizadas~~ (removido no teste do aparelho, D-08; a cor base fica em Definir plano de fundo → Cor sólida) · Alterar fonte · Definir plano de fundo · Restaurar
fundo · Escala da imagem · Ícones temáticos · Cartões e vidro (Alterar borda, raio em dp, Efeito de vidro)

**Comportamento:** Home abre todos os apps · Todos os apps do lado esquerdo (canhoto) · Desativar rolagem alfabética ·
Exibir indicador de widgets · Deslocamento do cabeçalho · Verificar atualizações

**Avançado:** Informações do app · Novidades · Launcher padrão · Redefinir launcher · Desinstalar launcher ·
Colaboradores · Política de privacidade · Termos de serviço · Licenças de código aberto

**Conferência (fase 3, 17/11):** 26 itens (3 cartões + 23 linhas), conferidos pelo `SettingsInventoryTest`. Nada mais saiu.

## As 3 direções do protótipo

- **A · Home viva:** uma tela só, com a mini-home no topo (como nas sheets) e grade de atalhos (Paleta, Fonte, Fundo,
  Vidro) igual ao Personalizar; o resto em seções com linhas de ícone. As três subtelas viram seções da mesma rolagem.
- **B · Cartões com resumo:** a raiz mantém o logo, mas cada área vira um cartão grande que mostra o valor atual
  ("Frostbite · Istok Web · vidro médio"); as subtelas ganham prévia no topo e grupos.
- **C · No polegar:** conteúdo ancorado embaixo, busca "Buscar ajuste", abas Aparência / Comportamento / Avançado na
  parte de baixo; o logo encolhe e vira cabeçalho.

## Escolha (16/11, Mateus + Aislan)

**Direção B, com ajustes:**
- Raiz: logo, nome e versão em cima; Aparência, Comportamento e Avançado como **cartões sem tags e sem setinha**
  (ícone + título + subtítulo, como as linhas de hoje). As tags de resumo (Frostbite, Istok Web…) saem: crescem demais.
- Aparência, Comportamento e Avançado abrem como **bottom sheet** (`AureoleEditOverlay`) por cima da raiz, com título
  centralizado e sem botão de voltar. Reaproveita a prévia (mini-home em cima da sheet de Aparência) e todos os
  componentes das sheets.
- Avançado: Sistema → Sobre (Colaboradores, Privacidade, Termos, Licenças) → **Zona de cuidado no fim**. Redefinir e
  **Desinstalar** com "Segure para confirmar".
- Protótipo atualizado: aba "B · Cartões ✓" do `prototipo.html`.
