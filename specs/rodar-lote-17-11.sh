#!/usr/bin/env bash
# Roda o lote 17/11 no Claude Code e, se a sessão parar (limite de uso, sobrecarga, queda), espera e retoma
# de onde parou até existir specs/lote-17-11-FEITO.md (criado no fim da fase G).
# Uso (na raiz do repo):  bash specs/rodar-lote-17-11.sh
# Parar:  Ctrl+C  (ou criar specs/lote-17-11-PARAR)
set -u
cd "$(git rev-parse --show-toplevel)"

ESPERA_MIN="${ESPERA_MIN:-20}"          # minutos entre tentativas depois de uma parada
MAX_TENTATIVAS="${MAX_TENTATIVAS:-72}"   # 72 x 20 min = 24 h
# Para rodar sem ninguém aprovando ações, ajuste as permissões (ex.: "--permission-mode acceptEdits" ou a sua
# allowlist em .claude/settings.local.json). Sem isso, a sessão para esperando aprovação.
CLAUDE_FLAGS="${CLAUDE_FLAGS:-}"
FEITO="specs/lote-17-11-FEITO.md"
PARAR="specs/lote-17-11-PARAR"
LOG="specs/lote-17-11-execucao.log"

INICIO='Execute o lote de specs conforme specs/EXECUTAR-LOTE-17-11.md (seção "Prompt"), do começo ao fim, sem parar para perguntar.'
RETOMAR='Retome o lote 17/11 de onde parou. Releia specs/EXECUTAR-LOTE-17-11.md, o git log desde o início do lote e o PROGRESSO.md de cada spec para saber a próxima fase não concluída; se havia uma fase pela metade (mudanças sem commit), confira o que já está feito, termine e commite. Depois siga a ordem e as regras do EXECUTAR até a fase G criar specs/lote-17-11-FEITO.md.'

git checkout release/0.9.44 >>"$LOG" 2>&1 || { echo "Não consegui ir para release/0.9.44" | tee -a "$LOG"; exit 1; }

tentativa=0
while [ ! -f "$FEITO" ] && [ ! -f "$PARAR" ] && [ "$tentativa" -lt "$MAX_TENTATIVAS" ]; do
  tentativa=$((tentativa + 1))
  echo "=== $(date '+%d/%m %H:%M') tentativa $tentativa ===" | tee -a "$LOG"
  if [ "$tentativa" -eq 1 ]; then
    claude -p "$INICIO" $CLAUDE_FLAGS 2>&1 | tee -a "$LOG"
  else
    claude -c -p "$RETOMAR" $CLAUDE_FLAGS 2>&1 | tee -a "$LOG"
  fi
  [ -f "$FEITO" ] && break
  echo "Sessão parou sem terminar o lote. Nova tentativa em $ESPERA_MIN min." | tee -a "$LOG"
  sleep "$((ESPERA_MIN * 60))"
done

if [ -f "$FEITO" ]; then echo "Lote concluído: veja $FEITO" | tee -a "$LOG"; else echo "Parado sem concluir (veja $LOG)" | tee -a "$LOG"; fi
