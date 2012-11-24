# tair-repl

A Simple REPL for taobao tair.

# Install

* Install [leiningen](https://github.com/technomancy/leiningen)
* Clone the repo: 

``` bash
git clone https://github.com/xumingming/tair-repl.git
```

* Add `Clojars.org` to your `~/.m2/settings.xml`

``` xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

* Fetch the dependencies and run: 

``` bash
cd tair-repl && lein deps && lein repl
```

* For the commands to use, have a look at the `Usage` section 

# Usage

``` clojure
(use 'tair.repl)
(put "key" "value")     ;; put something into tair with default expire time(24hr)
(put "key" "value" 24)  ;; put something into tair with expire time of 24 seconds
(query "key")           ;; get something from tair
(delete "key")          ;; delete something from tair
(set-namespace 99)      ;; change the namespace you want to operate in
(set-config-id "xxx")   ;; change the config-id of your tair
;; if you have put a Java Object into the cache, you can add the jar 
;; which defines the Java class to tair-repl's classpath
(add-jar "/tmp/test.jar")
(put "key-for-an-object" (doto (Person.) (.setName "james") (.setAge 20)))
;; #<ResultCode code=0, msg=success>

;; then you query it, you will get a readable string representation
;; for the object rather than something like "serialize error"
;; Clojure User: this "string representation" is actually a map
;;               so you can process it as you wish!
(query "key-for-an-object")
;; {"name" "james", "age" 20}

;; At last, the help command:
(help)                  ;; show the help text
```


# License

Copyright (C) 2012 xumingming

Distributed under the Eclipse Public License, the same as Clojure.
