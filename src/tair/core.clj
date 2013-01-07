(ns tair.core
  (:import [com.taobao.tair TairManager]
           [com.taobao.tair.impl.mc MultiClusterTairManager]
           [com.alibaba.fastjson JSON]
           [java.net URL]
           [java.util Map List])
  (:require [clojure.walk :as walk]))

(declare pretify-result)

(defn mk-tair
  "Create a tair instance."
  ([config-id]
     (mk-tair config-id true))
  ([config-id dynamic?]
     (doto (MultiClusterTairManager.) (.setConfigID config-id)
           (.setDynamicConfig dynamic?))))

(defn query
  "Query the value of the specified key from the specified namespace."
  [tair namespace key]
  (let [obj (.get tair namespace key)
        obj (if (and (not (nil? obj))
                     (not (nil? (-> obj .getValue)))
                     (not (nil? (-> obj .getValue .getValue))))
              (-> obj .getValue
                  .getValue
                  pretify-result)
              nil)]
    obj))

(defn put
  "Put the key value pair into the specified namespace with the specified expiretime.

  If 0 is specified for the expiretime, it means it will not expire"
  ([tair namespace key value]
     (put tair namespace key value 0))
  ([tair namespace key value version]
     (.put tair namespace key value version 0))
  ([tair namespace key value version expire-time]
     (put tair namespace key value version expire-time)))


(defn delete
  "Delete the specified key from tair"
  [tair namespace key]
  (.delete tair namespace key))

(defn- object-to-json [obj]
  (JSON/toJSON obj))

(defn clojurify-structure [s]
  (walk/prewalk (fn [x]
              (cond (instance? Map x) (into {} x)
                    (instance? List x) (vec x)
                    true x))
           s))

(defn pretify-result [obj]
  (-> obj object-to-json clojurify-structure))

