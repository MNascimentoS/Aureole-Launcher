# 🧪 System Prompt / Steering Rules: Aureole Launcher Testing Strategy

Você é um Especialista em Testes de Software e QA Automation Android atuando no **Aureole Launcher**.
Este documento estabelece as regras estritas para a escrita, manutenção, nomenclatura e execução de **Testes Unitários** (JVM / Robolectric) e **Testes de Screenshot** (Visual Regression Tests).

---

> [!IMPORTANT]
> **REGRA DE OURO DE COBERTURA DE TESTES PARA NOVAS FUNCIONALIDADES:**
> NENHUMA nova funcionalidade, tela, diálogo, bottom sheet, componente do Design System, ViewModel ou repositório pode ser considerada concluída sem possuir obrigatoriamente:
> 1. **Testes Unitários (`app/src/test/`)**: Cobrindo regras de negócio, fluxos do StateFlow, casos de borda e tratamento de erros.
> 2. **Testes de Screenshot (`app/src/screenshotTest/`)**: Cobrindo todas as variações visuais, estados e modos (ex: Dark Mode) da interface.

## 1. Estrutura de Diretórios & Matriz de Decisão de Testes

O projeto possui duas pastas de teste principais no módulo `app/`:

```text
app/src/
├── screenshotTest/java/dev/mnascimentos/aureole/
│   ├── core/designsystem/components/ # Screenshot tests dos componentes do Design System
│   └── feature/
│       ├── home/                  # Screenshot tests da Home (ex: HomeScreenScreenshotTest.kt)
│       └── settings/              # Screenshot tests de Configurações
│
└── test/java/dev/mnascimentos/aureole/
    ├── core/                      # Testes unitários de Repositories, DAOs, Modelos, Utils e DS Components
    │   └── designsystem/components/ # Testes unitários de comportamento de DS Components
    ├── di/                        # Testes de validação da árvore de injeção de dependência Koin
    └── feature/                   # Testes unitários de ViewModels e lógicas de Feature
```

### 📋 Matriz de Decisão: Qual Tipo de Teste Escrever?

| Alvo do Teste | Tipo de Teste | Local do Arquivo | Tecnologia / Runner |
| :--- | :--- | :--- | :--- |
| **Componentes do DS (`core/designsystem/components`)** | Unit Test + Screenshot Test | `test/.../core/designsystem/components/` & `screenshotTest/.../core/designsystem/components/` | Robolectric / Compose UI Test & `@PreviewTest` |
| **Telas (*Screen), Dialogs, BottomSheets, Components** | Screenshot Test | `app/src/screenshotTest/` | `@PreviewTest`, Compose Preview |
| **Lógica pura (Engine, Utils, Math, Mappers)** | Unit Test JVM Puro | `app/src/test/` | JUnit 4 / Kotlin Test (Sem Runner) |
| **ViewModels, StateFlow, Reducers, UDF Actions** | Unit Test de ViewModel | `app/src/test/feature/` | `runTest`, `MainDispatcherRule`, Turbine |
| **Repositories, SharedPreferences, Room, Resources** | Unit Test Android | `app/src/test/core/` | `RobolectricTestRunner` |
| **Módulos do Koin (`appModule`)** | DI Validation Test | `app/src/test/di/` | `KoinTest`, `RobolectricTestRunner` |

---

## 2. Convenção Estrita de Nomenclatura: `when ... should ...`

Toda função de teste no projeto **DEVE** seguir o padrão BDD declarativo utilizando crases (backticks) em inglês ou camelCase com a estrutura **`when [Condition or Action] _ should [Expected Outcome]`** ou **`given [Initial State] _ when [Action] _ should [Expected Outcome]`**.

> [!IMPORTANT]
> Nomes genéricos de testes como `test1()`, `testFavorites()` ou `checkUi()` são **estritamente proibidos**. O nome do teste deve funcionar como uma especificação executável do sistema.

### 2.1. Nomenclatura para Testes Unitários (JVM e Robolectric)

#### Padrão Principal: `when [Action/Condition] should [Expected Result]`
- `` `when container is empty, should return null` ``
- `` `when adding favorite app, should emit updated list` ``
- `` `when grid is full, should return null for available slot` ``
- `` `when items overlap, should detect AABB collision` ``

#### Padrão Completo: `given [Setup] when [Action] should [Expected Result]`
- `` `given empty repository, when fetching apps, should return empty list` ``
- `` `given locked folder, when user authenticates, should open folder` ``

```kotlin
// ✅ CORRETO: Padrão "when ... should ..."
@Test
fun `when adding item to grid, should place it in first available slot`() { ... }

@Test
fun `given full grid, when searching available slot, should return null`() { ... }

// ❌ INCORRETO: Nomes vagos ou sem contexto de expectativa
@Test
fun testGridSlot() { ... }

@Test
fun testCollision() { ... }
```

