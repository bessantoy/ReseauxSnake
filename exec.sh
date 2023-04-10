#!/bin/bash

path=$(pwd)

if [ -z "$1" ]; then
    echo "Usage: $0 <command>"
    exit 1
else
  if [ -d "$1" ]; then
    cd "$1"
    file=$(find . -maxdepth 1 -iname "main.java" -type f)
    if [ -f "$file" ]; then
      cp=""
      if [ -d "$path/lib" ]; then
        cp="$path/lib/*:"
      fi
      if [ ! -d "$path/build" ]; then
        mkdir "$path/build"
      fi
      for i in $(find . -maxdepth 1 -iname "*.java" -type f); do
        javac -d $path/build/ -cp "$cp" $i
      done
      java -cp "$path/build/:$cp" $file
      cd ../
    else
      echo "$1/Main.java does not exist"
    fi
    cd ../
  else
      echo "Folder does not exist"
  fi
fi