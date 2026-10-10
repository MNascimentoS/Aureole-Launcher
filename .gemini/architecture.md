# Regras de Desenvolvimento e Arquitetura do Projeto (Android + Compose)

Este documento estabelece as regras fundamentais de arquitetura, organização de código e boas práticas para o projeto **Aureole Launcher**.

---

## 1. Organização Estrutural de Pastas e Pacotes

Adote rigorosamente a convenção **Package-by-Feature** combinada com **Clean Architecture**. É proibido misturar regras de negócio, dados e UI no mesmo arquivo.

### 1.1 Árvore de Diretórios Padronizada

```text
dev.mnascimentos.aureole/
│
├── composable/                         # Design System interno (Componentes visuais reutilizáveis)
│   ├── SettingsComponents.kt
│   └── ColorPickerDialog.kt
│
├── core/                               # Recursos globais e compartilhados
│   ├── analytics/                      # Auxiliares de analytics e métricas
│   ├── data/                           # Repositórios globais, banco de dados Room e cache
│   │   ├── cache/                      # IconCache, etc.
│   │   ├── db/                         # Room AppDatabase, DAOs e Entities globais
│   │   └── repository/                 # Implementação de repositórios globais
│   ├── designsystem/                   # Tokens, Temas e utilitários visuais
│   │   ├── theme/                      # AureoleTheme.kt, AureoleColor.kt, AureoleTypography.kt
│   │   ├── palette/                    # ThemePaletteBase.kt, ThemePaletteExtended.kt
│   │   ├── icons/                      # AureoleIcons.kt (Catálogo unificado de ícones)
│   │   └── utils/                      # HazeUtils.kt, AureolePreview.kt
│   └── lifecycle/                      # Observadores de ciclo de vida desacoplados (DefaultLifecycleObserver)
│       ├── WidgetLifecycleObserver.kt
│       └── UpdateLifecycleObserver.kt
│
└── feature/                            # Módulos de funcionalidades/telas
    └── [feature_name]/                 # Ex: feature/home, feature/settings
        ├── screens/                    # Composables de telas inteiras (ex: SettingsScreen.kt, HomeScreen.kt)
        ├── components/                 # Subcomponentes e diálogos exclusivos da feature (ex: EditSidePanelDialog.kt)
        ├── extensions/                 # Extensões do ViewModel e gerenciadores de estado da feature
        ├── model/                      # UiStates, Actions, Params e Data Classes exclusivas da UI
        ├── [Feature]Activity.kt        # Entrada Android da tela (Apenas se necessário)
        └── [Feature]ViewModel.kt       # MVI/MVVM ViewModel da feature
```

### 1.2 Regras Estritas de Divisão e Localização de Arquivos

1. **Separação de Modelos e UI:** É **estritamente proibido** declarar `data class`, `enum` ou interfaces de estado/parâmetros no mesmo arquivo de UI (`*Screen.kt`, `*Dialog.kt`). Todos os modelos devem residir na pasta `model/` da respectiva feature.
2. **Componentes Reutilizáveis vs. Exclusivos:** Se um Composable for reutilizado em duas ou mais telas ou for um elemento genérico do Design System (toggles, botões, dialogs reutilizáveis), mova-o para o pacote raiz `composable/`. Se for exclusivo de uma feature, mantenha-o em `feature/[feature_name]/components/`.

---

## 2. Detekt e Qualidade de Código (Regras à Prova de Falha)

1. **Limite Estrito de Linhas (300-400 linhas):**
   - O tamanho máximo absoluto para qualquer arquivo é de **300 a 400 linhas** (com exceção do catálogo unificado de ícones `AureoleIcons.kt`, que possui justificativa documentada e supressão explícita).
   - Se um arquivo se aproximar de 300 linhas, ele deve ser refatorado e dividido em subcomponentes ou arquivos de modelos/extensões.

2. **Complexidade Cognitiva Baixa:**
   - Máximo de 2 níveis de indentação dentro de qualquer bloco ou composable.
   - Use **Early Return (Guard Clauses)** no topo das funções em vez de aninhar múltiplos `if/else`.

3. **Gerenciamento do Detekt:**
   - **Zero Issues (`maxIssues: 0`):** O build falhará se houver qualquer violação do Detekt.
   - **Proibição de `@Suppress` sem Justificativa:** Não utilize `@Suppress` sem um comentário explicativo logo acima.
   - **Proibido alterar `detekt.yml`:** Arquivos de configuração do Detekt nunca devem ser editados para ignorar erros.

---

## 3. Padrão de Tela, Ciclo de Vida e State Management

### 3.1 `*Activity.kt` (Entrada Mínima)
- **Permitido:** Inicializar o tema visual e chamar `setContent { AppTheme { Screen() } }`.
- **Isolamento de Ciclo de Vida:** Lógicas atreladas a eventos de ciclo de vida do sistema (gerenciamento de Widgets, In-App Updates, Receivers) **devem ser isoladas em `DefaultLifecycleObserver`** na pasta `core/lifecycle/` e registradas no `onCreate` da Activity.
- **Proibido:** Manipular estados complexos, fazer chamadas de rede ou sobrescrever métodos como `onStart`, `onResume`, `onStop`, `onDestroy` para lógicas que podem residir em Observers.

### 3.2 `*Screen.kt` / `*BottomSheet.kt` / `*Dialog.kt` (UI Declarativa e Stateless)
- A UI deve ser *stateless* e seguir o **Fluxo Unidirecional de Dados (UDF)**: recebe um `UiState` imutável e emite intenções do usuário através de um objeto de ações (`*Actions` ou `*Intent`).
- **Listas Performáticas:** Em `LazyColumn`, `LazyRow` e `LazyVerticalGrid`, forneça sempre uma `key` estável e `contentType`.
- **Regra Obrigatória para Bottom Views e Popups:** Toda bottom view (bottom sheet) e popup (dialog ou popup customizado) precisa obrigatoriamente ter:
  1. Previews no próprio arquivo do componente principal (utilizando `@AureolePreview` ou `@Preview`).
  2. Testes de screenshot (`@PreviewTest`) correspondentes na pasta de testes de screenshot (`app/src/screenshotTest`).

### 3.3 `*ViewModel.kt` (Orquestrador)
- Expõe um `StateFlow<UiState>` público e mantém um `MutableStateFlow` privado.
- **Proibição Absoluta:** Nenhuma classe do pacote `android.*` (como `Context`, `View`, `Activity`) deve ser importada no ViewModel.

---

## 4. Checklist de Validação Pós-Alteração

Ao finalizar qualquer alteração em arquivos de código, valide localmente no terminal:

```bash
# 1. Correção e validação estática do Detekt
./gradlew detekt --auto-correct

# 2. Execução dos testes unitários
./gradlew testDebugUnitTest

# 3. Compilação completa do projeto
./gradlew assembleDebug
```
