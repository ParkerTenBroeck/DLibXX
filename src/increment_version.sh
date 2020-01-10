#!/bin/bash

IN="VERSION"
OUT="build/Main/VERSION"

#collect data
revision=$(awk "NR == 1" $IN)
version=$(awk "NR == 2" $IN)
timestamp=$(date +%s)

#increment build
revision=$(($revision + 1))

#if packaging, increment actual version number
if [ -n "$1" ]; then
    printf "Current version: %s\nNew version (leave blank to keep current):\n" $version
    read -t 10 new
    version="${new:-$version}"
    printf "%s\n%s" "$revision" "$version" > "$IN"
fi

printf "%s\n%s\n" "$revision" "$version" "$timestamp" > "$OUT"
