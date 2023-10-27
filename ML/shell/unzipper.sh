#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Only $# argument found."
    exit 1
fi

if ! [ -d "$1" ] || ! [ -d "$2" ]; then
    echo "Check directory path."
    exit 1
fi

echo "target folder"
echo $1
echo "destination folder"
echo $2

unzip "$1*.zip" -d "$2"
