# Diretrizes de Arquitetura e Padrões de Código (Android + Compose)

Este documento define as regras fundamentais de arquitetura, organização de pastas e qualidade de código para o nosso projeto Android utilizando Jetpack Compose. O objetivo é garantir manutenibilidade, facilidade de leitura e escalabilidade.

---

## 🏗️ Estrutura Obrigatória por Tela (Feature)

Cada funcionalidade ou tela do aplicativo deve ser dividida em camadas bem definidas. É proibido misturar regras de negócio, dados e UI no mesmo arquivo.

### 1. Activity (`*Activity.kt`)
- Atua exclusivamente como o ponto de entrada da funcionalidade no sistema Android.
- **Responsabilidade Única:** Inicializar o tema visual, instanciar o ViewModel e invocar a Screen principal no `setContent`.
- **Isolamento de Ciclo de Vida:** Serviços e APIs do sistema atrelados ao ciclo de vida (ex: gerenciamento de Widgets do Android, In-App Update, BroadcastReceivers) **devem ser desacoplados em `DefaultLifecycleObserver`** armazenados em `core/lifecycle/` e apenas registrados no `onCreate` da Activity.
- **Proibido:** Lógica de negócio, chamadas de rede, gerenciamento de estado complexo ou sobrescrever métodos de ciclo de vida (`onStart`, `onResume`, `onStop`, `onDestroy`) para lógica que possa residir em um Observer.

### 2. Screen e Views (`*Screen.kt`, `*Dialog.kt`)
- O arquivo principal de interface utilizando Jetpack Compose.
- **Stateless:** A UI deve ser desacoplada de estado mutável direto. Ela deve receber um `UiState` imutável e repassar eventos de interação do usuário através de callbacks/ações.
- **Fluxo Unidirecional de Dados (UDF):** O fluxo de dados deve ser estritamente unidirecional (ViewModel -> UiState -> UI -> Actions -> ViewModel).
- **Proibido `data class` em arquivos de View:** É **estritamente proibido** declarar `data class`, `enum` ou interfaces de parâmetros dentro de arquivos `.kt` de UI. Todos os modelos de dados e estados de tela devem residir na pasta `model/`.

### 3. ViewModel (`*ViewModel.kt`)
- Gerencia o estado da tela exposto em um `StateFlow` imutável e responde às ações enviadas pela UI.
- Comunica-se exclusivamente com a camada de Repositório (`Repository`).
- **Proibido:** Importar referências de UI ou do framework Android (ex: `Context`, `View`, `Activity`).

### 4. Repository (`*Repository.kt` / `*RepositoryImpl.kt`)
- Ponto único de verdade (Single Source of Truth) para os dados da feature.
- Oculte do ViewModel a origem real dos dados (API remota, banco de dados local Room, SharedPreferences, DataStore ou cache).
- Retorne dados estruturados em tipos de domínio (geralmente encapsulados em `Result` ou `Flow`).

### 5. Data Layer (Camada de Dados)
- Onde residem os modelos de dados locais (Entities), DTOs (Data Transfer Objects), DAOs e chamadas de API (Retrofit/Ktor).
- Realize o mapeamento (Mappers) dos dados brutos para os modelos de domínio antes de enviá-los ao repositório.

---

## 📂 Convenção de Estrutura de Pacotes por Feature

Toda funcionalidade em `feature/<nome_da_feature>/` deve obrigatoriamente seguir a seguinte estrutura de subpacotes:

```text
feature/<nome_da_feature>/
├── screens/         # Composables de telas inteiras (ex: SettingsScreen.kt, HomeScreen.kt)
├── components/      # Componentes visuais e diálogos exclusivos da feature (ex: EditSidePanelDialog.kt)
├── extensions/      # Extensões do ViewModel e gerenciadores auxiliares de estado
├── model/           # UiStates, Actions, Params e Data Classes exclusivas da feature
├── <Feature>Activity.kt
└── <Feature>ViewModel.kt
```

---

## 🎨 Aureole Design System e Componentes Reutilizáveis (`/composable`)

- **Referência do Design System:** As diretrizes completas de UI/UX, integração com Figma, tokens de cor (`AureoleColors`), tipografia (`AureoleTypography`), espaçamentos (`AureoleSpacing`) e catálogo de ícones (`AureoleDS.icons`) estão documentadas em `.gemini/aureoleo_design_system.md`.
- **Design Tokens:** É proibido utilizar valores numéricos de dimensões ou cores em código hexadecimal brutos nas telas. Utilize sempre os tokens do tema via `AureoleDS.colors`, `AureoleDS.spacings` (ex: `AureoleDS.spacings.medium`, `AureoleDS.spacings.xSmall`) e ícones de `AureoleDS.icons`.
- **Pacote `composable/`:** O pacote raiz `composable/` atua como o nosso repositório de componentes visuais do Design System interno.
- **Estrutura de `core/designsystem/`:** Organizado nos seguintes subpacotes:
  - `theme/`: Configuração base do tema Compose (`AureoleTheme.kt`, `AureoleColor.kt`, `AureoleTypography.kt`, `AureoleSpacing.kt`)
  - `palette/`: Catálogo unificado de paletas de cores (`ThemePaletteBase.kt` com supressão explícita para o catálogo)
  - `icons/`: Catálogo unificado de ícones (`AureoleIcons.kt`)
  - `utils/`: Utilitários visuais e anotações de preview (`HazeUtils.kt`, `AureolePreview.kt`)
