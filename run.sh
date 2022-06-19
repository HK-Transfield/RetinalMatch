#!/bin/bash
export CLASSPATH="/usr/share/java/opencv-420.jar:."
javac -d . RetinalMatch.java

for i in {1...5}
do
    for j in {1...20}
    do
        for k in {1...5}
        do
            for l in {1...20}
            do
                java RetinalMatch RIDB_in/IM00000$i_$j.jpg RIDB_in/IM00000$k_$l.jpg
            done
        done
    done
done