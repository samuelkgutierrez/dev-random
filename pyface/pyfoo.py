#!/usr/bin/env python

from ctypes import cdll

lib = cdll.LoadLibrary('./libfoo.so')

lib.library_foo_init()

print('all done!')
