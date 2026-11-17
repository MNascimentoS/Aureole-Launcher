# Ajustes: propostas de reorganização (fase 5 da `ajustes-sheets-settings`)

Exploração sem código. Base: telas de Ajustes depois das fases 1 a 4 (código em
`app/src/main/java/.../feature/settings/screens/` e referências em
`app/src/screenshotTestDebug/reference/.../feature/settings/`), o Personalizar, a sheet do Botão rápido e os menus
(`AureoleMenuListSheet`, `AureoleMenuRow`, `AureoleSwitchRow`). Mockups em `PROPOSTAS-IA.html`, no mesmo estilo do
`prototipo.html`.

## Como está hoje (o que eu vi)

- **Raiz:** logo grande, "Aureole", versão (toque abre as notas), três linhas com ícone e subtítulo (Aparência,
  Comportamento, Avançado), "Muito obrigado!" preso embaixo. Em paisagem, dois painéis (marca à esquerda, linhas à
  direita). Metade de baixo da tela fica vazia.
- **Aparência:** Definir plano de fundo · Efeito de vidro (valor "Médio") · Alterar fonte · Paleta de cores · Cores
  personalizadas (bolinha da cor) · Alterar borda ("20 dp") · Ícones temáticos (switch). Restaurar fundo e Escala da
  imagem só aparecem quando há papel de parede próprio. Efeito de vidro e Alterar borda abrem a **mesma** sheet
  Cartões e vidro.
- **Comportamento:** Deslocamento do cabeçalho (roda 9/10/11 %) · Home abre todos os apps · Todos os apps do lado
  esquerdo · Exibir indicador de widgets · Desativar rolagem alfabética · Verificar atualizações (switch).
- **Avançado:** Informações do app · Novidades · Launcher padrão · Redefinir launcher · Desinstalar launcher; depois,
  em texto mais claro, Colaboradores · Política de privacidade · Termos de serviço · Licenças de código aberto.
- **O resto do app** já fala outra língua: seções em versalete (AJUSTAR, ADICIONAR À TELA INICIAL, BOTÃO), tiles, linhas
  com coluna de ícone e chevron, subtítulo com o valor atual ("Atalho do clique · Todos os apps"), ação destrutiva no
  fim depois de um divisor, em vermelho, com segurar.

Observações que pesam na reorganização:

1. O título "Comportamento" esconde coisas de lugares diferentes: dois itens são da **lista de apps** (rolagem
   alfabética, deslocamento do cabeçalho, que também posiciona o topo dos Ajustes), um é do **botão Início**, um da
   **tela inicial** (indicador de widgets), um é a **mão** do app inteiro (canhoto) e um é de **sistema** (atualizações).
2. Paleta e Fonte já existem no Personalizar (AJUSTAR: Layout, Paleta, Fonte). Ou seja, hoje a aparência está
   partida entre duas portas.
3. Destrutivos (Redefinir, Desinstalar) estão no meio do Avançado, sem divisor nem vermelho, ao contrário da regra
   dos menus (§6 de `aureole_components.md`).
4. Os quatro links legais estão a 2 toques e parecem desabilitados (cor baixa, sem ícone nem chevron).

## Opinião sobre as 3 direções do protótipo

| Direção | O que eu aproveitaria | O que me preocupa |
|---|---|---|
| **A · Home viva** | A mini-home como prévia; a grade de tiles idêntica ao Personalizar; "Zona de cuidado" junta no fim. | Uma rolagem de ~28 itens: o canhoto e o "Verificar atualizações" ficam a 2–3 telas de rolagem. A marca vira cabeçalho pequeno e a raiz perde o "Muito obrigado!" como fecho visível. Mini-home é um componente novo a manter em sincronia com a home real. |
| **B · Cartões com resumo** | Valor atual no próprio item (já é o padrão da sheet do Botão rápido); prévia no topo de Aparência; grupos com rótulo nas subtelas. | Os chips do cartão quebram com fonte do sistema no máximo e em pt-BR (textos longos); continua com 2 toques para tudo, e a divisão Comportamento/Avançado segue a de hoje (o problema 1 acima continua). |
| **C · No polegar** | Conteúdo ancorado embaixo e busca "Buscar ajuste". | Abas embaixo competem com a barra de gestos e com o "voltar"; busca para 28 itens custa índice, strings de sinônimo e testes, e só paga se a lista crescer muito. O logo encolhido perde o caráter da raiz. |

