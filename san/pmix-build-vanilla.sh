#!/bin/bash

make clean && \
./configure \
--prefix=/home/samuel/local/pmix \
--enable-devel-check --enable-debug \
&& make -j install
