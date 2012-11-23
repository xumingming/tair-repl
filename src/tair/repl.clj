(ns tair.repl
  (:import [com.taobao.tair TairManager]
           [com.taobao.tair.impl.mc MultiClusterTairManager]
           [com.alibaba.fastjson JSON]
           [java.net URL])
  (:require [dynapath.util :as dp]
            [fs.core :as fs]))

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


(defn set-namespace
  "Sets the tair namespace you want to operate in."
  [ns]
  (reset! tnamespace ns))

(defn set-config-id
  [new-config-id]
  (reset! config-id new-config-id)
  (reset! tair (mk-tair @config-id))
  (.init @tair))

(declare pretify-result)
(defn query
  "Query the specified key in the @tnamespace"
  [key]
  (let [obj (.get @tair @tnamespace key)]
    (if (and (not (nil? obj))
             (not (nil? (-> obj .getValue)))
             (not (nil? (-> obj .getValue .getValue))))
      (-> obj .getValue
          .getValue
          pretify-result)
      nil)))

(defn put
  "put something into @tair"
  ([key value]
     (put key value default-expire-time))
  ([key value expire-time]
     (.put @tair @tnamespace key value @version expire-time)))

(defn delete [key]
  "Delete something from @tair."
  (.delete @tair @tnamespace key))

(defn get-classloader []
  (.getClassLoader Compiler))

(defn get-classpath []
  (-> (get-classloader) dp/all-classpath-urls))

(defn copy-jar [jar]
  ;; mk the ~/.tair-repl dir
  (when (not (fs/exists? (fs/expand-home "~/.tair-repl")))
    (fs/mkdir (fs/expand-home "~/.tair-repl")))
  (let [from-path (fs/expand-home jar)
        to-path (fs/expand-home (str "~/.tair-repl/" (fs/base-name jar)))]
    (println "from-path:" from-path)
    (println "to-path:" to-path)
    (fs/copy (fs/absolute-path from-path) (fs/absolute-path to-path))
    (fs/absolute-path to-path)))

(defn add-jar0 [path]
  (let [classloader (get-classloader)]
    (dp/add-classpath-url classloader (URL. (str "file:" path)))))

(defn add-jar [path]
  (let [real-path (copy-jar path)]
    (add-jar0 real-path)))

(defn- object-to-json [obj]
  (JSON/toJSON obj))

(defn pretify-result [obj]
  (-> obj object-to-json .toString
          (.replaceAll "\"" "'")))


;; init tair
(.init @tair)

;; add the jar in ~/.tair-repl to the classpath
(let [jars (fs/list-dir (fs/expand-home "~/.tair-repl"))]
  (doseq [jar jars]
    (add-jar0 (fs/expand-home (str "~/.tair-repl/" jar)))))
