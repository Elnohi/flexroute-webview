#!/bin/sh
# Gradle wrapper script
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
