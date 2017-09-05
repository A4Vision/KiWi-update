#!/bin/bash

# Experiment 0
echo "Experiments Results" > results.txt
# Parameters order:
# itemsRange=2000000 threadsAmount=* preFillAmount=1000000 mapNumber=* experimentIndex=5 experimentsCount=5 testDurationSeconds=3.00 warmUpSeconds=1.00

export _JAVA_OPTIONS="-Xmx256m -Xms256m"
export PREFIX="env LD_PRELOAD=libjemalloc.so numactl --interleave=all"
export experimentsCount="5"

for experimentIndex in 0 1
do
    echo "experiment $experimentIndex"
    echo "==========================="
    for threadsNumber in 1 2 4 8 16 32 44
    do
        for mapNumber in 0 1 2 3
        do
            export cmd="$PREFIX java -d64 -server -ea benchmarks.Main 2000000 $threadsNumber 1000000 $mapNumber $experimentIndex $experimentsCount 5 3"
            echo $cmd
            $cmd >> results.txt
            tail -n 4 results.txt
        done
    done
done

for experimentIndex in 2 3
do
    echo "experiment $experimentIndex"
    echo "==========================="
    for threadsNumber in 1 2 4 8 16 32 44
    do
        for mapNumber in 0 1
        do
            export cmd="$PREFIX java -d64 -server -ea benchmarks.Main 2000000 $threadsNumber 1000000 $mapNumber $experimentIndex $experimentsCount 5 3"
            echo $cmd
            $cmd >> results.txt
            tail -n 4 results.txt
        done
    done
done
