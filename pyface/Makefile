all:
	$(CC) -c -fPIC library_foo.c -o foo.o
	$(CC) -shared -Wl,-soname,libfoo.so -o libfoo.so  foo.o

clean:
	rm -f libfoo.so foo.o


