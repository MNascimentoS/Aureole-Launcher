# 📱 Aureole Product Context

Contexto do produto que não aparece no código. Vale para qualquer tarefa que toque textos, site, loja, privacidade
ou monetização.

## 1. O app
- **Aureole Launcher:** launcher Android leve, sem anúncios e sem rastreamento. Android 9+, Kotlin, 100% Jetpack Compose.
- Pacote: `dev.mnascimentos.aureole`. Na Play Store, publicado pelo grupo de desenvolvedores **"Teu Apps"**.
- Idiomas: EN (`res/values/`) e PT-BR (`res/values-pt-rBR/`). Toda string nova existe nos dois.
- Licença: PolyForm Noncommercial (`LICENSE`, `NOTICE`).

## 2. Privacidade (promessa pública, não quebrar)
- Sem conta, sem login, sem analytics e sem servidor próprio. Lista de apps, fotos e ajustes ficam no aparelho.
- A única coisa que sai é relatório técnico de falha (**Firebase Crashlytics**), sem nome, contatos, localização nem
  lista de apps.
- Qualquer feature que mande dado para fora do aparelho exige atualizar a política de privacidade (PT e EN) antes.
- **Permissões:** a política (`docs/privacy/*/`, seção "Permissões utilizadas") lista cada `uses-permission` do
  manifest. Permissão nova entra na tabela, nos dois idiomas, no mesmo commit. `EXPAND_STATUS_BAR` (normal, sem
  diálogo) abre a aba de notificações: deslizar para baixo na home e atalho "Notifications"
  (`ajustes-review-aislan-2` fase 2).

## 3. Pessoas e créditos
- Créditos no app: **Mateus** ("The Creator") e **Aislan Tavares** ("The Dreamer").
- Os ícones `ic_aureole_*` são do Mateus. Não trocar nem "melhorar" sem pedido.

## 4. Contatos
- PT-BR: `contato@aureolelauncher.app`. EN: `support@aureolelauncher.app`.
- O antigo `aureole@mnascimentos.dev` não deve mais aparecer.
- Sugestões e bugs: formulários linkados no site e no README.

## 5. Site (`docs/`, GitHub Pages)
- Domínio: **aureolelauncher.app** (antes `aureole.mnascimentos.dev`). Visual dark-first.
- Páginas em PT-BR e EN: home, `/privacy/pt-br/` e `/privacy/en/`, `/terms/pt-br/` e `/terms/en/`.
- Mudanças no site vão junto com a release em andamento (branch `release/x.y.z`) e sempre com print para validar.

## 6. Monetização (planejado, ainda não existe)
- Premium como **compra única** dentro do app (sem assinatura), **sem login**.
- Validação da compra **só no aparelho**. Reembolso direto pelo Google Play.
- Os Termos de Uso existem antes do premium, para já cobrir a compra.
