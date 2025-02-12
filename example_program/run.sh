#!/bin/sh
# This script compiles and executes the example program

# Compile the example program
javac -d . -classpath "../library/src/main/java" "src/main/java/example_test/Time.java"

# Execute the example program
java "example_test.Time"

# Remove the compiled files
rm -r example_test
rm -r clap