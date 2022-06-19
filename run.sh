#!/bin/bash
export CLASSPATH="/usr/share/java/opencv-420.jar:."
javac -d . RetinalMatch.java

# java RetinalMatch RIDB_in/IM000005_1.jpg RIDB_in/IM000005_1.jpg
# java RetinalMatch RIDB_in/IM000002_12.jpg RIDB_in/IM000005_4.jpg
# java RetinalMatch RIDB_in/IM000003_12.jpg RIDB_in/IM000005_13.jpg
# java RetinalMatch RIDB_in/IM000004_14.jpg RIDB_in/IM000003_20.jpg
# java RetinalMatch RIDB_in/IM000001_14.jpg RIDB_in/IM000001_20.jpg
# java RetinalMatch RIDB_in/IM000001_13.jpg RIDB_in/IM000001_12.jpg
# java RetinalMatch RIDB_in/IM000002_1.jpg RIDB_in/IM000002_2.jpg
# java RetinalMatch RIDB_in/IM000001_2.jpg RIDB_in/IM000003_5.jpg
# java RetinalMatch RIDB_in/IM000004_13.jpg RIDB_in/IM000004_16.jpg
# java RetinalMatch RIDB_in/IM000005_12.jpg RIDB_in/IM000004_17.jpg

for i in {1...5}
do
    for j in {1...20}
    do
        for k in {1...5}
        do
            for l in {1...20}
            do
                java RetinalMatch "RIDB_in/IM00000$i_$j.jpg" "RIDB_in/IM00000$k_$l.jpg"
            done
        done
    done
done