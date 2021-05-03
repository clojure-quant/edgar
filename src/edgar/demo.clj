(ns edgar.demo
  (:require
   [clojure.core.async :refer [thread go <!]]
   [clojure.java.io]

   [edgar.edn :refer [edn-read edn-save]]
   [edgar.download :refer [dl-filing dl-primary]]
   [edgar.index :refer [filings-index]]
   [edgar.filing :refer [parse-filing]]
   [edgar.portfolio :refer [extract-pf-str]])
  ;(:gen-class)
  (:import java.io.File))

(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))

(defn nport-file [{:keys [cik no]} {:keys [fid]}]
  (str "data/nport/" cik "_" no "_" fid ".edn"))

(defn nport-file-xml [{:keys [cik no]}]
  (str "data/nport-xml/" cik "_" no ".xml"))

(defn dl-parse [f]
  (let [body (dl-filing f)]
    (when body
      (parse-filing body))))

(defn get-nport [f]
  (println "get-nport " f)
  (let [body (dl-primary f)]
    (when body
      (spit (nport-file-xml f) body)
      (let [pf (extract-pf-str body)]
        (edn-save (nport-file f pf) pf)))))


(defn get-nport-index [cik filename]
  (println "getting index.." filename)
  (let [filings (filings-index filename "NPORT-P" cik)]
    (edn-save "data/index.edn" filings)
    (println "downloading data.. #:" (count filings))
    (doall (map get-nport filings))))



(defn get-nport-all [cik]
  (doall (for [year [2019 2020 2021]
               q [1 2 3 4]]
     ; "data/index/2020-QTR4.tsv"
     ; "data/index/2021-QTR1.tsv"
           (let [filename (str "data/index/" year "-QTR" q ".tsv")]
             (get-nport-index cik filename)))))



(defn job1 []
  (thread
    (get-nport {:cik 1004655
                :no "0001752724-21-069935"})))

(defn job2 []
  (go  (<! (thread
             (println "start..")
             ;(get-nport-index 916488 "data/index/2018-QTR4.tsv" )
             ;(get-nport-all 916488)
              (get-nport-all nil)
             ;
             ))
       (println "done.")))



(defn -main []

  ;
  )

(comment
  (job1)
  (job2)
  (f)
  ;
  )


get-nport  {form NPORT-P, :date #object[java.time.LocalDate 0x37fbf3a5 2019-11-22], :cik 1003239, :name SEASONS SERIES TRUST, :no 0001752724-19-175480}
