# ajustes-redesign (exploração, sem código)

As telas de Ajustes (raiz, Aparência, Comportamento, Avançado) ficaram com a cara antiga, enquanto as sheets ganharam
seções em versalete, tiles, linhas com coluna de ícone, prévia viva e o lado do polegar. A ideia é ver como os Ajustes
ficariam reorganizados na mesma linguagem, **sem remover nada do app**. Por enquanto é só para olhar: protótipos aqui
(`prototipo.html`) e, depois da execução da spec `ajustes-sheets-settings`, as propostas da própria IA
(`PROPOSTAS-IA.md`, gerado pela fase 5 de lá).

## Inventário (tudo isto tem de continuar existindo)

**Raiz:** logo + "Aureole" + versão (toque abre as notas da versão) · Aparência · Comportamento · Avançado · "Muito obrigado!"

**Aparência:** Paleta de cores · Cores personalizadas (cor base) · Alterar fonte · Definir plano de fundo · Restaurar
fundo · Escala da imagem · Ícones temáticos · Cartões e vidro (Alterar borda, raio em dp, Efeito de vidro)

**Comportamento:** Home abre todos os apps · Todos os apps do lado esquerdo (canhoto) · Desativar rolagem alfabética ·
Exibir indicador de widgets · Deslocamento do cabeçalho · Verificar atualizações

**Avançado:** Informações do app · Novidades · Launcher padrão · Redefinir launcher · Desinstalar launcher ·
Colaboradores · Política de privacidade · Termos de serviço · Licenças de código aberto

## As 3 direções do protótipo

- **A · Home viva:** uma tela só, com a mini-home no topo (como nas sheets) e grade de atalhos (Paleta, Fonte, Fundo,
  Vidro) igual ao Personalizar; o resto em seções com linhas de ícone. As três subtelas viram seções da mesma rolagem.
- **B · Cartões com resumo:** a raiz mantém o logo, mas cada área vira um cartão grande que mostra o valor atual
  ("Frostbite · Istok Web · vidro médio"); as subtelas ganham prévia no topo e grupos.
- **C · No polegar:** conteúdo ancorado embaixo, busca "Buscar ajuste", abas Aparência / Comportamento / Avançado na
  parte de baixo; o logo encolhe e vira cabeçalho.
