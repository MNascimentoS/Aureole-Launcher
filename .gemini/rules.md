Regras de Desenvolvimento e Arquitetura do Projeto (Android + Compose)
1. Detekt e Qualidade de Código
   Validação Contínua: Sempre ao fim de uma atividade, execute ./gradlew detekt --auto-correct (ou a task equivalente). Se houver falhas, corrija os problemas introduzidos antes do commit. Utilize ./gradlew detekt --auto-correct (via plugin de formatação) para agilizar correções de estilo.

Imutabilidade das Regras: Nunca edite os arquivos de configuração do Detekt (app/config/detekt/detekt.yml) ou adicione supressões cegas (@Suppress) sem uma justificativa técnica formalizada em comentário.

Escopo e Tamanho: Os arquivos devem ser estritamente concisos, limitados a 300-400 linhas. Arquivos que ultrapassem esse limite devem ter suas lógicas extraídas para use cases, extensões ou componentes menores.

Separação de Responsabilidades: Funções devem ter um único propósito estrutural. Evite complexidade cognitiva alta (aninhamentos profundos como if dentro de forEach dentro de try).

Sem Modelos na UI: Arquivos de UI (*Screen.kt) não devem conter declarações de data class, enum class ou constantes de negócio. Modelos estruturais e estados devem residir em arquivos ou pacotes dedicados (State, Event, Model).

Nomenclatura Limpa e Explícita: Use nomes que descrevam a ação e o domínio exato (ex: updateUserThemePreference() em vez de setTheme()).

2. Estrutura Obrigatória por Tela (Feature)
   A arquitetura segue uma separação clara orientada a fluxo unidirecional de dados (Unidirectional Data Flow - UDF), aderindo aos princípios do MVI (Model-View-Intent).

Activity (*Activity.kt):

Papel: Ponto de entrada exclusivo da tela.

Regras: Apenas inicializa o tema (setContent), instancia o ViewModel (via injeção de dependência como Hilt/Koin) e chama o componente root da Screen.

Proibido: Qualquer lógica de negócio, requisições, manipulação de estado ou acesso direto a banco de dados.

Screen (*Screen.kt):

Papel: UI puramente declarativa e (idealmente) stateless.

Regras: Recebe o estado do ViewModel via collectAsStateWithLifecycle() para garantir segurança de ciclo de vida. Aplica State Hoisting rígido: receba dados básicos e callbacks (lambdas) em vez de instâncias de ViewModel nas funções filhas.

Performance (Listas): Ao utilizar LazyColumn ou LazyRow (especialmente para fluxos dinâmicos ou streaming), sempre defina key e contentType nos itens para garantir a preservação automatizada do estado da UI e evitar recomposições desnecessárias.

Obrigatório: Fornecer funções @Preview contemplando cenários de sucesso, erro e carregamento (usando mock data).

ViewModel (*ViewModel.kt):

Papel: Orquestrador de estado e regras de apresentação.

Regras (MVI): Expõe um único StateFlow representando o estado da tela (State). Recebe intenções do usuário através de métodos ou um canal de eventos (Intent/Action). Efeitos colaterais de disparo único (navegação, snackbars) devem usar SharedFlow ou Channel (Effect).

Proibido: Importar classes do framework Android (ex: Context, View, R.string), mantendo o ViewModel 100% testável via JUnit.

Repository (*Repository.kt / *RepositoryImpl.kt):

Papel: Fonte de verdade da feature (Single Source of Truth).

Regras: Oculta a complexidade de roteamento entre Cache, Room (Local) e Retrofit/Ktor (Remoto). Retorna sempre dados estruturados envoltos em utilitários de tratamento, como Result<T> ou fluxos assíncronos (Flow<T>).

Data Layer (Mappers e DTOs):

Regras: O pacote de dados (DAOs, chamadas de API, DTOs) é isolado. Modelos de resposta da rede (*Response / *DTO) devem ser convertidos em Modelos de Domínio através de funções de mapeamento (toDomain()) antes de chegarem ao ViewModel.

3. Componentes Reutilizáveis (/composable)
   Design System Interno: A pasta atua como um sistema de design padronizado. Antes de criar um componente iterativo na Screen (ex: botões personalizados, cards, text fields), verifique o catálogo existente.

Regra de Ouro do Compose: Todo componente reutilizável deve aceitar um parâmetro modifier: Modifier = Modifier como o primeiro parâmetro opcional (logo após os dados obrigatórios), permitindo que o chamador externo controle padding, tamanho e cliques sem alterar o componente base.

Agnosticismo: Componentes visuais não devem saber de onde os dados vêm. Não passe ViewModels ou Repositories para dentro de botões ou listas genéricas.

4. Boas Práticas Assíncronas (Corrotinas)
   Despachantes (Dispatchers): Nunca faça hardcode de Dispatchers.IO ou Dispatchers.Default diretamente nas funções. Injete os dispatchers nas classes para facilitar os testes unitários.

Escopo: Proibido o uso de GlobalScope. Inicie corrotinas atreladas ao ciclo de vida correto (ex: viewModelScope.launch).

5. Pós-Operação (Post-Op)
   Validação: Sempre valide o projeto inteiro com ./gradlew assembleDebug (ou execute a build diretamente na IDE) após finalizar a feature.

Testes e Cobertura: Garanta que a lógica crítica do ViewModel e dos Mappers foi coberta por testes unitários locais.

Deploy Local: Se o escopo da tarefa afetar a renderização nativa ou navegação, faça o deploy no dispositivo físico ou emulador conectado para validar fluidez e possíveis janks visuais (./gradlew installDebug).