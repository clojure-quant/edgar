(ns edgar.demo
  (:require
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.download :refer [dl-filing dl-primary]]
   [edgar.index :refer [filings-index]]
   [edgar.filing :refer [ parse-filing]]
   [edgar.portfolio :refer [extract-pf-str]]
   )
  ;(:gen-class)
  )

(defn get-filings []
  (let [filings (filings-index
                 ;"data/index/2021-QTR1.tsv"
                 "data/index/2020-QTR4.tsv"
                 "NPORT-P"
                 100334
                               ;1004655
                 )]
    (edn-save "report/index.edn" filings)
    filings
;(take 2 filings )
    )
  )


(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))

(defn nport-file [{:keys [cik no]}]
  (str "data/nport/" cik "-" no "-nport.edn"))

(defn nport-file-xml [{:keys [cik no]}]
  (str "data/nport-xml/" cik "_" no ".xml"))

(defn dl-parse [f]
  (let [body (dl-filing f)]
    (when body
      (parse-filing body))))

(defn get-nport [f]
  (let [body (dl-primary f)]
    (when body
      (spit (nport-file-xml f) body)
      (let [pf (extract-pf-str body)]
        (edn-save (nport-file f) pf)
        )
      )))

(defn -main
  []
 
  (get-nport {:cik 1004655
              :no "0001752724-21-069935"})

  (->> (get-filings) 
      (map get-nport)
      )

  ;
)
           
     
    