#!/usr/bin/env bash

# Gradle startup script
APP_HOME=$(cd "$(dirname "$0")" && pwd)
JAVA_CMD=$(which java)

if [ -z "$JAVA_CMD" ]; then
    echo "ERROR: Java not found. Please install Java."
    exit 1
fi

# Use system gradle if available
if command -v gradle &> /dev/null; then
    exec gradle "$@"
else
    echo "ERROR: Gradle not found. Please install Gradle."
    exit 1
fi
