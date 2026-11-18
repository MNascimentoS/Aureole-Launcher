# Decisões: ajustes-redesign

## D-01 Item que abre outra sheet: troca no lugar e volta, ou empilha?
opções: Troca no lugar e volta | Empilha por cima
trava: fase 2
resposta: Troca no lugar e volta (17/11 00:04)

## D-02 Colaboradores e Licenças: sheet ou tela cheia?
opções: Colaboradores sheet, Licenças tela cheia | Os dois como sheet | Os dois tela cheia
trava: fase 2
resposta: Colaboradores sheet, Licenças tela cheia (17/11 00:04)

## D-03 "Verificar atualizações" vai para Avançado › Sistema?
opções: Sim | Não, fica no Comportamento
trava: fase 1
resposta: Sim (17/11 00:04)

## D-04 Sheet de Comportamento tem prévia?
opções: Sem prévia | Prévia da lista de apps
trava: fase 2
resposta: Sem prévia (17/11 00:04)

## D-05 Tags de resumo nos cartões da raiz?
opções: Sem tags | Rolagem horizontal
trava: fase 1
resposta: Sem tags, e sem setinha (16/11 23:54, Mateus + Aislan)

## D-06 "Desinstalar launcher" na Zona de cuidado: manter a sheet "Fale conosco / Desinstalar"?
opções: Segurar dispara a ação direto (abre a página do app na loja) | Segurar abre a sheet de retenção antes
trava: fase 1
resposta: Segurar dispara a ação direto (opção do PLANO, sem sheet extra). `UninstallBottomSheet` e `FactoryResetBottomSheet` ficam no código sem uso pela tela de Avançado; fase 3 decide se removem (e a string `settings_advanced_contact_us`).

## D-07 Cor do cartão da raiz
opções: `AureoleDS.colors.card` (não existe) | `surfaceVariant` a 45% de opacidade (igual ao `--card` do protótipo)
trava: fase 1
resposta: `surfaceVariant` a 45%, conservador (sem criar token novo).

## D-08 "Cores personalizadas" continua na Aparência?
opções: Remover (a cor base já está em Definir plano de fundo → Cor sólida) | Manter
trava: fase 3
resposta: Remover (Mateus, 17/11 00:37, teste no aparelho)

## D-09 Paleta e Fonte: trocar de sheet ou trocar o conteúdo dentro da sheet de Aparência?
opções: Dentro da sheet de Aparência, reaproveitando a prévia | Trocar de sheet (D-01)
trava: fase 3
resposta: Dentro da sheet de Aparência (Mateus, 17/11 00:37). Os outros filhos (Fundo, Cartões e vidro, Novidades, Colaboradores) seguem o D-01.

## D-10 Voltar das Licenças de código aberto: reabre o Avançado ou volta para a raiz?
opções: Reabre o Avançado na mesma rolagem | Volta para a raiz
trava: fase 2
resposta: Reabre o Avançado na mesma rolagem (conservador: é o que a tela antiga fazia). Seguido na fase 2.


## D-11 Paisagem com prévia: a sheet fica centralizada na vertical ou presa embaixo?
opções: Presa embaixo, com a altura toda da tela | Centralizada na vertical
trava: fase 3
resposta: Presa embaixo, com até 90% da altura (conservador, decidido na execução). A prévia fica centralizada na coluna esquerda; a sheet, na direita, continua bottom sheet (arrastar, deslizar e fechar iguais ao retrato) e chega à altura útil, então nada corta e o conteúdo rola dentro. Centralizar exigiria cantos de baixo arredondados e outra conta de deslize. Rever na revisão visual da fase 4.
revisão (fase 4): RF-16 atendido com desvio aceito: sheet presa embaixo, até ~90% da altura (inclusive a Avançado em paisagem, via `OverlayHostSpec.isTallInLandscape`). Centralização na vertical fica como melhoria futura.

## D-12 Títulos da Paleta e da Fonte dentro da sheet
opções: Reaproveitar "Paleta de cores" (linha) e "Fonte" (Personalizar) | Strings novas
trava: fase 3
resposta: Reaproveitar `settings_appearance_color_palette` e `home_customize_adjust_font` (conservador, sem string nova; decidido na execução).

## D-13 Locals do overlay: allowlist no lint.xml ou parâmetro?
opções: Acrescentar `LocalOverlaySheetMaxWidth` e `LocalOverlayExternalBackdrop` à allowlist do `app/lint.xml` | Passar por parâmetro (`OverlayHostSpec`)
trava: fase 4
resposta: Parâmetro, por não afrouxar regra (Mateus/coordenação, 17/11). `OverlayHostSpec(maxSheetWidth, hasExternalBackdrop)` em `core/designsystem/model/`, com padrão neutro, entra por `AureoleEditOverlayConfig.host`; as sheets filhas das Ajustes recebem `host` e Home/Personalizar seguem iguais.
