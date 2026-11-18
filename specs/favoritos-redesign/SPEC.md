# Spec: favoritos-redesign

Reorganizar a sheet Escolher favoritos (`composable/favorites/`) aberta da home e dos Ajustes.

**Protótipo:** `../prototipo-lote-17-11.html`, aba "2 · favoritos-redesign" (direção A) · **Decisões:** `DECISOES.md`

## Requisitos (comuns às duas direções)
- [ ] **RF-01** **Um scroll só** por tela: o conteúdo da sheet é uma única `LazyColumn`; a prévia não rola.
- [ ] **RF-02** "Personalizar home" sai da lista e vira o ícone de lápis no **início** da linha do título (48 dp de
  toque); a logo do Aureole fica no fim, como nas outras sheets da home (`polimento-ui` RF-05); espelha no canhoto. Na sheet aberta pelos Ajustes ele não aparece (ver `polimento-ui` RF-07).
- [ ] **RF-03** O card "Sem favoritos…" só aparece com 0 favoritos, numa linha, e o texto acompanha "Mostrar todos".
- [ ] **RF-04** Busca fixa embaixo (como hoje), filtrando favoritos e demais apps.
- [ ] **RF-05** Paisagem: prévia à esquerda e sheet à direita (padrão do RF-16 da `ajustes-redesign`), com título e
  opções em ~70 dp; a lista de apps tem que aparecer sem rolar.
- [ ] **RF-06** Nada sai: mostrar todos os apps, rótulos, ordenar (arrastar ⋮⋮), remover (−), adicionar (+), busca,
  Personalizar home e o comportamento da sheet dos Ajustes (sem os switches).

## Direção A · Lista única (escolhida, D-01)
- [ ] **RF-A1** "Mostrar todos os apps" e "Rótulos" viram dois chips numa linha logo abaixo do título; "Rótulos" fica
  desabilitado com "Mostrar todos" desligado (como hoje).
- [ ] **RF-A2** Cabeçalhos "Favoritos · N" (com a dica de ordenar) e "Todos os apps · N" grudam no topo ao rolar.

## Testes
- Screenshot: retrato e paisagem, com 0 e com 3 favoritos, canhoto.
- Unitário: filtro da busca e regra do aviso (0 favoritos × mostrar todos).

## Fora do escopo
- Mudar a lista de apps da home ou o editor de pastas.
