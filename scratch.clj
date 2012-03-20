(ns scratch
  (:use [cljx.rules :only [cljs-rules]] :reload-all)
  (:require [clojure.string :as string]
            [jonase.kibit.core :as kibit])
  (:import [java.io File]))

(set! *print-meta* true)
(set! *print-meta* false)


;;Taken from clojure.tools.namespace
(defn cljx-source-file?
  "Returns true if file is a normal file with a .cljx extension."
  [^File file]
  (and (.isFile file)
       (.endsWith (.getName file) ".cljx")))

(defn find-cljx-sources-in-dir
  "Searches recursively under dir for CLJX files.
Returns a sequence of File objects, in breadth-first sort order."
  [^File dir]
  ;; Use sort by absolute path to get breadth-first search.
  (sort-by #(.getAbsolutePath ^File %)
           (filter cljx-source-file? (file-seq dir))))



(defn process [filename]
  (let [{:keys [cljs clj]} (reduce (fn [cur [func args]] (apply func cur args))
                                   {:cljx (toplevel-forms-in filename)}
                                   (seq @middleware))
        output-base (string/replace filename "cljx" "")
        warning (str ";;This file autogenerated from \n;;\n;;  " filename "\n;;\n")]
    
    (spit (str output-base "clj") (str warning (string/join "\n" clj)))
    (spit (str output-base "cljs") (str warning (string/join "\n" cljs)))))

(process "test/cljx/testns/core.cljx")


(kibit/check-file "test/cljx/testns/core.cljx" :rules cljs-rules)
