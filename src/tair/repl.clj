(ns tair.repl
  (:import [com.taobao.tair TairManager]
           [com.taobao.tair.impl.mc MultiClusterTairManager]))

(defn mk-tair [config-id]
  (let [tair (MultiClusterTairManager.)
        _ (doto tair (.setConfigID config-id)
                (.setDynamicConfig true))]
    tair))

(def default-expire-time 86400)

(def tnamespace (atom 89))
(def version (atom 0))
(def config-id (atom "b2bcomm-daily"))

(def tair (atom (mk-tair @config-id)))
(.init @tair)

(defn set-namespace
  "Sets the tair namespace you want to operate in."
  [ns]
  (reset! tnamespace ns))

(defn set-config-id
  [new-config-id]
  (reset! config-id new-config-id)
  (reset! tair (mk-tair @config-id))
  (.init @tair))

(defn query
  "Query the specified key in the @tnamespace"
  [key]
  (let [obj (.get @tair @tnamespace key)]
    obj))

(defn put
  "put something into @tair"
  ([key value]
     (put key value default-expire-time))
  ([key value expire-time]
     (.put @tair @tnamespace key value @version expire-time)))

(defn delete [key]
  "Delete something from @tair."
  (.delete @tair @tnamespace key))
