#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_HOME="${JAVA_HOME:-/Users/liuyidi/.sdkman/candidates/java/21.0.12-tem}"
MODE="${1:-run}"

cd "$ROOT_DIR"

if [[ "$MODE" == "jar" ]]; then
  exec env JAVA_HOME="$JAVA_HOME" java -jar gateway/target/gateway-1.0.0-SNAPSHOT.jar
fi

exec env JAVA_HOME="$JAVA_HOME" mvn -pl gateway spring-boot:run
