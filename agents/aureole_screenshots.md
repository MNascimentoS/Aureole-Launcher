# 📸 Aureole Screenshots: telas, fluxos e catálogo

Contrato **v2** das imagens de referência. É lido por fora do app (TeuSpecs, TeuApps, android-analyzer): mudar
pasta, nome ou tag do KDoc é mudança de contrato (§6). Desde o v2 não há manifesto nem lista: os PNGs são achados
pelas pastas e o título de cada um vem do KDoc do teste (§3).

## 1. O que tem screenshot
| Tipo | O que é | Quantos |
|---|---|---|
| **Tela** | Tela, sheet, dialog ou popup de nível superior, inteiro, como o usuário vê | até **3 estados** em retrato + o `Padrao` em paisagem (`@paisagem`) |
| **Fluxo** | Jornada em passos numerados (`P01`, `P02`…) | 1 PNG por passo novo; passo igual a um estado de tela vira `@ref` no KDoc do fluxo, sem PNG |
| **Catálogo** | Um grupo de primitivos do DS (texto, controles, menu, dialog/popup, edit overlay, marca) | 1 PNG por grupo, todas as variantes juntas |

- **Não tem screenshot próprio:** componente isolado, seção de sheet, linha, card, item de lista. Ele aparece na
  tela onde é usado; comportamento vai para Compose UI test ou teste JVM.
- **Paisagem:** toda tela tem o `Padrao` também em paisagem (`@AureoleLandscapePreview`, `@paisagem` no KDoc do
  arquivo), mesmo quando o layout deitado é igual (dialog central). Os outros estados ficam só em retrato
  (`screenshots-telas-fluxos` D-19). O caso monta a paisagem por `fixtures/OrientedCase` (já dentro de `HomeCase`,
  `SettingsRootCase` e `SettingsSheetCase`); fluxo e catálogo só têm paisagem marcada com `@paisagem`.
- Estado novo só quando muda o layout (vazio, erro, canhoto espelhado, conteúdo longo). Texto ou cor diferente no
  mesmo layout não é estado.
- Passar do teto, ou screenshot de componente isolado: decisão `D-xx` na spec, citando esta regra, e
  `@excecao <spec> D-xx` no KDoc do arquivo.

## 2. Pastas e nomes
```text
app/src/screenshotTest/
  java/dev/mnascimentos/aureole/
    telas/<tela>/<Tela>TelaScreenshotTest.kt          @PreviewTest ... fun <Estado>ScreenshotTest()
    fluxos/<fluxo>/<Fluxo>FluxoScreenshotTest.kt      @PreviewTest ... fun P<NN><Passo>ScreenshotTest()
    catalogo/<grupo>/<Grupo>CatalogoScreenshotTest.kt @PreviewTest ... fun <Grupo>ScreenshotTest()
    fixtures/                                         estados e dados de exemplo compartilhados (sem @PreviewTest)
```
- `<tela>`, `<fluxo>`, `<grupo>`: id em minúsculas sem separador (`todosapps`, `criarpasta`). **Imutável.**
- `<Estado>`: PascalCase; o primeiro estado de toda tela é `Padrao`. `<Passo>`: PascalCase depois de `P<NN>`, com
  `NN` contínuo a partir de `01`.
- Um arquivo por tela/fluxo/grupo, máx. 800 linhas. Casos montados com `@Composable private fun XCase(...)`
  como em `receitas.md`.

Referência gerada pelo `updateDebugScreenshotTest`:
```text
app/src/screenshotTestDebug/reference/dev/mnascimentos/aureole/<tipo>/<id>/<Classe>Kt/<Função>_<preview>_<hash>_<n>.png
```
**Chave estável** de uma imagem: `<tipo>/<id>/<estado|passo>/<preview>` (ex.: `telas/home/Padrao/Dark Mode`).
A classe `Kt`, o `ScreenshotTest` do fim, o hash e o índice não entram na chave. Leitores pareiam antes/depois
entre releases por essa chave.

