#!/bin/bash

san="-fsanitize=undefined"
#san="-fsanitize=address"
#san="-fsanitize=memory -fsanitize-memory-track-origins=2 -fsanitize-ignorelist=/home/samuel/devel/openpmix/msan-il.txt"

make clean && \
./configure \
CC=clang \
"CFLAGS=-g -O0 ${san} -fno-omit-frame-pointer -fno-optimize-sibling-calls" \
LIBS="${san}" \
--prefix=/home/samuel/local/pmix \
--enable-devel-check --enable-debug \
--with-hwloc=/home/samuel/local/hwloc \
--with-libevent=/home/samuel/local/libev \
&& make -j install
