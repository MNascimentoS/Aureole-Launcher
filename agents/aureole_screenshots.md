# 📸 Aureole Screenshots: telas, fluxos e catálogo

Contrato **v1** das imagens de referência. É lido por fora do app (TeuSpecs, TeuApps, android-analyzer): mudar
pasta, nome ou campo do manifesto é mudança de contrato (§6).

## 1. O que tem screenshot
| Tipo | O que é | Quantos |
|---|---|---|
| **Tela** | Tela, sheet, dialog ou popup de nível superior, inteiro, como o usuário vê | até **3 estados** em retrato; paisagem só com `paisagem: true` |
| **Fluxo** | Jornada em passos numerados (`P01`, `P02`…) | 1 PNG por passo novo; passo igual a um estado de tela usa `ref`, sem PNG |
| **Catálogo** | Um grupo de primitivos do DS (texto, controles, menu, dialog/popup, edit overlay, marca) | 1 PNG por grupo, todas as variantes juntas |

- **Não tem screenshot próprio:** componente isolado, seção de sheet, linha, card, item de lista. Ele aparece na
  tela onde é usado; comportamento vai para Compose UI test ou teste JVM.
- Estado novo só quando muda o layout (vazio, erro, canhoto espelhado, conteúdo longo). Texto ou cor diferente no
  mesmo layout não é estado.
- Passar do teto, ou screenshot de componente isolado: decisão `D-xx` na spec, citando esta regra, e
  `"excecao": "<spec> D-xx"` no item do manifesto.

## 2. Pastas e nomes
```text
app/src/screenshotTest/
  screenshots.json
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

## 3. Manifesto `app/src/screenshotTest/screenshots.json`
```json
{
  "contrato": 1,
  "referencias": "app/src/screenshotTestDebug/reference/dev/mnascimentos/aureole",
  "telas": [
    { "id": "home", "area": "home", "titulo": { "pt": "Home", "en": "Home" }, "paisagem": true,
      "estados": [
        { "id": "Padrao", "titulo": { "pt": "Padrão", "en": "Default" },
          "antes": ["feature/home/HomeScreenScreenshotTestKt/HomeScreenDefaultScreenshotTest_Dark Mode"] }
      ] }
  ],
  "fluxos": [
    { "id": "criarpasta", "area": "home", "titulo": { "pt": "Criar pasta", "en": "Create folder" },
      "passos": [
        { "id": "P01SegurarApp", "titulo": { "pt": "Segurar um app", "en": "Long-press an app" } },
        { "id": "P02Formulario", "ref": "telas/pastaformulario/Criar" }
      ] }
  ],
  "catalogo": [ { "id": "controles", "titulo": { "pt": "Controles", "en": "Controls" } } ]
}
```
- `area`: `home`, `ajustes` ou `jogo` (agrupa no mapa de telas).
- `antes`: chaves legadas (caminho sob `referencias`, sem `_<hash>_<n>.png`) que o item substitui. Nunca remover.
- `idsAntigos`: lista de ids anteriores, se um id precisar mudar.
- `ref`: passo sem PNG que mostra um estado de tela.
- `excecao`: decisão que libera o teto ou o componente isolado.

## 4. Guarda (`ScreenshotContractTest`, roda no `testDebugUnitTest`)
Falha quando:
1. existe PNG de referência fora de `telas/`, `fluxos/` ou `catalogo/`;
2. uma pasta de referência não está no manifesto, ou um estado/passo/grupo do manifesto não tem PNG (fora `ref`);
3. tela com mais de 3 estados em retrato sem `excecao`, ou PNG de paisagem sem `paisagem: true`;
4. passos fora de sequência (`P01`, `P02`…), `ref` para estado inexistente, id fora de `[a-z0-9]+`;
5. o manifesto não abre ou `contrato` não é 1.

## 5. Como fazer
- **Tela nova ou mudança visual:** atualizar o estado afetado (ou criar um, dentro do teto) e o manifesto.
- **Componente novo:** sem screenshot próprio; garantir que ele aparece num estado de tela ou no catálogo do grupo.
- **Gerar:** `./gradlew updateDebugScreenshotTest`, depois `./gradlew validateDebugScreenshotTest` e
  `./gradlew testDebugUnitTest --tests "*ScreenshotContractTest*"`.
- **Remover tela/estado:** apagar a função, a pasta de referência (`git rm -r`) e o item do manifesto.

## 6. Leitores do contrato
| Leitor | Usa |
|---|---|
| TeuSpecs | mapa de telas (área → tela → estados, fluxos em ordem) e antes/depois entre `release/x.y.z` pela chave estável e por `antes` |
| TeuApps | o mesmo, lendo o repo público no GitHub |
| android-analyzer (n8n) | `screenshots` no relatório: imagens por tipo, telas, fluxos e resumo do manifesto |

Mudança de contrato: `"contrato": 2`, esta página atualizada e os três leitores ajustados na mesma spec.
