#!/usr/bin/env bash

javac -classpath rsyntaxtextarea.jar:weblaf-complete-1.28.jar:./ `find ./ -name "*.java"`

mkdir -p temporary
cp -r org temporary/org
cd temporary
jar xf ../rsyntaxtextarea.jar
jar xf ../weblaf-complete-*.jar
jar cfe ../metta-full.jar org.mettascript.editor.swing.Main *
cd ..
rm -r temporary