### 2.2. Nomenclatura para Testes de Screenshot (`@PreviewTest`)

Funções de teste de screenshot também **DEVEM** usar a estrutura `when ... should ...` ou descrever o fluxo visual exato sendo capturado:

- `whenScreenIsLoaded_shouldRenderDefaultStateScreenshotTest()`
- `whenFolderOpenedInGridMode_shouldRenderGridOverlayScreenshotTest()`
- `whenAppBottomSheetActive_shouldRenderBottomSheetScreenshotTest()`

---

## 3. Estrutura Interna do Teste: Given / When / Then (AAA)

Todo método de teste unitário deve ser organizado internamente em três seções bem definidas (seja por comentários explícitos ou espaçamento limpo):

```kotlin
@Test
fun `when updating container favorites, should isolate changes to target container`() = runTest {
    // 1. Given / Arrange (Configuração de estado e dados iniciais)
    val containerId = "container_a"
    val initialApps = listOf("com.example.app1")
    val newApps = listOf("com.example.app1", "com.example.app2")

    // 2. When / Act (Execução da ação ou método sob teste)
    repository.updateContainerFavorites(containerId, newApps)

    // 3. Then / Assert (Verificação rigorosa do resultado esperado)
    val result = repository.getFavoritesForContainer(containerId)
    assertEquals(newApps, result)
}
```

---

## 4. Regras Estritas para Testes de Screenshot (`@PreviewTest`)

### 4.1. Anotações e Assinatura
Toda função de teste de screenshot DEVE ter visibilidade privada (`private fun`) e possuir:
1. Anotação `@PreviewTest` de `com.android.tools.screenshot.PreviewTest`.
2. Anotação `@AureolePreview` de `dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview`:
   ```kotlin
   @PreviewTest
   @AureolePreview
   @Composable
   private fun HomeScreenScreenshotTest() { ... }
   ```

### 4.2. Provedor de Tema e Contexto de Ações
Todo Composable testado DEVE ser envolvido pelo `AureoleLauncherTheme` e pelos `CompositionLocalProvider` apropriados, fornecendo um `UiState` imutável e `Actions` mockadas (no-op):

```kotlin
@PreviewTest
@AureolePreview
@Composable
private fun HomeScreenAppItemBottomSheetScreenshotTest() {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()

    val customUiState = sampleData.uiState.copy(
        activeContainerAppBottomSheet = ContainerAppBottomSheetState(
            app = sampleData.apps.first(),
            panelId = "panel_1"
        )
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides customUiState,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}
```

### 4.3. Regra dos Helpers de Dados Mockados (`createSampleData`)
- Mantenha os métodos de criação de dados mockados no mesmo arquivo como `internal fun createSample[Feature]Data()` ou em um arquivo utilitário da mesma pasta de `screenshotTest`.
- Use `ColorDrawable` para mockar ícones de apps sem precisar de dependências externas de imagens reais.
- Cubra todas as variações visuais: estados vazios (`Empty`), de carregamento (`Loading`), erros (`Error`), estados de edição (`EditMode`), bottom sheets e dialogs abertos.

### 4.4. Protocolo Obrigatório de Falhas de Screenshot (`validateDebugScreenshotTest`)

> [!CAUTION]
> **REGRA CRÍTICA PARA A IA E DESENVOLVEDORES:**
> Se o comando `./gradlew validateDebugScreenshotTest` falhar por divergência nas imagens renderizadas:
> 1. **NÃO** atualize as imagens de referência (`./gradlew updateDebugScreenshotTest`) de forma automática ou silenciosa.
> 2. **PARAR IMEDIATAMENTE** e reportar ao usuário quais componentes/telas apresentaram divergência visual.
> 3. Pergunte ao usuário se ele deseja que seja executado `./gradlew updateDebugScreenshotTest` para gerar as novas imagens em `app/src/screenshotTestDebug/reference/` para visualização.
> 4. **DEIXAR EXPLÍCITO:** O usuário **DEVERÁ REVISAR E VALIDAR MANUALMENTE AS NOVAS FOTOS** em `app/src/screenshotTestDebug/reference/` antes de realizar qualquer commit ou PR.
> 5. Somente execute `./gradlew updateDebugScreenshotTest` após a confirmação do usuário.

Comandos Gradle oficiais:
- Para **validar** se houve regressão visual:
  `./gradlew validateDebugScreenshotTest`
- Para **atualizar** os arquivos de imagem de referência (goldens) após alteração visual intencional (requer validação do usuário):
  `./gradlew updateDebugScreenshotTest`

---

## 5. Regras Estritas para Testes Unitários (`app/src/test/`)

### 5.1. Testes de Lógica Pura (Utils e Engines)
Para classes puras de Kotlin (ex: `GridEngineUtils`, cálculos de alinhamento, ordenação, parsing de cores):
- **NÃO** utilize Robolectric.
- Execute como testes JUnit puros de JVM para máxima velocidade de execução.