Resumo: de A, a prévia e os tiles; de B, o valor atual como subtítulo; de C, o conteúdo perto do polegar. As
propostas abaixo usam essas peças, mas mudam **o critério de agrupamento** (A, B e C mantêm as três áreas de hoje).

---

## Proposta D · Por onde muda

**Ideia.** Agrupar pelo lugar do launcher que o ajuste afeta, não pelo tipo de ajuste: Tema, Fundo e cartões, Tela
inicial e lista, Sistema. A raiz mostra essas quatro portas como linhas de menu (ícone, título, subtítulo com o valor
atual) e traz o "Sobre" (legais + colaboradores) para a própria raiz, com o "Muito obrigado!" no fecho. É a mudança mais
barata e a que mais conserta o problema do "Comportamento".

**Árvore**

```
Raiz
├─ [cabeçalho] logo · Aureole · versão (toque → notas da versão)
├─ AJUSTES
│  ├─ Tema ›                 "Frostbite · Istok Web"
│  │   ├─ CORES: Paleta de cores · Cores personalizadas
│  │   └─ ÍCONES E LETRA: Alterar fonte · Ícones temáticos
│  ├─ Fundo e cartões ›       "Cor sólida · vidro médio"
│  │   ├─ FUNDO: Definir plano de fundo · Escala da imagem* · Restaurar fundo*
│  │   └─ CARTÕES E VIDRO: Efeito de vidro · Alterar borda (raio dp)
│  ├─ Tela inicial e lista ›  "Destro · Início abre a lista"
│  │   ├─ MÃO: Todos os apps do lado esquerdo
│  │   ├─ TELA INICIAL: Home abre todos os apps · Exibir indicador de widgets
│  │   └─ LISTA DE APPS: Desativar rolagem alfabética · Deslocamento do cabeçalho
│  └─ Sistema ›               "Launcher padrão ✓ · atualizações ligadas"
│      ├─ ESTE APARELHO: Launcher padrão · Verificar atualizações · Informações do app
│      ├─ VERSÃO: Novidades
│      └─ ─── divisor ─── Redefinir launcher (vermelho, segurar) · Desinstalar launcher (vermelho, segurar)
├─ SOBRE
│  ├─ Colaboradores › (tela atual)
│  ├─ Política de privacidade ↗ · Termos de serviço ↗
│  └─ Licenças de código aberto › (tela atual)
└─ Muito obrigado!
(* só com papel de parede próprio, como hoje)
```

**Item de hoje → onde fica**

| Item de hoje | Onde fica em D |
|---|---|
| Logo, nome, versão → notas | Cabeçalho da raiz (tamanho médio), toque abre as notas |
| Aparência / Comportamento / Avançado (entradas) | Viram Tema, Fundo e cartões, Tela inicial e lista, Sistema |
| Muito obrigado! | Fim da raiz |
| Paleta de cores | Tema › Cores |
| Cores personalizadas | Tema › Cores |
| Alterar fonte | Tema › Ícones e letra |
| Ícones temáticos | Tema › Ícones e letra |
| Definir plano de fundo | Fundo e cartões › Fundo |
| Restaurar fundo | Fundo e cartões › Fundo |
| Escala da imagem | Fundo e cartões › Fundo |
| Efeito de vidro | Fundo e cartões › Cartões e vidro |
| Alterar borda (raio dp) | Fundo e cartões › Cartões e vidro |
| Home abre todos os apps | Tela inicial e lista › Tela inicial |
| Todos os apps do lado esquerdo | Tela inicial e lista › Mão (primeiro item) |
| Desativar rolagem alfabética | Tela inicial e lista › Lista de apps |
| Exibir indicador de widgets | Tela inicial e lista › Tela inicial |
| Deslocamento do cabeçalho | Tela inicial e lista › Lista de apps |
| Verificar atualizações | Sistema › Este aparelho |
| Informações do app | Sistema › Este aparelho |
| Novidades | Sistema › Versão (e continua no toque da versão) |
| Launcher padrão | Sistema › Este aparelho |
| Redefinir launcher | Sistema, depois do divisor |
| Desinstalar launcher | Sistema, depois do divisor |
| Colaboradores | Raiz › Sobre |
| Política de privacidade | Raiz › Sobre |
| Termos de serviço | Raiz › Sobre |
| Licenças de código aberto | Raiz › Sobre |

