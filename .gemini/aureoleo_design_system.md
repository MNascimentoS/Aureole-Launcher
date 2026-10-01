# Diretrizes e Steering: Figma & Design System Integration (Jetpack Compose)

## 1. Configuração e Credenciais do Figma
- **Credenciais**: Carregadas do arquivo `credentials.properties` (`FIGMA_ACCESS_TOKEN`).
- **Amostra**: Veja `credentials.properties.sample` para a estrutura exigida.
- **Base Endpoint API REST**: `https://api.figma.com/v1`
- **Headers HTTP**:
  ```text
  X-Figma-Token: <FIGMA_ACCESS_TOKEN>
  ```

---

## 2. Mapeamento de Layout Figma -> Jetpack Compose
Ao inspecionar componentes no Figma (via MCP ou API REST):

- **Auto Layout**:
  - `layoutMode: "HORIZONTAL"` $\rightarrow$ `Row`
  - `layoutMode: "VERTICAL"` $\rightarrow$ `Column`
  - `itemSpacing` $\rightarrow$ `Arrangement.spacedBy(N.dp)`
  - `paddingLeft/Right/Top/Bottom` $\rightarrow$ `Modifier.padding(...)`
  - `primaryAxisAlignItems` / `counterAxisAlignItems` $\rightarrow$ Mapear para `Arrangement` e `Alignment` (`Center`, `SpaceBetween`, `Start`, `End`).

- **Cores & Efeitos**:
  - `fills` (Solid) $\rightarrow$ `Color(0xFF...)`
  - `strokes` & `strokeWeight` $\rightarrow$ `BorderStroke(N.dp, Color)` ou `Modifier.border(...)`
  - `rectangleCornerRadii` / `cornerRadius` $\rightarrow$ `RoundedCornerShape(N.dp)`
  - `effects` (Drop Shadow) $\rightarrow$ `Modifier.shadow(...)` ou `graphicsLayer`

- **Tipografia**:
  - Mapear `fontFamily`, `fontSize`, `fontWeight`, `lineHeight` e `letterSpacing` para objetos `TextStyle` no catálogo de tema da aplicação (`Type.kt`).

---

## 3. Regras de Código do Design System no Projeto

1. **Assinatura dos Componentes**:
   - Todo componente reutilizável do Design System deve aceitar `modifier: Modifier = Modifier` após os parâmetros obrigatórios de conteúdo.
   - Componentes devem ser **Stateless** (receber estados brutos e lambdas de callback).

2. **Design Tokens Unificados**:
   - As cores, tipografias e formas extraídas do Figma devem residir nos arquivos de tema do projeto (`Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt`).
   - Evitar valores numéricos e hexadecimais *hardcoded* diretamente nas telas; utilizar sempre tokens do tema (`MaterialTheme.colorScheme`, `MaterialTheme.typography`).

3. **Previews e Documentação**:
   - Fornecer sempre funções `@Preview` mostrando o componente em Light Theme e Dark Theme.

4. **Qualidade e Detekt**:
   - O código gerado deve atender aos limites de linhas (<300 linhas por arquivo) e passar no Detekt sem warnings (`./gradlew detekt`).

---

## 4. Estrutura do Tema Base e Cores (Aureole Colors)
O aplicativo suporta múltiplos temas definidos pelo usuário (ex: Frostbite, etc.), cada um com o mesmo conjunto de tokens base. O sistema de cores foi criado sob o objeto `AureoleColors`.

### Design Tokens Base (Composition Local)
Os seguintes design tokens formam a base da paleta de todos os temas do app:
- `onSurfaceHigh`
- `onSurfaceMedium`
- `onSurfaceLow`
- `outline`
- `surfaceVariant`
- `surface`
- `background`

Essas cores podem ser acessadas no Compose utilizando:
```kotlin
AureoleDS.colors.onSurfaceHigh
AureoleDS.colors.surfaceVariant
```

