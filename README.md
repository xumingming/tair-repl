# tair-repl

A commandline client for taobao [Tair](https://github.com/taobao/tair).

# Install

* Download this [jar](http://clojars.org/repo/tair-repl/tair-repl/1.0.0-SNAPSHOT/tair-repl-1.0.0-20130108.113042-1.jar)

* Rename the downloaded jar name to `tair-repl.jar` [Actually this step is optional]
* Run:

``` bash
java -jar tair-repl.jar
```

# Usage

``` bash
xumingmingv:tair-repl(git:master)$ java -jar dist/tair-repl-1.0.0-SNAPSHOT-standalone.jar 
log4j:WARN No appenders could be found for logger (com.taobao.tair.ResultCode).
log4j:WARN Please initialize the log4j system properly.
	 set-config-id <config-id>        -- set config-id
	 set-namespace <namespace>        -- set namespace
	 put <key> <value> [<value-type>] -- put something into tair
	 get <key>                        -- get something from tair
	 delete <key>                     -- delete something from tair
	 settings                         -- show the current settings
	 add-jar <path-to-jar>            -- add a jar file to the classpath(if you need to put an object into tair)
 => settings
Current settings:
	config-id: ldbcommon-daily
	namespace: 652
 => 
add-jar         delete          exit            get             put             set-config-id   set-namespace   settings        
 => put hello world
SUCCESS!
 => get hello
world
 => delete hello
SUCCESS!
 => set-namespace 89
namespace set to  89
 => set-config-id b2bcomm-daily
Config-id set to  b2bcomm-daily
 => get hello
world
 => exit
```


# License

Copyright (C) 2012 xumingming

Distributed under the Eclipse Public License, the same as Clojure.
