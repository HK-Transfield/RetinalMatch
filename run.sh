#!/bin/bash
export CLASSPATH="/usr/share/java/opencv-420.jar:."
javac -d . RetinalMatch.java
java RetinalMatch <path to image 1>.jpg <path to image 2>.jpg