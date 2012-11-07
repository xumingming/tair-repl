(ns tair.repl
  (:import [com.taobao.tair TairManager]
           [com.taobao.tair.impl.mc MultiClusterTairManager]))

(defn mk-tair [config-id]
  (let [tair (MultiClusterTairManager.)
        _ (doto tair (.setConfigID config-id)
                (.setDynamicConfig true))]
    tair))

(def *namespace* 99)
(def *expire-time* 86400)
(def *version* 0)
(def *config-id* "b2bcomm-daily")

(def *tair* (mk-tair *config-id*))
(.init *tair*)

(defn query
  "Query the specified key in the namespace"
  [key]
  (let [obj (.get *tair* *namespace* key)]
    obj))

(defn put
  "put something into tair"
  [key value]
  (.put *tair* *namespace* key value *version* *expire-time*))

(defn delete [key]
  "Delete something from tair."
  (.delete *tair* *namespace* key))