**Melhora**
- Descoberta: o nome diz onde o efeito aparece ("rolagem alfabética" mora com a lista; "atualizações" com o sistema).
- Legais e Colaboradores: de 2 toques para 1, com cara de link de verdade.
- Destrutivos seguem a regra dos menus (fim, divisor, vermelho, segurar).
- Valor atual no subtítulo (padrão do Botão rápido) evita entrar só para conferir.

**Piora**
- Cliques: iguais aos de hoje para quase tudo (2 toques); uma porta a mais na raiz (4 em vez de 3).
- "Tela inicial e lista" mistura três assuntos pequenos; precisa de rótulos de seção para não virar o Comportamento de
  novo.
- Uma mão: igual a hoje (raiz no meio da tela); a raiz fica mais alta e a seção Sobre pode exigir rolar em telas
  pequenas.

**Custo:** **baixo a médio** (~1 fase). Reaproveita as telas atuais: renomear e redistribuir linhas entre
`SettingsAppearanceScreen`/`SettingsBehaviorScreen`/`SettingsAdvancedScreen` (vira uma tela a mais), rotas no
`SettingsRoute`, ~12 strings novas (en + pt-BR), rótulo de seção e divisor já existem nos menus. Subtítulos com valor
atual pedem funções puras no `model/` (testáveis em JVM). Referências de screenshot da raiz e das 4 subtelas, retrato e
paisagem, regravadas.

---

## Proposta E · Ajustes é uma sheet

**Ideia.** Os Ajustes deixam de ser telas cheias e passam a ser uma sheet alta sobre a home, como o Personalizar e o
Botão rápido: a home real fica atrás e **é a prévia viva** (troca a paleta, a fonte ou o vidro e a mudança aparece
atrás da sheet, sem mini-home falsa). Os toggles ficam direto na sheet raiz, sem subtela; o que já é sheet (Paleta,
Fonte, Fundo, Cartões e vidro) troca o conteúdo da sheet, como "Botão rápido › Aparência" faz hoje.

**Árvore**

```
Sheet "Ajustes" (sobe de baixo, estica até o conteúdo; marca no topo da sheet)
├─ [cabeçalho] logo pequeno · Aureole · versão (toque → notas)
├─ APARÊNCIA  (tiles, como AJUSTAR do Personalizar)
│  ├─ Paleta (tile) → sheet Paleta  ·  Fonte (tile) → sheet Fonte
│  ├─ Fundo (tile) → sheet Fundo   ·  Vidro (tile) → sheet Cartões e vidro (Efeito de vidro · Alterar borda)
│  └─ linhas: Cores personalizadas › · Ícones temáticos (switch)
│            · Escala da imagem* (valor) · Restaurar fundo*
├─ USO  (switches direto na sheet)
│  ├─ Todos os apps do lado esquerdo · Home abre todos os apps
│  ├─ Desativar rolagem alfabética · Exibir indicador de widgets
│  └─ Deslocamento do cabeçalho (roda, como hoje)
├─ SISTEMA
│  ├─ Launcher padrão · Verificar atualizações (switch)
│  ├─ Informações do app ↗ · Novidades ›
├─ SOBRE
│  └─ Colaboradores › · Política de privacidade ↗ · Termos de serviço ↗ · Licenças de código aberto ›
├─ ─── divisor ───
│  ├─ Redefinir launcher (vermelho, segurar)
│  └─ Desinstalar launcher (vermelho, segurar)
└─ Muito obrigado!  (fecho da rolagem)
Colaboradores e Licenças continuam telas cheias (abrem por cima).
(* só com papel de parede próprio)
```

**Item de hoje → onde fica**

