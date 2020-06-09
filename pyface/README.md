`make` should generate `libfoo.so`.

`pyfoo.py` loads and calls a function defined in `libfoo.so`.

Output should look something like this:
```
::: python3 ./pyfoo.py
library foo initialized!
all done!
```
