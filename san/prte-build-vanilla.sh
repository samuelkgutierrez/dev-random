#!/bin/bash

make clean && \
./configure \
--prefix=/home/samuel/local/prrte \
--with-pmix=/home/samuel/local/pmix --enable-debug \
&& make -j install
