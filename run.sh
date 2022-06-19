#!/bin/bash
export CLASSPATH="lib/opencv-455.jar:."
javac -d . *.java
java RetinalMatch