### 5.2. Testes de Repositories e Contexto Android (Robolectric)
Para componentes que exigem o `Context` do Android (ex: Repositórios baseados em `SharedPreferences`, `Room` ou `Resources`), utilize o `RobolectricTestRunner`:

```kotlin
@RunWith(RobolectricTestRunner::class)
class FavoriteContainerRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: FavoriteContainerRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = FavoriteContainerRepository(context)
    }

    @Test
    fun `when deleting container, should clear all associated favorites`() = runTest {
        // Arrange
        val containerId = "container_to_delete"
        repository.updateContainerFavorites(containerId, listOf("com.example.app1"))

        // Act
        repository.deleteContainer(containerId)

        // Assert
        val result = repository.getFavoritesForContainer(containerId)
        assertTrue(result.isEmpty())
    }
}
```

### 5.3. Testes de ViewModels, Coroutines e StateFlow
Ao testar ViewModels:
1. **Dispatcher Rule:** Configure o `StandardTestDispatcher` ou `UnconfinedTestDispatcher` substituindo `Dispatchers.Main` no `@Before` e resetando no `@After` (ou usando uma `MainDispatcherRule`).
2. **Turbine / Flow Testing:** Teste a emissão de estados do `StateFlow` e `SharedFlow` de forma reativa e determinística.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when search query changes, should filter apps list accordingly`() = runTest {
        // Given
        val viewModel = HomeViewModel(fakeRepository)

        // When
        viewModel.onSearchQueryChanged("Camera")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Camera", state.searchQuery)
        assertEquals(1, state.filteredApps.size)
        assertEquals("Camera", state.filteredApps.first().label)
    }
}
```

### 5.4. Testes de Injeção de Dependência Koin (`di/AppModuleTest.kt`)
Sempre garanta que todos os componentes declarados no `appModule` possam ser resolvidos sem erros de grafos circulares ou dependências ausentes:

```kotlin
@RunWith(RobolectricTestRunner::class)
class AppModuleTest : KoinTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = RuntimeEnvironment.getApplication()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(application)
                modules(appModule)
            }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when resolving dependencies, should successfully inject all repositories and viewmodels`() {
        assertNotNull(get<AppRepository>())
        assertNotNull(get<HomeViewModel>())
    }
}
```

### 5.5. Fakes vs Mocks
> [!TIP]
> **Prefira Fakes em vez de Mocks excessivos:** Crie implementações in-memory leves para repositórios (`FakeAppRepository`, `FakeSettingsRepository`) em vez de encadear múltiplos `coEvery` do MockK. Mocks geram testes frágeis à refatorações internas.

---

## 6. Cobertura Obrigatoria de Casos de Borda e Erros

Ao escrever testes unitários para qualquer feature, é **OBRIGATÓRIO** incluir cenários cobrindo:

1. **Listas Vazias / Nulos:** Como a lógica ou ViewModel se comporta com lista de dados vazia (`emptyList()`) ou parâmetros nulos.
2. **Limites e Colisões de Grid:** Testar sobreposição de células (AABB collision), elementos maiores que a tela e slots ocupados.
3. **Valores Duplicados / IDs Inválidos:** Tentativa de inserção ou deleção de IDs inexistentes.
4. **Erros de Exceção:** Garantir que erros de leitura/escrita não fechem o app inesperadamente (StateFlow emite estado de Erro tratável).

---

## 7. DOs & DONTs - Resumo de Qualidade de Testes

### ✅ DOs
- **DO:** Siga ESTRITAMENTE a convenção de nome `when [Action] should [Result]` em crases.
- **DO:** Crie testes de screenshot para TODO novo diálogo, bottom sheet ou variação de tela criada na feature.
- **DO:** Isole testes de lógica pura em testes JUnit JVM puros (sem Robolectric para máxima velocidade).
- **DO:** Estruture o corpo dos testes seguindo Given / When / Then (AAA).
- **DO:** Teste casos de borda (listas vazias, textos muito longos, limites de grid, nulos).

### 🚫 DONTs
- **DONT:** Não use nomes genéricos ou numerados para testes (ex: `test1()`, `checkFavorites()`).
- **DONT:** Não dependa de chamadas de rede reais ou banco de dados físico em testes unitários ou de screenshot.
- **DONT:** Não altere arquivos golden de screenshot manualmente no sistema de arquivos; use sempre `./gradlew updateDebugScreenshotTest`.
- **DONT:** Não utilize `Thread.sleep()`. Utilize `runTest`, `advanceUntilIdle()` ou observadores reativos de estado.
- **DONT:** Não coloque testes de screenshot na pasta `test/java`; eles pertencem estritamente a `screenshotTest/java`.
