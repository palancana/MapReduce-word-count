#!/bin/bash

#Before running this script, please check:
# - This directory level contains "src" folder
# - "src" folder contains "WordCountMultiThread.java" file
# - This directory level contains "files" folder
# - "files" folder contains all the files you want to count words

#Execution in terminal should be like:
# $ sh WordCount.sh file1.txt file2.txt
# where 'file1.txt file2.txt' is the name or names of your files


for FILE in "$@"
do

	#Uncomment lines 19 and 21 for compiling the program
	#Creates the bin directory if it does not exist
	#mkdir -p bin
	#Compiles the .java class, in order to execute it later
	#javac -d bin src/WordCountMultiThread.java
	#Executes the program with the given argument
	echo "$FILE"
  	java -cp "bin" WordCountMultiThread files/$FILE
done