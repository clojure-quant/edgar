(ns edgar.import.index
  (:require
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.index :refer [filings-index]]
   [edgar.import.nport :refer [nport]]
   )
  (:import java.io.File))

(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))

(defn get-nport-index [cik filename]
  (println "getting index.." filename)
  (let [filings (filings-index filename "NPORT-P" cik)]
    (edn-save "data/index.edn" filings)
    (println "downloading data.. #:" (count filings))
    (doall (map nport filings))))

(defn get-nport-all [cik]
  (doall (for [year [2019 2020 2021]
               q [1 2 3 4]]
     ; "data/index/2020-QTR4.tsv"
     ; "data/index/2021-QTR1.tsv"
           (let [filename (str "data/index/" year "-QTR" q ".tsv")]
             (get-nport-index cik filename)))))

(defn import-index []
    (println "importing index ..")
    ;(get-nport-index 916488 "data/index/2018-QTR4.tsv" )
    ;(get-nport-all 916488)
    (get-nport-all nil)
    (println "done."))

(comment
  (import-index)
  ;
  )
