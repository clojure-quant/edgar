(ns edgar.import.filings
  (:require
   [clojure.pprint :refer [print-table]]
   [edgar.sec.download :refer [download-submissions filings-recent]]
   [edgar.db.edn :as db-edn]))


(defn company-filings-type [cik form]
  (->> (download-submissions cik)
       (filings-recent)
       (filter #(= form (:form %)))
       ;(db-edn/save-companies)
       ))


; accession number
;  0001193125-15-118890 is the accession number, a unique identifier assigned automatically to an accepted submission by EDGAR. 
;  (0001193125) is the CIK of the entity submitting the filing. 
;- The next two numbers (15) represent the year. 
;- The last series of numbers represent a sequential count of submitted filings from that CIK.


(defn company-filings-10Q [cik]
  (->> (company-filings-type cik "10-Q")
       (map #(select-keys % [:date-report :date-accept :no-access]))))
  



(comment
  (dissoc {:a 1 :b 2} :b :a)
  (->> (company-filings-type 1652044 "10-Q")
      (map #(dissoc % :xbrl  :size :act :xbrl-il :form :items :primary-desc :no-film :no-file))
;      count
      print-table
      )
  ;  https://www.sec.gov/ix?doc=/Archives/edgar/data/1652044/000165204422000090/goog-20220930.htm

  (print-table
   (company-filings-10Q 1652044))
  
;
 ) 
    
