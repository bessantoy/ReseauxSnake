#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 <command>"
    exit 1
else
  if [ -d "$1" ]; then
    cd "$1"
    file=$(find . -maxdepth 1 -iname "Main.java")
    if [ -f "$file" ]; then
      javac -d ./build/ $file
      filename="${file##*/}"
      filename="${filename%.*}"
      cd build/
      echo "java $filename"
      java $filename
      cd ../
    else
      echo "Main.java does not exist"
    fi
    cd ../
  else
      echo "Folder does not exist"
  fi
fi