### Design Tokens de Dimensões e Espaçamento (AureoleDimens)
Os tokens de dimensões e espaçamento do Aureole Launcher seguem a escala baseada nas regras do Figma (grade de 4dp/8dp) para garantir consistência visual em margens, preenchimentos (padding) e arranjos entre componentes:

- `none`: `0.dp`
- `xxxSmall`: `2.dp`
- `xxSmall`: `4.dp`
- `xSmall`: `8.dp`
- `small`: `12.dp`
- `medium`: `16.dp`
- `large`: `20.dp`
- `xLarge`: `24.dp`
- `xxLarge`: `32.dp`
- `xxxLarge`: `48.dp`
- `huge`: `64.dp`

Essas dimensões podem ser acessadas via Compose utilizando:
```kotlin
AureoleDS.dimens.medium // 16.dp
AureoleDS.dimens.xSmall // 8.dp
```

### Tema Padrão: Frostbite
Os valores *default* em desenvolvimento são mapeados pelo tema Frostbite:
- `onSurfaceHigh`: `#FFFFFF`
- `onSurfaceMedium`: `#C5D8E8`
- `onSurfaceLow`: `#9BB4C8`
- `outline`: `#7D96AA`
- `surfaceVariant`: `#4A5D6B`
- `surface`: `#2B373E`
- `background`: `#12181D`

A implementação está consolidada nos arquivos `AureoleColors.kt`, `Color.kt` e injetada no `AureoleLauncherTheme` (via `Theme.kt`). Use sempre as variáveis de `AureoleTheme.colors` nas composables locais em vez de hexadecimais brutos.

---

## 5. Catálogo de Ícones do Aureole (Figma Node 108:1697)
A biblioteca de ícones oficial do Aureole no Figma está mapeada e estruturada para uso nos componentes do app (`SettingsCustomComponents.kt`, `OpenedFolderLayouts.kt`, etc.):

### NAVEGAÇÃO E CONTROLES
- `Home`: Ícone de página inicial / launcher padrão.
- `Menu` / `Tiny Menu` / `Drag Menu`: Linhas horizontais de menu/gaveta lateral.
- `Arrow Up` / `Arrow Down` / `Left Up` / `Right Down`: Setas de navegação e gestos.
- `Align right`: Alinhamento e orientação de painel.

### AÇÕES E EDIÇÃO
- `Edit`: Ícone de lápis para alteração de papéis de parede e textos.
- `Add`: Sinal de mais para adição de pastas e atalhos.
- `Delete`: Ícone de lixeira para remoção e desinstalação.
- `Search`: Ícone de lupa para barra de busca.
- `Undo`: Seta de rotação anti-horária para desfazer/restaurar layout.
- `Refresh ccw`: Seta dupla de atualização para verificação de updates e redefinição.
- `Check`: Marca de seleção para confirmações e estados ativos.
- `Corners` / `Press` / `Click`: Indicadores visuais de bordas e atalhos.

### TEMA, CORES E EFEITOS
- `Color Fill` (`format_color_fill`): Preenchimento de cor de menu.
- `Text Color` (`format_color_text`): Modificadores de cor de texto.
- `Droplet` / `Folder Stroke Color`: Gota/paleta de cores primárias e contornos de pasta.
- `Glass`: Linhas paralelas (`//`) indicando efeito de desfoque/vidro fosco (Haze/Blur).
- `Layout`: Mapeamento visual de grades e coleções.

### SISTEMA E UTILITÁRIOS
- `Settings`: Ícone de engrenagem para preferências gerais.
- `Advanced`: Ícone de ferramentas / três pontos (`...`) para opções avançadas.
- `Info`: Círculo de informação (`i`) para dados do aplicativo.
- `Calendar`: Ícone de calendário.
- `Keyboard`: Ícone de teclado.
- `Mail`: Ícone de e-mail/suporte.
- `Eye off`: Ícone de visibilidade (ocultar itens/scrubber).
- `Star`: Ícone de estrela.
- `Smile`: Ícone de avaliação/colaboradores.

