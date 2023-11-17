#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "No argument found."
    exit 1
fi

if ! [ -d "$1" ]; then
    echo "Check directory path."
    exit 1
fi

echo "target folder"
echo $1

find "$1" -name "*_[LRUD][_.]*" -exec rm -v {} \;