- **Regra de Ouro:** Qualquer componente visual genérico ou compartilhado por mais de uma tela (botões customizados, seletores, toggles, dialogs reutilizáveis) **deve residir obrigatoriamente no pacote `composable/`**.
- **Assinatura de Componentes:** Todo Composable público reutilizável do Design System deve aceitar `modifier: Modifier = Modifier` logo após os parâmetros de conteúdo obrigatórios e ser estritamente *stateless*.

---

## 🔄 Gerenciamento do Ciclo de Vida (`core/lifecycle/`)

- Lógicas do ecossistema Android que dependem dos eventos de ciclo de vida da Activity (start, stop, resume, destroy) devem ser encapsuladas como implementações de `DefaultLifecycleObserver`.
- Devem ser localizadas no pacote `core/lifecycle/`.
- Exemplos: `WidgetLifecycleObserver.kt`, `UpdateLifecycleObserver.kt`.

---

## 📏 Qualidade de Código e Restrições do Detekt

O código deve ser pequeno, conciso, simples e direto. A leitura deve ser natural.

- **Tamanho Máximo de Arquivos (Limite Estrito):** Arquivos devem ser extremamente concisos. **O tamanho máximo permitido para qualquer arquivo é de 300 a 400 linhas** (com exceção explicita de catálogos unificados do Design System como `AureoleIcons.kt` e `ThemePaletteBase.kt`, que contêm supressão documentada). Se um arquivo ultrapassar 300-400 linhas, ele deve ser imediatamente refatorado e dividido em subcomponentes ou arquivos utilitários.
- **Funções Limpas:** Funções devem ter apenas uma responsabilidade clara. Evite aninhamentos profundos (`if` dentro de `if` dentro de `for`).
- **Zero Detekt Issues (`maxIssues: 0`):** O código deve estar 100% em conformidade com do Detekt. O build falhará se houver violações.
- **Proibição de `@Suppress` Sem Justificativa:** Não utilize `@Suppress` sem uma justificativa arquitetural explícita, revisada e documentada.
- **Proteção das Configurações do Detekt:** Arquivos de configuração do Detekt (como `detekt.yml`) **nunca** devem ser alterados para ignorar erros.
- **Nomenclatura Expressiva:** Variáveis e funções devem declarar explicitamente o que fazem (ex: `fetchUserPreferences()` em vez de `getData()`).

---

## 📝 Histórico de Refatoração

**[Recente] Conformidade Estrita com a Arquitetura**
O projeto passou por uma grande refatoração para garantir 100% de adequação a estas diretrizes:
1. **Separação de Modelos e UI:** Todas as `data classes` (configurações, params, etc.) foram removidas dos arquivos de View (`*Screen.kt`, `*Dialog.kt`) e transferidas para pacotes `model/` dedicados (ex: `FolderModels.kt`, `EditSidePanelModels.kt`).
2. **Criação do pacote `/composable`:** Componentes visuais compartilhados (como `SettingsComponents` e `ColorPickerDialog`) foram realocados de pacotes específicos (`feature/settings` e `core/designsystem`) para o pacote compartilhado centralizado `composable/`.
3. **Redução do Tamanho de Arquivos:** Arquivos que infligiam a regra de limite de 300-400 linhas foram divididos logicamente (`ThemePalette.kt`, `DynamicGridContainer.kt`, e `WidgetPickerBottomSheet.kt`).
4. **Limpeza da MainActivity:** Lógicas de ciclo de vida (como o gerenciador de Widgets e as verificações do *In-App Update*) foram completamente isoladas em observadores (`WidgetLifecycleObserver` e `UpdateLifecycleObserver` dentro da pasta `core/lifecycle/`), tornando a Activity restrita apenas à inicialização do Tema e da UI.
5. **Organização da Feature `settings`:** A pasta `feature/settings` foi reorganizada em subpacotes lógicos e padronizados (`screens/`, `components/`, `extensions/`, e `model/`), igualando a estrutura modular da feature `home`.
6. **Padronização do `core/designsystem/theme`:** Reorganizado em subpacotes e renomeados os arquivos de tema para utilizar o prefixo do projeto (`AureoleColor.kt`, `AureoleTheme.kt`, `AureoleTypography.kt`).
7. **Catálogo Unificado de Paletas:** Unificadas todas as paletas de cores em `ThemePaletteBase.kt` com supressão explícita do Detekt para o registro do Design System.

Siga estas regras para garantir que o projeto escale com saúde e sem acúmulo de débito técnico!
