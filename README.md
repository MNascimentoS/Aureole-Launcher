# 💫 Aureole Launcher

Um launcher Android moderno, fluido e altamente personalizável desenvolvido 100% em **Jetpack Compose**. O **Aureole Launcher** entrega uma experiência de tela inicial limpa e de alta performance, contando com estética *glassmorphic* (vidro fosco em tempo real), contêineres de grade dinâmica, painéis laterais customizados, suporte a listas/pilhas de widgets e uma arquitetura reativa sólida baseada em **Clean Architecture**, **MVVM** e **UDF**.

---

## 🌟 Principais Funcionalidades

- 📱 **Engine de Grade Dinâmica**: Grade flexível e totalmente customizável com redimensionamento dinâmico de células (colunas/linhas), suporte a drag-and-drop, contêineres roláveis (Scroll Views) e suporte a layouts retrato e paisagem.
- 🌫️ **Estética Glassmorphic (Efeito Blur em Tempo Real)**: Utiliza a biblioteca [Haze](https://github.com/chrisbanes/haze) e Haze Materials para aplicação de desfoque fluido em tempo real em diálogos, painéis laterais, popups de pasta e na gaveta de aplicativos.
- 🎨 **Design System Aureole Exclusivo**: Tokenização estrita de cores (`AureoleDS.colors`), tamanhos e espaçamentos (`AureoleDS.dimens`), ícones (`AureoleDS.icons`) e tipografia (`AureoleDS.typography`). Conta com suporte a papéis de parede customizados, temas dinâmicos e seletores de cor interativos.
- ⚡ **Containers & Organização Lateral**: Painéis laterais reutilizáveis de acesso rápido com suporte a pastas integradas, ícones customizados via `FolderIconRegistry`, exibições em lista ou grade e suporte a modo canhoto/mão esquerda.
- 🔤 **Gaveta de Apps & Navegação Curva**: Gaveta de aplicativos de alta performance com busca instantânea e barra de depuração alfabética curva com resposta tátil/gestual para navegação rápida entre os apps instalados.
- 🧩 **Sistema de Widgets Avançado**: Host `AppWidgetHost` nativo do Android com suporte a adição de widgets individuais ou pilhas de widgets (Widget Stack Carousel), indicação visual de posição, rotação e remoção intuitiva.
- ⚙️ **Configurações Extensas & Gestos**: Controle refinado de gestos na Home (deslizar para baixo/cima), atalhos de sistema, visibilidade de rótulos de apps e preferências do sistema.
- 🌐 **Internacionalização & Suporte Multilíngue**: Suporte completo a múltiplos idiomas (Inglês padrão e Português do Brasil pt-rBR) em todos os menus, diálogos e componentes com regras estritas de arquitetura impedindo textos *hardcoded*.
- 🔄 **In-App Updates & Telemetria**: Suporte integrado para In-App Updates nativos da Google Play Store (via `PlayAppUpdateManager`) e tratamento de erros via Firebase Crashlytics.

---

## 🛠️ Tecnologias & Stack do Projeto

| Categoria | Tecnologia / Biblioteca |
| :--- | :--- |
| **Linguagem** | Kotlin 2.x (Java 17 target) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) |
| **Arquitetura** | Clean Architecture + MVVM + Unidirectional Data Flow (UDF) |
| **Persistência de Dados** | [Room Database](https://developer.android.com/training/data-storage/room) + KSP |
| **Injeção de Dependências** | [Koin 4.x](https://insert-koin.io/) (`koin-android`, `koin-androidx-compose`) |
| **Efeitos Visuais & Blur** | [Haze](https://github.com/chrisbanes/haze) & Haze Materials |
| **Carregamento de Imagens** | [Coil Compose](https://coil-kt.github.io/coil/) |
| **Análise Estática & Linter** | [Detekt](https://detekt.dev/) (com formatting plugin) + Android Lint |
| **Testes de Unidade & UI** | JUnit 4, Robolectric, Compose UI Test |
| **Testes de Screenshot** | [AndroidX Compose Screenshot Testing](https://developer.android.com/studio/preview/screenshot-testing) |
| **Relatórios de Cobertura** | JaCoCo (`jacocoTestReport`) |
| **Atualizações & Telemetria** | Google Play In-App Updates, Firebase BOM & Crashlytics |

---

## 📐 Regras Arquiteturais & Convenções de Código

O **Aureole Launcher** é governado por regras estritas para garantir que o código seja mantido limpo, testável e previsível:

### 1. Separação de Parâmetros e Proibição de Declarações Inline (`STR-M03`)
- **Proibido Declarar Tipos Inline em View/Screen**: É estritamente proibido criar `data class`, `enum` ou interfaces de parâmetros dentro do arquivo da tela ou Composable principal.
- **Model Subfolder Mandatória**: Todos os parâmetros e modelos de estado da UI devem residir exclusivamente dentro do subpacote `model/` relativo ao componente (ex: `feature/home/components/containers/model/`).

### 2. Design System Tokens & Convenção de Nomenclatura (`STR-M06`, `REQ-01`)
- **Prefixo `Aureole` para Componentes Públicos**: Todos os componentes reutilizáveis do Design System em `core/designsystem/components/` DEVEM possuir o prefixo `Aureole` (ex: `AureoleText`, `AureoleDialog`, `AureoleEditOverlay`, `AureolePopupBox`).
- **Assinatura de Parâmetros**:
  1. `modifier: Modifier = Modifier` deve ser o primeiro parâmetro opcional.
  2. Parâmetros de slots ou lambdas `@Composable` devem ser posicionados no final.
- **Tokens de Dimensão (`AureoleDS.dimens`)**: Proibido utilizar valores numéricos soltos em `dp` ou `sp` (exceto em especificações puras de física de animação). Toda medida de padding, tamanho de ícone, raio de borda ou margin DEVE derivar de `AureoleDS.dimens`.
- **Anotação `@AureolePreview`**: Todos os componentes visuais, diálogos e telas devem incluir previews anotados com `@AureolePreview`.

### 3. Injeção de Estado e Ações via Providers (`LocalHomeUiState`, `LocalHomeActions`)
- Em vez de propagar dezenas de callbacks por múltiplos níveis de composables, a árvore de interface consome estado e ações globais através dos canais `CompositionLocalProvider`:
  - `LocalHomeUiState.current`: Acesso ao estado reativo imutável da tela principal (`MainUiState`).
  - `LocalHomeActions.current`: Acesso ao barramento unificado de ações (`HomeScreenActions`).

---

## 📂 Estrutura do Projeto

```text
dev.mnascimentos.aureole/
├── AureoleApplication.kt          # Classe de aplicação & inicialização do Koin
├── MainActivity.kt                # Single Activity principal & vinculação de ciclo de vida
├── HomeActionsFactory.kt          # Fábrica de manipulação de ações da Home
├── composable/                    # Diálogos e utilitários de interface globais
├── core/
│   ├── data/                      # Entidades Room, DAOs, Database, Receivers & Repositories
│   ├── designsystem/              # Design System: AureoleTheme, tokens (colors, dimens, typography), ícones e componentes
│   └── lifecycle/                 # Observadores de ciclo de vida para AppWidgetHost e In-App Updates
├── di/                            # Módulos Koin para injeção de dependências (AppModule, ViewModelModule)
└── feature/
    ├── home/                      # Feature principal da Home Screen
    │   ├── components/            # Subcomponentes (appsdrawer, clock, containers, favorites, overlays, widgets)
    │   ├── grid/                  # Engine de grade, drag-and-drop e diálogos de edição de contêiner
    │   └── model/                 # Modelos de dados, parâmetros e estados da UI da Home
    └── settings/                  # Feature de Configurações do Launcher
```

---

## 🚀 Guia de Execução & Comandos Gradle

### Pré-requisitos

- **Android Studio:** Ladybug (2024.2.1) ou superior
- **JDK:** Java 17
- **Min SDK:** 28 (Android 9.0)
- **Compile / Target SDK:** 37

### Compilação e Build

- **Compilar APK de Debug:**
  ```bash
  ./gradlew :app:assembleDebug
  ```

- **Compilar APK de Release (com Minificação ProGuard/R8):**
  ```bash
  ./gradlew :app:assembleRelease
  ```

### Qualidade de Código & Análise Estática

- **Executar análise estática com Detekt (com auto-correção):**
  ```bash
  ./gradlew detekt
  ```

- **Executar Android Lint:**
  ```bash
  ./gradlew :app:lintDebug
  ```

### Testes & Relatórios de Cobertura

- **Executar Testes Unitários:**
  ```bash
  ./gradlew :app:testDebugUnitTest
  ```

- **Validar Testes de Screenshot (Screenshot Testing):**
  ```bash
  ./gradlew :app:validateDebugScreenshotTest
  ```

- **Atualizar Imagens de Referência dos Testes de Screenshot:**
  ```bash
  ./gradlew :app:updateDebugScreenshotTest
  ```

- **Gerar Relatório de Cobertura com JaCoCo:**
  ```bash
  ./gradlew :app:jacocoTestReport
  ```
  *(O relatório será gerado em `app/build/reports/jacoco/jacocoTestReport/html/index.html`)*

---

## 📄 Licença

Este projeto está licenciado sob a **PolyForm Noncommercial License 1.0.0** (Source-Available) - consulte o arquivo [LICENSE](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/LICENSE) para detalhes completos.

> **Nota**: Esta licença permite visualização, uso pessoal e modificações não comerciais, mas **proíbe estritamente a comercialização, distribuição lucrativa ou forks não autorizados para fins de monetização**.
