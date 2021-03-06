(ns edgar.import.index
  (:require
   [clojure.java.io :as io]
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.sec.index :refer [filings-index]]
   [edgar.import.nport :refer [nport]])
  (:import java.io.File))


(defn get-nport-index [cik filename]
  (println "getting index.." filename)
  (let [filings (filings-index filename "NPORT-P" cik)]
    (edn-save "data/index.edn" filings)
    (println "downloading data.. #:" (count filings))
    (doall (map nport filings))))

(defn get-nport-all [cik]
  (doall (for [year [2021
                     2020 
                     2019]
               q [4 3 2 1]]
     ; "data/index/2020-QTR4.tsv"
     ; "data/index/2021-QTR1.tsv"
           (let [filename (str "data/index/" year "-QTR" q ".tsv")]
             (when (.exists (io/file filename))
               (println "file existing: " filename)
               (get-nport-index cik filename))))))

(defn import-index []
  (println "importing index ..")
    ;(get-nport-index 916488 "data/index/2018-QTR4.tsv" )
  (get-nport-all 0)
    (get-nport-all nil)
  (println "done."))

(defn import-index-demo []
  (println "importing index ..")
    ;(get-nport-index 916488 "data/index/2018-QTR4.tsv" )
  (get-nport-all 916488)
  (println "done."))

(comment
  (import-index)


  ;
  )
