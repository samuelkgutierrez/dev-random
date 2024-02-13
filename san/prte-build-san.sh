#!/bin/bash

san="-fsanitize=undefined"
#san="-fsanitize=address"
#san="-fsanitize=memory -fsanitize-memory-track-origins=2 -fsanitize-ignorelist=/home/samuel/devel/openpmix/msan-il.txt"

make clean && \
./configure \
CC=clang \
"CFLAGS=-g -O0 ${san} -fno-omit-frame-pointer" \
LIBS="${san}" \
--prefix=/home/samuel/local/prrte \
--with-pmix=/home/samuel/local/pmix \
--with-libevent=/home/samuel/local/libev \
--enable-debug \
&& make -j install