| Item de hoje | Onde fica em E |
|---|---|
| Logo, nome, versão → notas | Cabeçalho da sheet, toque abre as notas |
| Aparência / Comportamento / Avançado (entradas) | Viram seções da sheet: Aparência, Uso, Sistema (+ Sobre) |
| Muito obrigado! | Fecho da rolagem da sheet |
| Paleta de cores | Tile Paleta |
| Cores personalizadas | Linha em Aparência |
| Alterar fonte | Tile Fonte |
| Ícones temáticos | Linha (switch) em Aparência |
| Definir plano de fundo | Tile Fundo |
| Restaurar fundo | Linha em Aparência (condicional) |
| Escala da imagem | Linha em Aparência (condicional) |
| Efeito de vidro | Tile Vidro → sheet Cartões e vidro |
| Alterar borda (raio dp) | Tile Vidro → sheet Cartões e vidro (e subtítulo do tile mostra "20 dp") |
| Home abre todos os apps | Uso |
| Todos os apps do lado esquerdo | Uso (primeiro) |
| Desativar rolagem alfabética | Uso |
| Exibir indicador de widgets | Uso |
| Deslocamento do cabeçalho | Uso (roda) |
| Verificar atualizações | Sistema |
| Informações do app | Sistema |
| Novidades | Sistema (e toque da versão) |
| Launcher padrão | Sistema |
| Redefinir launcher | Depois do divisor, no fim |
| Desinstalar launcher | Depois do divisor, no fim |
| Colaboradores | Sobre |
| Política de privacidade | Sobre |
| Termos de serviço | Sobre |
| Licenças de código aberto | Sobre |

**Melhora**
- Cliques: todo switch a **1 toque** depois de abrir (hoje 2); Paleta/Fonte/Fundo/Vidro também a 1.
- Prévia viva de verdade, sem mini-home para manter.
- Uma mão: a sheet sobe de baixo, mesmo lado do polegar que o resto do app; fecha por arrastar, toque fora e voltar.
- Consistência total com Personalizar e Botão rápido (mesmos componentes, mesmas animações da fase 3).

**Piora**
- Descoberta: uma rolagem longa (~28 itens); Sobre e destrutivos ficam bem no fim.
- A sheet passa dos 60% da regra dos menus: precisa da mesma exceção do Personalizar (sobe e estica).
- Deslocamento do cabeçalho mexe no topo da lista de apps, que não está visível atrás da sheet; a prévia não ajuda
  esse item.
- Paisagem: some o layout de dois painéis; a sheet larga precisa de largura máxima.
- Entrada pelo ícone do app "Configurações" / atalho do sistema: precisa abrir a home com a sheet aberta.

**Custo:** **alto** (~2–3 fases). O `SettingsActivity` vira overlay da home (ou atividade translúcida com a home atrás),
a navegação de `SettingsRoute` vira estado de sheet com conteúdo trocável, o `SettingsViewModel` precisa conversar com o
estado da home ao vivo, e todos os screenshots de Ajustes mudam. Risco de regressão no voltar e no entrar pelo atalho
de sistema.

---

## Proposta F · Aparência mora no Personalizar

**Ideia.** Separar por intenção: tudo que muda **a cara** da home vai para o Personalizar (que já tem Paleta e Fonte
e já é aberto da própria home), numa seção APARÊNCIA; os Ajustes ficam com o que muda **o funcionamento** e o que é do
**app**, numa raiz única sem subtelas. A raiz dos Ajustes mantém uma linha "Aparência ›" que abre o Personalizar,
para quem procurar lá.

**Árvore**

```
Personalizar (sheet de hoje)
├─ AJUSTAR: Layout · Paleta · Fonte                       (como hoje)
├─ APARÊNCIA (nova seção, linhas com ícone e valor)
│  ├─ Plano de fundo ›         "Cor sólida"   → sheet Fundo
│  │     (Escala da imagem* e Restaurar fundo* como linhas logo abaixo)
│  ├─ Cartões e vidro ›        "Médio · 20 dp" → sheet (Efeito de vidro · Alterar borda)
│  ├─ Cores personalizadas ›   (bolinha da cor)
│  └─ Ícones temáticos (switch)
└─ ADICIONAR À TELA INICIAL: (como hoje, inclui o tile Configurações)

Ajustes (uma tela, sem subtelas de categoria)
├─ [cabeçalho] logo · Aureole · versão (toque → notas)
├─ Aparência ›  "Paleta, fonte, fundo e vidro ficam no Personalizar" → abre o Personalizar
├─ MÃO E GESTOS: Todos os apps do lado esquerdo · Home abre todos os apps
├─ LISTA DE APPS: Desativar rolagem alfabética · Deslocamento do cabeçalho (roda)
├─ TELA INICIAL: Exibir indicador de widgets
├─ SISTEMA: Launcher padrão · Verificar atualizações · Informações do app · Novidades
├─ SOBRE: Colaboradores › · Privacidade ↗ · Termos ↗ · Licenças ›
├─ ─── divisor ─── Redefinir launcher · Desinstalar launcher (vermelho, segurar)
└─ Muito obrigado!
```

