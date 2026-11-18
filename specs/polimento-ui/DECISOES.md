# Decisões: polimento-ui

<!-- Todas respondidas em 17/11. Dúvida nova durante a execução: D-11+, opção conservadora. -->

## D-04 Quem já usa uma cor das Vivas: mantém?
opções: Mantém | Troca para a cor do tema
trava: fase 1
resposta: Troca para a cor do tema, o Fundo da paleta atual (migração única) (Mateus, 17/11)

## D-05 Logo sai de todas as sheets dos Ajustes ou só do Fundo?
opções: Todas as sheets dos Ajustes | Só Definir plano de fundo
trava: fase 1
resposta: Todas as sheets dos Ajustes (Mateus, 17/11)

## D-06 Indicador global desligado: o que acontece com os grupos?
opções: Copia o desligado para cada grupo | Ignora o global (todos ligados)
trava: fase 1
resposta: Cada grupo fica desligado (migração única) (Mateus, 17/11)

## D-07 Regra da cor do título
opções: Medium sem subtítulo, High com | Todos High | Todos Medium
trava: fase 1
resposta: Uma cor só, onSurfaceMedium, em todo título de linha (Mateus, 17/11 10:46: cor de "Personalizar home" diferente das outras)

## D-08 Linha destrutiva: sem quadrado ou quadrado de 28 dp?
opções: A · Sem quadrado | B · Quadrado de 28 dp
trava: fase 1
resposta: A · Sem quadrado (Mateus, 17/11 10:09)

## D-09 Animação da logo do cabeçalho das sheets
opções: Anel fecha (AureoleLogoRing) + encolher | Volta do halo | Animar enquanto pressiona
trava: fase 2
resposta: Animar enquanto pressiona, sem anel (Mateus, 17/11 10:23): encolhe e inclina com o dedo, volta com mola ao soltar e abre os Ajustes.

## D-10 Anel da logo: só fase 1 ou também fundo/listas agora? (padrão: só fase 1)
opções: Só fase 1 (raiz dos Ajustes, fim do onboarding, checklist) | Fase 1 e 2
trava: fase 2
resposta: Só fase 1: vitrine e espiada do onboarding, raiz dos Ajustes, fim do onboarding, checklist (Mateus, 17/11)

## D-11 Cor das Vivas salva: "Fundo da paleta atual" é qual?
Uma cor sólida salva vira o tema "Custom" semeado por ela, e o Fundo do Custom é a própria cor; o nome da paleta de antes não fica guardado. Na fase 2 a migração foi para a paleta padrão (Frostbite), o mesmo estado de uma instalação nova.
opções: Paleta padrão (Frostbite) | Custom com o Fundo do Frostbite como semente
trava: fase 2
resposta:

## D-12 Arrastar para baixo / tocar fora na Paleta e na Fonte: fecha a sheet ou volta para Aparência?
O RF-01 diz que no Definir plano de fundo o arrastar devolve para Aparência "como na Paleta e na Fonte", mas hoje Paleta e Fonte fecham a sheet inteira (salvando) ao arrastar ou tocar fora. Na fase 2 só o Fundo volta para Aparência; Paleta e Fonte ficaram como estão.
opções: Só o Fundo volta para Aparência | Paleta e Fonte também voltam
trava: fase 2
resposta:

## D-13 Quais sheets são "editores de item" sem logo (RF-05b)?
Na fase 3 a logo saiu do menu da pasta, do formulário da pasta, do Escolher apps da pasta, do menu do botão rápido, da Aparência do botão rápido e do Escolher atalho. Menus do app, relógio, container e pilha de widgets mantêm a logo.
opções: Assim (pasta e botão rápido inteiros) | Só os formulários (nome/ícone e aparência)
trava: fase 3
resposta:

## D-14 Onde fica o AureoleLogoRing?
O SPEC/PLANO pedem `core/designsystem/components` (junto do antigo `AureoleHaloProgress`); o pedido da execução falava em `composable/`. Ficou em `core/designsystem/components`, público, para a `ajustes-onboarding` usar.
opções: core/designsystem/components | composable/
trava: fase 3
resposta:


## D-15 A logo da raiz dos Ajustes usa o anel de progresso?
Pedido do dono na fase G: não. A logo grande da raiz dos Ajustes volta a ficar parada, sem anel e sem animação. Removidos SettingsUpdateRingObserver, o AppUpdateManager da SettingsActivity, SettingsUiState.updateRing, SettingsUpdateRing, strings settings_root_update_* e SettingsUpdateRingTest. AureoleLogoRing e o uso no onboarding ficam.
opções: Sem anel | Com anel
trava: fase G
resposta: Sem anel (pedido do dono)
