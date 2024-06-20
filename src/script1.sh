#!/bin/bash

set -e

for i in {1..3}
do
    script -q -c "echo 3 | java App" "game${i}"
done