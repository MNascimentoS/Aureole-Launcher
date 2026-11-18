# Decisões: teste-aparelho-17-11

<!-- Padrões abaixo seguem se o Mateus não mudar. Dúvida nova durante a execução: D-03+, opção conservadora. -->

## D-01 Título da sheet da pasta: nome da pasta ou "Configurações da pasta"? (padrão: nome da pasta, com o ícone dela)
opções: Nome da pasta | "Configurações da pasta"
trava: fase 2
resposta: Nome da pasta (padrão; "Configurações da pasta" cortava antes da logo)

## D-02 Quais "+" trocar? (padrão: só os de adicionar componente; ficam os "+" redondos das listas de apps)
opções: Só os de adicionar componente | Todos os "+"
trava: fase 2
resposta: Só os de adicionar componente (padrão)

## D-03 RF-07: "arrastar devolve para a lista" vale para arrastar a sheet para baixo? (padrão: não; só o voltar)
opções: Só voltar do sistema e o botão Voltar | Arrastar para baixo também volta para a lista
trava: fase 2
resposta: (padrão conservador) voltar do sistema e o botão Voltar devolvem à lista; arrastar para baixo continua salvando e fechando o editor do container, como em Paleta/Fonte

## D-04 RF-10: abraçar o conteúdo em todas as sheets com barra de baixo ou só nas de confirmação? (padrão: todas)
opções: Todas (correção no OverlaySheetBody) | Só confirmações curtas
trava: fase 2
resposta: (padrão) todas: a causa é única (weight com fill no corpo da sheet); conferir nos prints da fase 2

## D-05 RF-09 no layout lado a lado (paisagem com prévia ao lado): muda? (padrão: não)
opções: Não muda (a prévia some ao expandir como antes) | Também só some quando precisa
trava: fase 2
resposta: (padrão) não muda; a regra nova vale para a prévia empilhada acima da sheet
