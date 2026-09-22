# Diretrizes de Arquitetura e Padrões de Código (Android + Compose)

Este documento define as regras fundamentais de arquitetura, organização de pastas e qualidade de código para o nosso projeto Android utilizando Jetpack Compose. O objetivo é garantir manutenibilidade, facilidade de leitura e escalabilidade.

## 🏗️ Estrutura Obrigatória por Tela (Feature)

Cada nova funcionalidade ou tela do aplicativo deve ser dividida na seguinte estrutura de responsabilidades. Não é permitido misturar regras de negócio, dados e UI no mesmo arquivo.

### Activity (`*Activity.kt`)
- Atua apenas como o ponto de entrada da tela no sistema Android.
- Sua única responsabilidade é inicializar o tema, instanciar o ViewModel (via injeção de dependência) e chamar a Screen principal.
- **Proibido:** Lógica de negócio, chamadas de rede ou gerenciamento de estado complexo.

### Screen (`*Screen.kt`)
- O arquivo principal de UI da funcionalidade utilizando Jetpack Compose.
- Deve ser *stateless* (sempre que possível), recebendo o estado do ViewModel e repassando eventos (callbacks) de volta para ele.
- **Regra de Ouro da UI:** Todo componente visual utilizado na Screen (Botões, Cards, TextFields, etc.) deve ser importado da nossa pasta compartilhada `composable/` (nosso Design System interno). Não crie componentes genéricos isolados dentro da feature.
- **Proibido `data class` em arquivos de View:** Arquivos de UI / Screen ou componentes visuais não devem conter declarações de `data class`. Todos os modelos de dados e estados devem ser definidos em arquivos de modelo dedicados.

### ViewModel (`*ViewModel.kt`)
- Gerencia o estado da Screen (`StateFlow`/`LiveData`) e reage às ações do usuário.
- Comunica-se exclusivamente com o Repository.
- **Proibido:** Importar referências do framework Android (ex: `Context`, `View`).

### Repository (`*Repository.kt` / `*RepositoryImpl.kt`)
- Ponto único de verdade para os dados da feature.
- Oculte a origem dos dados (se vem da API, do banco local ou cache) do ViewModel.
- Retorne dados estruturados (geralmente encapsulados em `Result`/`Flow`).

### Data Layer (Camada de Dados)
- Onde residem os modelos de dados locais, DTOs (Data Transfer Objects), DAOs e chamadas de API (Retrofit/Ktor) exclusivas da feature.
- Faça o mapeamento (Mappers) dos dados brutos para os modelos de domínio antes de enviá-los ao repositório.

## 🧩 Componentes Reutilizáveis (`/composable`)

- A pasta `composable` atua como o nosso Design System.
- Antes de criar um componente visual na sua Screen, verifique se ele já existe na pasta `composable`.
- Se você criar um componente que pode ser usado em mais de uma tela, ele deve ser extraído e movido para a pasta `composable`.

## 📏 Qualidade de Código e Detekt

O código deve ser pequeno, simples e direto. A leitura deve ser natural.

- **Tamanho Máximo de Arquivos (Limite Estrito):** Classes e arquivos devem ser extremamente concisos. **O tamanho máximo permitido para um arquivo é de 300-400 linhas.** Se um arquivo ultrapassar esse limite, ele está fazendo coisas demais e deve ser imediatamente refatorado e dividido.
- **Funções Limpas:** Funções devem fazer apenas uma coisa. Evite aninhamentos complexos (`if` dentro de `if` dentro de `for`).
- **Detekt:** O código sempre deve estar de acordo com o detekt. O build falhará se houver violações (`maxIssues: 0`). Não utilize `@Suppress` sem uma justificativa arquitetural documentada e revisada. **Atenção:** Os arquivos de configuração do Detekt (como `detekt.yml`) nunca devem ser editados ou alterados.
- **Nomenclatura:** Variáveis e funções devem dizer exatamente o que fazem (ex: `fetchUserPreferences()` em vez de `getData()`).

Siga estas regras para garantir que o projeto escale com saúde e sem acúmulo de débito técnico!
