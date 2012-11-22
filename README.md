# tair-repl

A Simple REPL for taobao tair.

# Usage

``` clojure
lein repl
(use 'tair.repl)
(put "key" "value")     ;; put something into tair with default expire time(24hr)
(put "key" "value" 24)  ;; put something into tair with expire time of 24 seconds
(query "key")           ;; get something from tair
(delete "key")          ;; delete something from tair
(set-namespace 99)      ;; change the namespace you want to operate in
(set-config-id "xxx")   ;; change the config-id of your tair
```


# License

Copyright (C) 2012 xumingming

Distributed under the Eclipse Public License, the same as Clojure.