## 3. Títulos no KDoc e manifesto
Cada arquivo de teste abre com um KDoc **antes do `package`**: título da tela, fluxo ou grupo e as tags dele. Cada
função `@PreviewTest` tem um KDoc cuja **primeira frase** é o título do estado ou passo; o inglês vem de `@en`.
Descrição, se houver, vai num parágrafo depois do título. Título com até 60 caracteres e sem ponto no meio.
```kotlin
/**
 * Tela de bloqueio.
 *
 * @en Lock screen
 * @area ajustes
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.ajustesbloqueio

/**
 * Ligado.
 *
 * Sheet de fundo com "Usar também na tela de bloqueio" ligado.
 *
 * @en On
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = SettingsSheetCase(...)
```
- `@area`: `home`, `ajustes` ou `jogo` (agrupa no mapa de telas). Obrigatório em tela e fluxo.
- `@paisagem`: a pasta tem PNG em paisagem. `@excecao <spec> D-xx`: decisão que libera o teto ou o componente isolado.
- `@grupo <nome> / <name>` (opcional): nome do grupo de telas quando a pasta dá nome a uma família (`relogio` reúne
  `relogioseletor` e `editarrelogio`) e o título da tela não serve de nome do grupo (`Personalizar relógio`).
- No catálogo, a função `<Grupo>ScreenshotTest` leva o mesmo título do arquivo.
- É o mesmo texto que TeuSpecs e TeuApps mostram; um projeto sem esses KDocs aparece lá com o nome da função.

Passo de fluxo igual a um estado de tela não tem função nem PNG: vira uma linha `@ref` no KDoc do arquivo do fluxo,
`@ref <passo> telas/<id>/<Estado> <título> / <title>`. Fluxo só de `@ref` tem um arquivo só com o KDoc e o `package`.
```kotlin
/**
 * Criar pasta.
 *
 * @en Create folder
 * @area home
 * @ref P01SegurarApp telas/menuapp/Padrao Segurar um app / Long-press an app
 * @ref P02Formulario telas/container/PastaCriar Nome e ícone da pasta / Folder name and icon
 */
package dev.mnascimentos.aureole.fluxos.criarpasta
```
As chaves do contrato v1 (`antes`, `idsAntigos` do antigo `screenshots.json`) ficam no histórico do git (até o
commit que removeu o arquivo); nenhum leitor usa.

## 4. Guarda (`ScreenshotContractTest`, roda no `testDebugUnitTest`)
Lê os PNGs e os arquivos de teste de `telas/`, `fluxos/` e `catalogo/`. Falha quando:
1. existe PNG de referência fora de `telas/`, `fluxos/` ou `catalogo/`;
2. uma pasta com PNG (ou um fluxo só de `@ref`) não tem arquivo de teste com título (primeira frase e `@en`) antes
   do `package`, ou uma função com PNG não tem título no KDoc dela;
3. tela com mais de 3 estados em retrato sem `@excecao`, ou PNG de paisagem sem `@paisagem`;
4. id fora de `[a-z0-9]+`, estado fora de PascalCase, tela sem `Padrao`, tela/fluxo sem `@area`, passos fora de
   sequência (`P01`, `P02`…, contando os `@ref`), `@ref` fora do formato, fora de fluxo ou para estado sem PNG.

## 5. Como fazer
- **Tela nova ou mudança visual:** atualizar o estado afetado (ou criar um, dentro do teto) com o KDoc de título.
  Não há arquivo a mais para atualizar.
- **Componente novo:** sem screenshot próprio; garantir que ele aparece num estado de tela ou no catálogo do grupo.
- **Gerar:** `./gradlew updateDebugScreenshotTest`, depois `./gradlew validateDebugScreenshotTest` e
  `./gradlew testDebugUnitTest --tests "*ScreenshotContractTest*"`.
- **Remover tela/estado:** apagar a função e a pasta de referência (`git rm -r`) e os `@ref` que apontavam para ela.

## 6. Leitores do contrato
| Leitor | Usa |
|---|---|
| TeuSpecs | mapa de telas com os títulos do KDoc (pt/en) e antes/depois entre `release/x.y.z` pela chave estável |
| TeuApps | o mesmo, lendo os PNGs e os `.kt` do repo público no GitHub |
| android-analyzer (n8n) | `screenshots` no relatório: imagens por tipo, telas, fluxos e resumo do manifesto |

Mudança de contrato: versão + 1 no topo desta página e os três leitores ajustados na mesma spec.
O android-analyzer ainda lê o `screenshots.json` do v1 (resumo de telas e fluxos): precisa passar a contar pelos PNGs
e pelos KDocs.
