(ns tair.repl
  (:import [java.net URL])
  (:use [tair.core])
  (:require [dynapath.util :as dp]
            [fs.core :as fs]))

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

(defn env
  "Show the current settings."
  []
  (println "Current settings:")
  (println "\tconfig-id:" @config-id)
  (println "\tnamespace:" @tnamespace))

(defn help
  "Prints helps"
  []
  (let [help-text
        "\t\t (set-config-id 'ldbcomm')      -- set your config-id
         (set-namespace 89)             -- set your namespace
         (query 'key')                  -- query something from tair
         (put 'key' 'value')            -- put something into tair(expire in 24hr)
         (put 'key' 'value' 120)        -- put something into tair(expire in 2min)
         (delete 'key')                 -- delete something from tair
         (add-jar '/tmp/test.jar')      -- add a jar into classpath (if you have put an object into tair)
         (env)                          -- show the current value of config-id and namespace"]
    (println (.replaceAll help-text "'" "\""))))

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


;; init tair
(.init @tair)

;; add the jar in ~/.tair-repl to the classpath
(let [jars (fs/list-dir (fs/expand-home "~/.tair-repl"))]
  (doseq [jar jars]
    (add-jar0 (fs/expand-home (str "~/.tair-repl/" jar)))))
