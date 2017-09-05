

export _JAVA_OPTIONS="-Xmx2g -Xms2g"
export PREFIX="env LD_PRELOAD=libjemalloc.so numactl --interleave=all"
export experimentsCount="5"

export mapNumber="1"
export experimentIndex="3"

for threadsNumber in 16 32 44
do
    export cmd="$PREFIX java -d64 -server -ea benchmarks.Main 2000000 $threadsNumber 1000000 $mapNumber $experimentIndex $experimentsCount 5 3"
    echo $cmd
    $cmd >> results3.txt
done




export mapNumber="3"
export experimentIndex="1"
export threadsNumber="44"

export cmd="$PREFIX java -d64 -server -ea benchmarks.Main 2000000 $threadsNumber 1000000 $mapNumber $experimentIndex $experimentsCount 5 3"
echo $cmd
$cmd >> results3.txt

