# Regras de Desenvolvimento e Arquitetura do Projeto

1. **Detekt e Qualidade de Código:**
   - Sempre que modificar código Kotlin, execute `./gradlew detekt` (`gradle_build(commandLine="detekt")`). Se houver falhas, corrija os problemas introduzidos até que o build passe.
   - **IMPORTANTE:** Nunca edite arquivos de configuração do Detekt (como `app/config/detekt/detekt.yml` ou arquivos baseline). A configuração do Detekt deve permanecer intacta.
   - O código deve ser pequeno, conciso e direto (arquivos com mais de 300-400 linhas devem ser divididos).
   - Funções devem fazer apenas uma coisa, evitando aninhamentos complexos (`if` dentro de `if` dentro de `for`).
   - Nomenclatura descritiva (ex: `fetchUserPreferences()` em vez de `getData()`).

2. **Estrutura Obrigatória por Tela (Feature):**
   - **Activity (`*Activity.kt`):** Apenas ponto de entrada do sistema Android, inicializa o tema, instancia o ViewModel (via DI) e chama a Screen. Proibido: lógica de negócio, chamadas de rede ou gerenciamento de estado complexo.
   - **Screen (`*Screen.kt`):** UI stateless em Compose, recebendo estado do ViewModel e repassando callbacks. Todos os componentes visuais devem vir da pasta compartilhada `composable/` (Design System interno).
   - **ViewModel (`*ViewModel.kt`):** Gerencia o estado (`StateFlow`/`LiveData`), reage às ações do usuário e comunica-se exclusivamente com el Repository. Proibido: importar referências do framework Android (`Context`, `View`).
   - **Repository (`*Repository.kt` / `*RepositoryImpl.kt`):** Única fonte de verdade para os dados da feature, ocultando a origem (API/DB/cache) e retornando dados estruturados (`Result`/`Flow`).
   - **Data Layer:** Modelos locais, DTOs, DAOs e chamadas de API, com Mappers para modelos de domínio.

3. **Componentes Reutilizáveis (`/composable`):**
   - Atua como Design System. Verifique se o componente já existe antes de criá-lo na Screen, e extraia componentes reutilizáveis para a pasta `composable`.

4. **Pós-Operação:**
   - Sempre execute um build do projeto (`gradle_build(commandLine="assembleDebug")` ou `./gradlew assembleDebug`).
   - Se houver dispositivo conectado, instale/implante o app (`deploy` ou `./gradlew installDebug`).
