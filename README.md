# 💫 Aureole Launcher

Um launcher Android moderno, fluido e personalizável desenvolvido 100% em **Jetpack Compose**. O **Aureole Launcher** entrega uma experiência de tela inicial limpa e de alta performance, contando com design vidro fosco (glassmorphic), contêineres de grade dinâmica, painéis laterais customizados e integração nativa com widgets.

---

## 🌟 Principais Funcionalidades

- 📱 **Engine de Grade Dinâmica**: Grade flexível e livre com suporte a número personalizado de colunas e linhas, layouts responsivos para orientações retrato e paisagem, e reposicionamento por arrastar e soltar.
- 🌫️ **Estética Glassmorphic**: Utiliza a biblioteca [Haze](https://github.com/chrisbanes/haze) para efeito de desfoque (blur) em tempo real em diálogos, painéis laterais, pastas e na gaveta de apps.
- 🎨 **Design System Aureole**: Engine de temas integrada com paletas de cores customizadas (ex: *Frostbite*), suporte a papéis de parede personalizados, coloração dinâmica das barras de sistema e seletores de cores.
- ⚡ **Painéis Laterais & Organização**: Painéis laterais de acesso rápido com pastas integradas, ícones de pasta personalizados, exibição em lista ou grade, e posicionamento flexível à esquerda ou direita.
- 🔤 **Gaveta de Apps & Seletor Curvo**: Gaveta de aplicativos rápida com busca instantânea e barra alfabética curva para navegação ágil entre os aplicativos instalados.
- 🧩 **Sistema de Widgets**: Host `AppWidget` nativo do Android totalmente integrado, com seletor de widgets, redimensionamento, suporte a pilha de widgets e menus pop-up.
- ⚙️ **Configurações Extensas**: Personalização detalhada sobre comportamento, aparência, gestos, listas de apps e papéis de parede dinâmicos.
- 🔄 **In-App Updates**: Suporte integrado para In-App Updates da Google Play e integração com Firebase Crashlytics.

---

## 🛠️ Tecnologias & Bibliotecas

- **Linguagem:** Kotlin 2.x
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arquitetura:** Clean Architecture + MVVM + Fluxo Unidirecional de Dados (UDF)
- **Banco de Dados / Persistência:** [Room](https://developer.android.com/training/data-storage/room) & Helpers SQLite
- **Programação Assíncrona:** Kotlin Coroutines & `StateFlow` / `SharedFlow`
- **Efeitos Visuais & Blur:** [Haze](https://github.com/chrisbanes/haze) & Haze Materials
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)
- **Injeção de Dependência:** [Koin](https://insert-koin.io/) (v4.x)
- **Qualidade de Código & Análise Estática:** [Detekt](https://detekt.dev/)
- **Testes:** JUnit, Robolectric, Compose UI Test, Compose Screenshot Testing, JaCoCo
- **CI/CD:** GitHub Actions & Fastlane (Distribuição Interna na Google Play)

---

## 📐 Regras & Diretrizes do Projeto

O **Aureole Launcher** segue regras arquiteturais rigorosas e padronizadas para manter o código escalável, testável e manutenível:

### 1. Arquitetura & Fluxo Unidirecional de Dados (UDF)
- **MVVM + Clean Architecture**: Separação estrita de responsabilidades entre `Activity`, `Screen/View`, `ViewModel`, `Repository` e `Data Layer`.
- **Proibição de Declarações Inline em Views (`STR-M03`)**: É **estritamente proibido** declarar `data class`, `enum` ou interfaces de parâmetros dentro de arquivos de tela/diálogo/view. Todos os modelos de dados e parâmetros devem residir na subpasta `model/` do componente.
- **Organização de Extensões de ViewModel**: Métodos de extensão de ViewModel devem residir nas subpastas `extensions/` de cada componente específico (`components/<componente>/extensions/`).
- **Injeção de Ações via Providers**: Ações da UI e estados de tela são disponibilizados na árvore de Composables através de `CompositionLocalProvider` (`LocalHomeUiState`, `LocalHomeActions`).

### 2. Design System & Componentes (`STR-M06`)
- **Nomenclatura Padronizada**: Todos os componentes reutilizáveis do Design System em `core/designsystem/components/` devem utilizar o prefixo `Aureole` (ex: `AureoleText`, `AureoleDialog`, `AureoleEditOverlay`).
- **Documentação KDoc**: Qualquer componente do Design System deve obrigatoriamente possuir documentação KDoc com descrição do componente e marcações `@param` para seus parâmetros.
- **Convenção de Assinatura de Parâmetros**:
  - `modifier: Modifier = Modifier` deve ser posicionado como o **primeiro parâmetro opcional**.
  - Lambdas Composables / Slots de conteúdo devem ser posicionados como o **último parâmetro**.
- **Design Tokens**: Proibido utilizar cores hexadecimais brutos ou dimensões numéricas soltas. Utilize sempre `AureoleDS.colors`, `AureoleDS.dimens` e `AureoleDS.icons`.
- **Previews de Componentes**: Todo componente, diálogo ou bottom sheet deve conter anotações `@AureolePreview` diretamente no próprio arquivo de declaração.

### 3. Injeção de Dependências & Ciclo de Vida
- **Koin (v4)**: Repositórios e fontes de dados registrados como `single` em `di/AppModule.kt`. ViewModels registrados via `viewModel` e injetados nas Activities via `by viewModel()`. Proibido instanciar repositórios diretamente no ViewModel.
- **DefaultLifecycleObserver**: Lógicas atreladas ao ciclo de vida Android (Widgets, In-App Update) isoladas em observadores em `core/lifecycle/`.

### 4. Qualidade de Código & Suíte de Testes (`REQ-04`, `STR-M04`, `STR-M05`)
- **Análise Estática com Detekt**: Executar `./gradlew detekt --auto-correct` antes de enviar PRs.
- **Limite de Linhas por Arquivo**: Arquivos mantidos concisos (máximo de 300 a 400 linhas).
- **Testes Unitários de ViewModel**: Todo ViewModel deve possuir testes unitários cobrindo as emissões do `StateFlow` (`./gradlew testDebugUnitTest`).
- **Screenshot Testing (`@PreviewTest`)**: Todos os diálogos, bottom sheets e telas principais devem possuir teste de screenshot validado (`./gradlew validateDebugScreenshotTest` / `./gradlew updateDebugScreenshotTest`).

Para regras e guias detalhados, consulte [ARCHITECTURE.md](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/ARCHITECTURE.md) e o Índice Mestre em [.gemini/index.md](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/index.md).

---

## 📂 Estrutura do Projeto

```text
dev.mnascimentos.aureole/
├── AureoleApplication.kt          # Application customizada & inicialização do Koin
├── MainActivity.kt                # Activity principal & inicialização do ciclo de vida
├── HomeActionsFactory.kt          # Fábrica de ações da UI para a tela principal
├── composable/                    # Componentes reutilizáveis do Design System
├── core/
│   ├── data/                      # Room DB, DAOs, entidades, receivers e repositórios
│   ├── designsystem/              # Temas (AureoleTheme), paletas, ícones e utilitários Haze
│   └── lifecycle/                 # Observadores de ciclo de vida (Widgets e Updates)
├── di/                            # Módulos de Injeção de Dependência (Koin)
└── feature/
    ├── home/                      # Tela inicial, engine de grade, painéis laterais, pastas e widgets
    └── settings/                  # Preferências do launcher (Aparência, Comportamento, Avançado)
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- **Android Studio:** Ladybug ou superior
- **JDK:** Java 17
- **Min SDK:** 28 (Android 9.0)
- **Target SDK:** 37

### Compilando o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/mnascimentos/AureoleLauncher.git
   cd AureoleLauncher
   ```

2. Instale os hooks do Git (para verificações pré-push):
   ```bash
   ./gradlew installGitHooks
   ```

3. Compile o APK de debug:
   ```bash
   ./gradlew assembleDebug
   ```

### Qualidade de Código & Testes

- **Executar análise estática com Detekt:**
  ```bash
  ./gradlew detekt
  ```

- **Executar Testes Unitários:**
  ```bash
  ./gradlew testDebugUnitTest
  ```

- **Validar Testes de Screenshot:**
  ```bash
  ./gradlew validateDebugScreenshotTest
  ```

- **Atualizar Imagens de Referência de Screenshot Tests:**
  ```bash
  ./gradlew updateDebugScreenshotTest
  ```

- **Gerar Relatório de Cobertura (JaCoCo):**
  ```bash
  ./gradlew jacocoTestReport
  ```

---

## 📄 Licença

Este projeto está licenciado sob a **PolyForm Noncommercial License 1.0.0** (Source-Available) - consulte o arquivo [LICENSE](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/LICENSE) para mais detalhes.

> **Nota**: Esta licença permite a visualização, uso pessoal e experimentos não comerciais, mas **proíbe estritamente o uso comercial, monetização ou redistribuição/fork não autorizado para fins lucrativos**.
