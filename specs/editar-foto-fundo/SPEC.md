# Spec: editar-foto-fundo

Editor da foto de fundo escolhida na galeria: enquadrar (zoom e posição), escurecer e desfocar, sem alterar a foto
original, com a home por cima como prévia e salvar ao fechar + Desfazer. E aplicar o fundo (foto ou cor sólida)
também na tela de bloqueio do Android.

**Base:** branch com `ajustes-sheets-settings` **e** `ajustes-redesign` concluídas (Definir plano de fundo já é uma
sheet filha da sheet de Aparência, com troca no lugar; usa `AureoleAutoSaveOverlay`, `WallpaperUndoStash`). **Protótipo:** `prototipo.html` · **Decisões:** `DECISOES.md` · **Execução:** `../EXECUTAR-AJUSTES-E-FUNDO.md`

## Requisitos

### Abrir o editor

- [ ] **RF-01** Escolher uma foto em "Imagem personalizada" abre o editor logo em seguida, já com a foto. O editor é
  tela cheia por cima dos Ajustes; ao fechar, volta para a sheet Definir plano de fundo (que por sua vez volta para
  Aparência, pela navegação da `ajustes-redesign`). (fase 2)
- [ ] **RF-02** Com uma foto já aplicada, o cartão "Imagem personalizada" mostra uma miniatura da foto e, embaixo,
  dois **botões só com ícone** (40 dp, área de toque de 48 dp): **lápis** = "Editar foto" (abre o editor com os ajustes
  atuais; botão principal, preenchido) e **imagem com setas** = "Trocar foto" (abre a galeria). Os rótulos ficam como
  `contentDescription` para o TalkBack. Os dois cartões de tipo têm a
  mesma altura, e o conteúdo do "Cor sólida" (ícone + texto) fica **centralizado na vertical**, não no topo. (fase 2)

### Editor

- [ ] **RF-03** Tela cheia com a foto e, por cima, a home em versão fantasma (relógio e uma fileira de apps a ~40%)
  para o usuário ver o que fica atrás do quê. Uma moldura marca a área da tela; o que fica fora aparece esmaecido. (fase 2)
- [ ] **RF-04** **Enquadrar:** pinça para aproximar (1x a 5x), arraste para mover, toque duplo alterna 1x ↔ 2x no ponto
  tocado. A foto nunca deixa buraco: o zoom mínimo é o que cobre a tela inteira e o arraste para nas bordas. (fase 2)
- [ ] **RF-05** Sheet de ajustes embaixo (sobe de baixo, como as outras), com três abas: **Enquadrar** (botões
  "Centralizar" e "Girar 90°"), **Escurecer** (0–70%, padrão 30%, o valor de hoje) e **Desfoque** (0–25 dp,
  padrão 0). Cada slider mostra o valor; "Redefinir" volta tudo ao padrão. (fase 2)
- [ ] **RF-06** Fechar o editor (voltar, tocar fora da sheet não se aplica aqui: a foto ocupa a tela; então voltar do
  sistema ou arrastar a sheet para baixo) salva. Se mudou algo, a tela de baixo mostra "Foto do fundo ajustada ·
  Desfazer"; o Desfazer volta os ajustes anteriores (ou a foto anterior, se a foto foi trocada). (fase 2)

### Como o fundo é desenhado

- [ ] **RF-07** A foto original é guardada uma vez (como hoje) e os ajustes ficam separados: zoom, foco (x, y
  normalizados de 0 a 1), rotação em passos de 90°, escurecer e desfoque. Editar de novo parte da original, sem perda. (fase 1)
- [ ] **RF-08** A home desenha o fundo com esses ajustes; o escurecimento sai do 30% fixo e passa a usar o valor salvo.
  Em paisagem, usa o mesmo ponto de foco (cobre a tela, centrado no foco). (fase 1)
- [ ] **RF-09** Desfoque: Android 12+ com `RenderEffect`; Android 9–11 (o app suporta desde a API 28) com uma cópia
  desfocada gerada ao salvar e guardada em cache. O vidro (Haze) dos cartões continua funcionando em cima. (fase 1)
- [ ] **RF-10** "Escala da imagem" (Preencher / Ajustar / Esticar) continua nos Ajustes. Com enquadramento salvo, ver D-01. (fase 1)
- [ ] **RF-11** Instalações que já têm foto: sem ajustes salvos, o fundo fica exatamente como hoje (Preencher,
  centralizado, escurecer 30%). (fase 1)

### Tela de bloqueio (fase 3)

- [ ] **RF-13** Em Definir plano de fundo, abaixo do tipo, um switch **"Usar também na tela de bloqueio"** (vale para
  cor sólida e foto). Ligado, cada fundo salvo também vira o papel de parede da tela de bloqueio do Android. (fase 3)
- [ ] **RF-14** O que vai para a tela de bloqueio é o que o usuário vê na home sem os apps: foto com enquadramento,
  rotação, escurecer e desfoque (D-04), em bitmap do tamanho da tela em retrato; cor sólida vira um bitmap liso da
  cor do fundo. Aplicado em segundo plano, com `WallpaperManager.setBitmap(…, FLAG_LOCK)`. (fase 3)
- [ ] **RF-15** Ao ligar o switch pela primeira vez, um aviso curto: o Android não deixa o Aureole guardar a imagem
  que está hoje na tela de bloqueio, então desligar não volta a anterior. Botões "Usar mesmo assim" / "Agora não". (fase 3)
- [ ] **RF-16** Desligar o switch pergunta o que fazer com a tela de bloqueio: **"Manter como está"** ou **"Voltar ao
  fundo do sistema"** (`clear(FLAG_LOCK)`: a tela de bloqueio passa a usar o papel de parede do sistema). (fase 3)
- [ ] **RF-17** Desfazer (do salvar ao fechar e do editor) também volta a tela de bloqueio para o fundo anterior que o
  Aureole aplicou. Se o dispositivo não deixar trocar papel de parede (`isSetWallpaperAllowed() == false`, aparelho
  gerenciado), o switch aparece desligado e desabilitado, com a explicação embaixo. Falha ao aplicar: snackbar
  "Não deu para mudar a tela de bloqueio", e a home segue normal. (fase 3)

## Fora do escopo

- Filtros de cor, recorte livre fora da proporção da tela, texto ou desenho na foto.
- Fundo diferente para paisagem.
- Mudar o papel de parede da **tela inicial** do sistema (a home do Aureole desenha o próprio fundo, como hoje).
- Ler ou guardar a imagem atual da tela de bloqueio (precisa de permissão de sistema que apps comuns não têm).
