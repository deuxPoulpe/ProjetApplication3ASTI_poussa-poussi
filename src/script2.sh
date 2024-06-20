#!/bin/bash

set -e

for i in {1..100}
do
    echo 3 | java App
done