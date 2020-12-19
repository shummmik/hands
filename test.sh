#!/bin/bash
./prepare.sh
./run.sh < src/test/scala/input.txt > src/test/scala/tmp.txt
diff src/test/scala/tmp.txt src/test/scala/output.txt
rm src/test/scala/tmp.txt
