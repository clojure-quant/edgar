(ns edgar.app
  (:require
   [clojure.core.async :refer [thread go <!]]
   [clojure.java.io :as io]
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.download :refer [dl-filing dl-primary]]
   [edgar.index :refer [filings-index]]
   [edgar.filing :refer [parse-filing]]
   [edgar.portfolio :refer [extract-pf-str]]
   [edgar.db :refer [add-report]]
   )
  (:gen-class)
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

(defn save [f {:keys [sid cik no] :as pf}]
  (when (and cik sid no)
    (add-report pf)
    (edn-save (nport-file f pf) pf))
  )


(defn get-nport 
  ([f]
   (get-nport f true))
  ([f keep?]
  (let [f-xml (nport-file-xml f)]
    (if (and keep? (.exists (io/file f-xml)))
      (println "existing: " f)
      (try
        (do
        (println "get-nport " f)
        (let [body (dl-primary f)]
          (when body
            (spit f-xml body)
            (let [pf (extract-pf-str body)
                  pf (assoc pf :no (:no f))
                  ]
              (save f pf)
              ))))
          (catch Exception e
              (println "ex in: " f)   
                 )
      )))))


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
    (println "importing..")
    (get-nport-all nil)
    (println "done."))

(comment
  (job1)
  ;(job2)
  (get-nport {:cik 1660765
              :name "Clayton Street Trust" 
              :no "0001741773-19-001031"} false)

  ;
  )
