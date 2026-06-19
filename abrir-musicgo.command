#!/bin/bash
# ============================================================
#  MusicGO — lanzador de doble clic (macOS)
#  Haz doble clic en este archivo para abrir la app (GUI JavaFX).
#  La primera vez Maven descarga JavaFX; puede tardar un poco.
# ============================================================
cd "$(dirname "$0")" || exit 1
echo "🎧  Iniciando MusicGO..."
echo "    (la primera vez se descargan dependencias, ten paciencia)"
echo ""
./mvnw javafx:run
