(ns tair.repl
  (:refer-clojure :exclude [get])
  (:import [java.net URL])
  (:use [tair.core])
  (:require [dynapath.util :as dp]
            [fs.core :as fs]
            [clojure.string :as string]
            [colorize.core :as color]
            [clojure.pprint :as pprint])
  (:gen-class))

(def tnamespace (atom 652))
(def version (atom 0))
(def config-id (atom "ldbcommon-daily"))

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
  (println "\t set-config-id <config-id>        -- set config-id")
  (println "\t set-namespace <namespace>        -- set namespace")
  (println "\t put <key> <value> [<value-type>] -- put something into tair")
  (println "\t get <key>                        -- get something from tair")
  (println "\t delete <key>                     -- delete something from tair")
  (println "\t settings                         -- show the current settings")
  (println "\t add-jar <path-to-jar>            -- add a jar file to the classpath(if you need to put an object into tair)"))

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
    (fs/copy (fs/absolute-path from-path) (fs/absolute-path to-path))
    (fs/absolute-path to-path)))

(defn add-jar0 [path]
  (let [classloader (get-classloader)]
    (dp/add-classpath-url classloader (URL. (str "file:" path)))))

(defn add-jar [path]
  (let [path (fs/absolute-path (fs/expand-home path))
        jars (if (fs/directory? path)
               (map #(str path "/" %) (fs/list-dir path))
               [path])]
    (doseq [jar jars
            :let [real-path (copy-jar jar)]]
      (add-jar0 real-path)
      (println "Added jar" jar "to the classpath."))))

;; the MAIN loop
(defn -main []
  ;; init tair
  (.init @tair)

  ;; add the jar in ~/.tair-repl to the classpath
  (let [jars (fs/list-dir (fs/expand-home "~/.tair-repl"))]
    (doseq [jar jars]
      (add-jar0 (fs/expand-home (str "~/.tair-repl/" jar)))))

  (loop [input "help"]
    (try
      (let [argv (if (nil? input)
                   ["exit"]
                   (string/split input #" "))
            command (first argv)
            argv (rest argv)]
        (condp = command
          "set-config-id" (let [new-config-id (first argv)]
                            (set-config-id new-config-id)
                            (println "Config-id set to " new-config-id))
        
          "set-namespace" (let [new-namespace (Integer/valueOf (first argv))]
                            (set-namespace new-namespace)
                            (println "namespace set to " new-namespace))
        
          "put"   (if (< (count argv) 2)
                    (println "put expects at least 2 args," (count argv) "given.")
                    (let [key (first argv)
                          value (second argv)
                          value-type (if (> (count argv) 2)
                                       (nth argv 2)
                                       nil)
                          value (condp = value-type
                                  "int" (Integer/valueOf value)
                                  "long" (Long/valueOf value)
                                  value)
                          result-code (put @tair @tnamespace key value)]
                      (if (= (:code result-code) 0)
                        (println "SUCCESS!")
                        (println "FAIL! code:" (:code result-code) ", message:" (:message result-code)))))
        
          "get" (if (not= (count argv) 1)
                  (println "get expects 1 arg," (count argv) "given.")
                  (let [key (first argv)
                        ret (get @tair @tnamespace key)]
                    (pprint/pprint ret)))
        
          "delete" (if (not= (count argv) 1)
                     (println "delete expects 1 arg," (count argv) "given.")
                     (let [key (first argv)
                           result-code (delete @tair @tnamespace key)]
                       (if (= (:code result-code) 0)
                         (println "SUCCESS!")
                         (println "FAIL! code:" (:code result-code) ", message:" (:message result-code)))))
        
          "settings"    (env)

          "add-jar" (let [jar (first argv)]
                      (add-jar jar))
        
          "exit" (System/exit 0)

          ;; if command is empty, do nothing
          ""  (print)
        
          (help)))
      (catch Throwable e
        (println "ERROR: " e)
        (.printStackTrace e)))

    (print (str " => "))
    (flush)
    (recur (read-line))))
