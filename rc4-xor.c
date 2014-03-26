/* fun with stream ciphers */
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <assert.h>
#include <stdint.h>
#include <fcntl.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>

int
main(int argc, char **argv)
{
    if (argc != 2) {
        errno = EIO; perror("need only path to rc4'd file...");
    }
    size_t fsize = 56;
    int fds[2];
    fds[0] = open(argv[1], O_RDONLY);
    assert(-1 != fds[0]);
    const char *fname = "mail.new.rc4";
    fds[1] = open(fname, O_RDWR | O_CREAT, 0666);
    assert(-1 != fds[1]);
    assert(0 == ftruncate(fds[1], fsize));
    uint8_t buf = 0;
    char *oldtxt = "Hey Boss. I've always hated you. I quit. Sincerely, Bob";
    char *newtxt = "Hey Boss. I'm taking the day off tomorrow. Regards, Bob";
    assert(strlen(newtxt) + 1 == fsize);
    for (int i = 0; read(fds[0], &buf, sizeof(buf)); ++i) {
        uint8_t n = buf ^ (oldtxt[i] ^ newtxt[i]); 
        write(fds[1], &n, sizeof(n));
    }
    printf("\nnew rc4'd file written to: %s\n", fname);
    close(fds[0]); close(fds[1]);
    return EXIT_SUCCESS;
}
