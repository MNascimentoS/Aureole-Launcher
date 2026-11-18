# Decisões: editar-foto-fundo

<!-- Todas respondidas em 17/11. Dúvida nova durante a execução: D-06+, opção conservadora. -->

## D-01 "Escala da imagem" depois do editor: manter como está ou o enquadramento manda? (padrão: o enquadramento manda)
opções: O enquadramento manda (Escala só vale sem ajustes salvos) | Manter a Escala e aplicar o enquadramento só em Preencher
trava: fase 1
resposta: O enquadramento manda (17/11 00:05)

## D-02 Desfoque no Android 9–11: gerar cópia desfocada ao salvar ou esconder o desfoque nessas versões? (padrão: gerar cópia)
opções: Gerar cópia desfocada | Esconder a aba Desfoque abaixo do Android 12
trava: fase 1
resposta: Gerar cópia desfocada (17/11 00:05)

## D-03 Girar 90° entra? (padrão: sim)
opções: Sim | Não
trava: fase 2
resposta: Sim, Girar 90° entra (17/11 00:05)

## D-04 Tela de bloqueio recebe escurecer e desfoque, ou só a foto enquadrada? (padrão: igual à home, com escurecer e desfoque)
opções: Igual à home (enquadrar + escurecer + desfoque) | Só a foto enquadrada (sem escurecer e desfoque)
trava: fase 3
resposta: Igual à home (17/11 00:05)

## D-05 O switch "Usar também na tela de bloqueio" começa ligado ou desligado? (padrão: desligado)
opções: Desligado | Ligado
trava: fase 3
resposta: Desligado (17/11 00:05)

## D-06 Foto recém-escolhida e fechada sem mexer: mostra Desfazer?
opções: Sim, "Fundo alterado · Desfazer" (volta a foto anterior) | Não, sem snackbar
trava: fase 2
resposta: Sim (conservador, decidido na execução). O Desfazer da troca, que antes aparecia na hora de escolher a foto, agora espera o editor fechar (senão apareceria por cima do editor). Com mudança: "Foto do fundo ajustada · Desfazer" e volta a foto anterior; sem mudança: "Fundo alterado · Desfazer", também volta a foto anterior. "Editar foto" sem mudança continua sem snackbar (RF-06).

## D-07 "Soltar fora do limite volta com mola" sem nunca mostrar buraco
opções: Arraste e zoom mínimo com limite duro; só a pinça além de 5x estica e volta com mola | Elástico também no arraste e abaixo de 1x (mostra borda vazia por um instante)
trava: fase 2
resposta: Limite duro no arraste e em 1x (RF-04 "sem buraco nunca"); a pinça além de 5x estica com resistência e volta com mola ao soltar; toque duplo, Centralizar e Redefinir andam com mola (conservador, decidido na execução).