**Item de hoje → onde fica**

| Item de hoje | Onde fica em F |
|---|---|
| Logo, nome, versão → notas | Cabeçalho dos Ajustes |
| Aparência / Comportamento / Avançado (entradas) | Aparência vira atalho para o Personalizar; Comportamento e Avançado viram seções da raiz |
| Muito obrigado! | Fim dos Ajustes |
| Paleta de cores | Personalizar › Ajustar (já está lá) |
| Cores personalizadas | Personalizar › Aparência |
| Alterar fonte | Personalizar › Ajustar (já está lá) |
| Ícones temáticos | Personalizar › Aparência |
| Definir plano de fundo | Personalizar › Aparência |
| Restaurar fundo | Personalizar › Aparência (condicional) |
| Escala da imagem | Personalizar › Aparência (condicional) |
| Efeito de vidro | Personalizar › Aparência › Cartões e vidro |
| Alterar borda (raio dp) | Personalizar › Aparência › Cartões e vidro |
| Home abre todos os apps | Ajustes › Mão e gestos |
| Todos os apps do lado esquerdo | Ajustes › Mão e gestos |
| Desativar rolagem alfabética | Ajustes › Lista de apps |
| Exibir indicador de widgets | Ajustes › Tela inicial |
| Deslocamento do cabeçalho | Ajustes › Lista de apps |
| Verificar atualizações | Ajustes › Sistema |
| Informações do app | Ajustes › Sistema |
| Novidades | Ajustes › Sistema (e toque da versão) |
| Launcher padrão | Ajustes › Sistema |
| Redefinir launcher | Ajustes, depois do divisor |
| Desinstalar launcher | Ajustes, depois do divisor |
| Colaboradores | Ajustes › Sobre |
| Política de privacidade | Ajustes › Sobre |
| Termos de serviço | Ajustes › Sobre |
| Licenças de código aberto | Ajustes › Sobre |

**Melhora**
- Acaba a aparência em duas portas: quem segura a home para personalizar acha fundo e vidro ali, com a home de
  verdade atrás como prévia. Fundo a partir da home: hoje 3 toques (segurar → Configurações → Aparência → Fundo),
  em F 2 (segurar → Fundo).
- Ajustes sem subtelas: todo switch a 1 toque da raiz (hoje 2).
- Uma mão: o Personalizar já é sheet embaixo; os Ajustes ficam curtos (~20 linhas).

**Piora**
- Descoberta para quem só conhece "Configurações": precisa da linha "Aparência ›" (um toque a mais e uma troca de
  contexto, sai dos Ajustes e vai para a home).
- O Personalizar cresce (uma seção a mais): fica mais alto e precisa rolar em telas pequenas; mexe numa sheet que
  acabou de ser ajustada nas fases 1–4.
- A sheet do Botão rápido "Aparência" e a seção APARÊNCIA do Personalizar ficam com o mesmo nome para coisas
  diferentes; pede outro nome ("Visual da home"?).

**Custo:** **médio** (~2 fases). Fase 1: Ajustes viram uma tela com seções (junta Comportamento e Avançado, tira as
rotas de categoria; Colaboradores e Licenças continuam). Fase 2: seção nova no Personalizar reaproveitando as sheets de
Fundo, Cartões e vidro e Cores que já existem (hoje abertas a partir dos Ajustes, então o estado precisa estar
disponível no `HomeViewModel` ou num repositório compartilhado). Screenshots do Personalizar (destro, canhoto, raio 0)
e de toda a área de Ajustes regravados.

