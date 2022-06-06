#!/bin/bash
export CLASSPATH=lib/opencv-455.jar:.
javac -d . MyImage.java
java MyImage