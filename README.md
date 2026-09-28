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
- ⚙️ **Configurações Extensas**: Personalização detalhada sobre comportamento, aparência, gestos, listas de apps, papéis de parede dinâmicos e opções de privacidade/analytics.
- 🔄 **In-App Updates**: Suporte integrado para In-App Updates da Google Play e integração com Firebase Crashlytics e Analytics.

---

## 🛠️ Tecnologias & Bibliotecas

- **Linguagem:** Kotlin 2.x
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arquitetura:** Clean Architecture + MVVM + Fluxo Unidirecional de Dados (UDF)
- **Banco de Dados / Persistência:** [Room](https://developer.android.com/training/data-storage/room) & Helpers SQLite
- **Programação Assíncrona:** Kotlin Coroutines & `StateFlow` / `SharedFlow`
- **Efeitos Visuais & Blur:** [Haze](https://github.com/chrisbanes/haze) & Haze Materials
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)
- **Qualidade de Código & Análise Estática:** [Detekt](https://detekt.dev/) (`maxIssues: 0` obrigatório)
- **Testes:** JUnit, Robolectric, Compose UI Test, Compose Screenshot Testing, JaCoCo
- **CI/CD:** GitHub Actions & Fastlane (Distribuição Interna na Google Play)

---

## 🏗️ Arquitetura & Diretrizes

O Aureole Launcher segue princípios arquiteturais rigorosos para garantir a manutenibilidade, leitura e escalabilidade do código:

- **Fonte Única da Verdade (SSOT):** Os repositórios encapsulam as fontes de dados (Room, SharedPreferences, caches em memória) e expõem o estado reativo através de `Flow` e `StateFlow`.
- **UI Stateless:** Os componentes composable recebem um estado imutável (`UiState`) e emitem eventos via callbacks.
- **Observadores de Ciclo de Vida:** Funcionalidades atreladas ao ciclo de vida do Android (Widgets, In-App Updates, Broadcast Receivers) são isoladas em classes `DefaultLifecycleObserver` localizadas em `core/lifecycle/`.
- **Limites Estritos de Tamanho de Arquivo:** Arquivos de produção são mantidos concisos (máximo de 300 a 400 linhas) para garantir modularidade.

Para regras detalhadas sobre a arquitetura, consulte o arquivo [ARCHITECTURE.md](file:///home/mateus/AndroidStudioProjects/AureoleLauncher/ARCHITECTURE.md) e as especificações do sistema de design em [.gemini/aureoleo_design_system.md](file:///home/mateus/AndroidStudioProjects/AureoleLauncher/.gemini/aureoleo_design_system.md).

---

## 📂 Estrutura do Projeto

```text
dev.mnascimentos.aureole/
├── MainActivity.kt                # Activity principal & inicialização do ciclo de vida
├── HomeActionsFactory.kt          # Fábrica de ações da UI para a tela principal
├── composable/                    # Componentes reutilizáveis do Design System
├── core/
│   ├── analytics/                 # Auxiliares do Firebase / Analytics
│   ├── data/                      # Room DB, DAOs, entidades, receivers e repositórios
│   ├── designsystem/              # Temas (AureoleTheme), paletas, ícones e utilitários Haze
│   └── lifecycle/                 # Observadores de ciclo de vida (Widgets e Updates)
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

- **Gerar Relatório de Cobertura (JaCoCo):**
  ```bash
  ./gradlew jacocoTestReport
  ```

---

## 📄 Licença

Este projeto está licenciado sob a **PolyForm Noncommercial License 1.0.0** (Source-Available) - consulte o arquivo [LICENSE](file:///home/mateus/AndroidStudioProjects/AureoleLauncher/LICENSE) para mais detalhes.

> **Nota**: Esta licença permite a visualização, uso pessoal e experimentos não comerciais, mas **proíbe estritamente o uso comercial, monetização ou redistribuição/fork não autorizado para fins lucrativos**.