---

## Comparação rápida

| | Hoje | D · Por onde muda | E · Ajustes é sheet | F · Aparência no Personalizar |
|---|---|---|---|---|
| Toques até um switch (da raiz) | 2 | 2 | 1 | 1 |
| Toques até legais | 2 | 1 | 1 (rolando) | 1 (rolando) |
| Fundo a partir da home | 3 | 3 | 2 | 2 |
| Prévia viva | não | não | home real atrás | home real atrás (no Personalizar) |
| Uma mão | meio da tela | meio da tela | embaixo | embaixo (aparência) / meio (Ajustes) |
| Custo | — | baixo/médio | alto | médio |

**Minha recomendação:** começar por **D** (barato, conserta o agrupamento e a zona de cuidado, e não fecha portas) e,
se o Mateus gostar da ideia de a aparência morar onde se mexe na home, evoluir para **F**. E é a mais bonita, mas o
custo e o risco (atividade virando overlay, entrada pelo atalho de sistema, paisagem) não compensam agora.

---

## Checklist do inventário (reorganizar sem remover)

Cada item do inventário de `IDEIA.md` aparece em cada proposta.

| # | Item | D | E | F |
|---|---|---|---|---|
| 1 | Logo + "Aureole" + versão (toque abre as notas) | ✅ cabeçalho da raiz | ✅ cabeçalho da sheet | ✅ cabeçalho dos Ajustes |
| 2 | Aparência (entrada) | ✅ Tema + Fundo e cartões | ✅ seção Aparência | ✅ linha "Aparência ›" + Personalizar |
| 3 | Comportamento (entrada) | ✅ Tela inicial e lista | ✅ seção Uso | ✅ seções Mão / Lista / Tela inicial |
| 4 | Avançado (entrada) | ✅ Sistema + Sobre | ✅ seções Sistema + Sobre | ✅ seções Sistema + Sobre |
| 5 | "Muito obrigado!" | ✅ | ✅ | ✅ |
| 6 | Paleta de cores | ✅ | ✅ | ✅ |
| 7 | Cores personalizadas (cor base) | ✅ | ✅ | ✅ |
| 8 | Alterar fonte | ✅ | ✅ | ✅ |
| 9 | Definir plano de fundo | ✅ | ✅ | ✅ |
| 10 | Restaurar fundo | ✅ | ✅ | ✅ |
| 11 | Escala da imagem | ✅ | ✅ | ✅ |
| 12 | Ícones temáticos | ✅ | ✅ | ✅ |
| 13 | Cartões e vidro: Alterar borda (raio dp) | ✅ | ✅ | ✅ |
| 14 | Cartões e vidro: Efeito de vidro | ✅ | ✅ | ✅ |
| 15 | Home abre todos os apps | ✅ | ✅ | ✅ |
| 16 | Todos os apps do lado esquerdo (canhoto) | ✅ | ✅ | ✅ |
| 17 | Desativar rolagem alfabética | ✅ | ✅ | ✅ |
| 18 | Exibir indicador de widgets | ✅ | ✅ | ✅ |
| 19 | Deslocamento do cabeçalho | ✅ | ✅ | ✅ |
| 20 | Verificar atualizações | ✅ | ✅ | ✅ |
| 21 | Informações do app | ✅ | ✅ | ✅ |
| 22 | Novidades | ✅ | ✅ | ✅ |
| 23 | Launcher padrão | ✅ | ✅ | ✅ |
| 24 | Redefinir launcher | ✅ | ✅ | ✅ |
| 25 | Desinstalar launcher | ✅ | ✅ | ✅ |
| 26 | Colaboradores | ✅ | ✅ | ✅ |
| 27 | Política de privacidade | ✅ | ✅ | ✅ |
| 28 | Termos de serviço | ✅ | ✅ | ✅ |
| 29 | Licenças de código aberto | ✅ | ✅ | ✅ |

Nenhum item foi removido; o comportamento de cada um (sheet que abre, switch, roda, segurar para confirmar, condição
de papel de parede próprio) fica como hoje. As telas de Colaboradores e Licenças continuam como estão nas três.
