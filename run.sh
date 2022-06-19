#!/bin/bash
# export CLASSPATH="/usr/share/java/opencv-420.jar:."
export CLASSPATH="/lib/opencv/java/opencv-455.jar:."
javac -d . RetinalMatch.java


# Matches
echo "----------------------------------------------------------"
echo "MATCHES"
java RetinalMatch RIDB_in/IM000005_1.jpg RIDB_in/IM000001_1.jpg
java RetinalMatch RIDB_in/IM000004_1.jpg RIDB_in/IM000002_1.jpg
java RetinalMatch RIDB_in/IM000003_1.jpg RIDB_in/IM000003_1.jpg
java RetinalMatch RIDB_in/IM000002_1.jpg RIDB_in/IM000004_1.jpg
java RetinalMatch RIDB_in/IM000001_1.jpg RIDB_in/IM000005_1.jpg
java RetinalMatch RIDB_in/IM000005_20.jpg RIDB_in/IM000001_20.jpg
java RetinalMatch RIDB_in/IM000004_20.jpg RIDB_in/IM000002_20.jpg
java RetinalMatch RIDB_in/IM000003_20.jpg RIDB_in/IM000003_20.jpg
java RetinalMatch RIDB_in/IM000002_20.jpg RIDB_in/IM000004_20.jpg
java RetinalMatch RIDB_in/IM000001_20.jpg RIDB_in/IM000005_20.jpg
java RetinalMatch RIDB_in/IM000001_14.jpg RIDB_in/IM000001_20.jpg

# Not matches
echo "----------------------------------------------------------"
echo "NOT MATCHES"
java RetinalMatch RIDB_in/IM000001_13.jpg RIDB_in/IM000001_12.jpg
java RetinalMatch RIDB_in/IM000002_1.jpg RIDB_in/IM000002_2.jpg
java RetinalMatch RIDB_in/IM000001_2.jpg RIDB_in/IM000003_5.jpg
java RetinalMatch RIDB_in/IM000004_13.jpg RIDB_in/IM000004_16.jpg
java RetinalMatch RIDB_in/IM000005_12.jpg RIDB_in/IM000004_17.jpg
java RetinalMatch RIDB_in/IM000002_14.jpg RIDB_in/IM000001_11.jpg
java RetinalMatch RIDB_in/IM000002_3.jpg RIDB_in/IM000002_17.jpg
java RetinalMatch RIDB_in/IM000001_2.jpg RIDB_in/IM000003_14.jpg
java RetinalMatch RIDB_in/IM000004_13.jpg RIDB_in/IM000004_8.jpg
java RetinalMatch RIDB_in/IM000005_2.jpg RIDB_in/IM000004_9.jpg
