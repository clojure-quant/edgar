(ns edgar.import.filings
  (:require
   [clojure.pprint :refer [print-table]]
   [edgar.sec.download :as download]
   [edgar.sec.xml2 :refer [parse-measurements]]
   [edgar.db.edn :as db-edn]))


(defn company-filings-type [cik form]
  (->> (download/download-submissions cik)
       (download/filings-recent)
       (filter #(= form (:form %)))
       ;(db-edn/save-companies)
       ))


; accession number
;  0001193125-15-118890 is the accession number, a unique identifier assigned automatically to an accepted submission by EDGAR. 
;  (0001193125) is the CIK of the entity submitting the filing. 
;- The next two numbers (15) represent the year. 
;- The last series of numbers represent a sequential count of submitted filings from that CIK.



(def fields
  ["InventoryNet"
        "AccountsPayableCurrent"
        "RevenueFromContractWithCustomerExcludingAssessedTax"
        "CostOfRevenue"
        "GeneralAndAdministrativeExpense"
        "NetIncomeLoss"
        "EarningsPerShareDiluted"
   ])


; goog-20220930_htm.xml
(defn download-10q-last-cik [cik]
  (let [last-filing (first  (company-filings-type cik "10-Q"))
        file-xml (clojure.string/replace
                  (:primary last-filing)
                            #".htm" "_htm.xml")
        url-filing (download/url-filing
                    cik
                    (:no-access last-filing)
                    file-xml)
        xml-str (download/dl-t url-filing)
        measurements (parse-measurements xml-str fields) ]
    (println "cik:" cik " #measurements: " (count measurements))
    measurements
      ))

(defn import-10q-last [symbol]
  (let [cik (-> (db-edn/find-company-with-ticker symbol)
                :cik)
        ms (download-10q-last-cik cik)
         edn-fn (str "data/" symbol ".edn")
        ]
     (db-edn/edn-save edn-fn ms)
     (count ms)))


(defn import-10q-last-safe [symbol]
  (try
    (import-10q-last symbol)
    (catch Exception ex
      (println "error for: " symbol)
      )))




(comment
  (dissoc {:a 1 :b 2} :b :a)
  (->> (company-filings-type 1652044 "10-Q")
      (map #(dissoc % :xbrl  :size :act :xbrl-il :form :items :primary-desc :no-film :no-file))
;      count
      print-table
      )
  ;  https://www.sec.gov/ix?doc=/Archives/edgar/data/1652044/000165204422000090/goog-20220930.htm

  (->> (company-filings-10Q 1652044)
       #_(print-table
        [:date-report
        ; :date-accept
         :no-access]
        )
       first
      ; (filing-url
       println
       
       )

  (-> (import-10q-last 1652044)
      ;count
      println
      )


  (db-edn/build-ticker-lookup)
  (import-10q-last "GOOG")
  (import-10q-last "AAPL")
  (import-10q-last "TTE")

  (map
   import-10q-last-safe
   (keys @db-edn/company-ticker-lookup))


  (pmap
   import-10q-last-safe
   (keys @db-edn/company-ticker-lookup))

  
;
 ) 
    
