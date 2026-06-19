#!/bin/bash
# ============================================================
#  MusicGO — lanzador de doble clic (macOS)
#  Haz doble clic para abrir la app (GUI JavaFX).
#  La primera vez Maven descarga JavaFX; puede tardar 1–2 min.
# ============================================================
cd "$(dirname "$0")" || exit 1

# Asegura que Java esté disponible aunque el doble-clic no herede el PATH del shell.
if [ -z "$JAVA_HOME" ] && [ -x /usr/libexec/java_home ]; then
  export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null)"
fi

echo "🎧  MusicGO — iniciando..."
echo "    JAVA_HOME=$JAVA_HOME"
echo "    (la primera vez se descargan Maven y JavaFX; puede tardar 1–2 min)"
echo ""

./mvnw javafx:run
codigo=$?

echo ""
if [ "$codigo" -ne 0 ]; then
  echo "⚠️  MusicGO terminó con error (código $codigo). Copia el mensaje de arriba."
else
  echo "✅  MusicGO se cerró correctamente."
fi
echo ""
echo "Pulsa cualquier tecla para cerrar esta ventana..."
read -n 1 -s
