(ns edgar.sec.portfolio
  (:require
   [clojure.string :as str]
   [clojure.set :refer [rename-keys]]
   [edgar.sec.xml :refer [p-str p-file]]
   [edgar.edn])
  (:import
   [java.text NumberFormat]
   [java.util Locale]))

(def n-port #{:borrowers :monthlyTotReturns :invstOrSecs})

;:valUSD "6530379.26000000"
;:pctVal "0.318938076609"
;:title "EXLSERVICE HOLDINGS INC"
;:invCountry "US"
;:balance "77173.00000000"
;:isRestrictedSec "N"
;:cusip "302081104"

(defn read-num [s]
  (if (or (nil? s) (= s "N/A"))
    0
  (let [format (NumberFormat/getNumberInstance Locale/US)
        s (str/trim s)
        ]
    (.parse format s))))


(defn holding [h]
  (let [h (select-keys h [:title 
                          :balance :valUSD :pctVal 
                          :cusip
                          :assetCat
                          :issuerCat
                          :invCountry
                          ])
        {:keys [balance valUSD pctVal]} h]

    (-> (assoc h
               :balance (read-num balance)
               :valUSD (read-num valUSD)
               :pctVal (read-num pctVal))
        (rename-keys {:balance :qty}))))

(defn extract [raw]
  (let [pf (get-in raw [:edgarSubmission :formData :invstOrSecs])
        cik (get-in raw [:edgarSubmission :headerData :filerInfo :filer :issuerCredentials :cik])
        ginfo (get-in raw [:edgarSubmission :formData :genInfo])
        nav (get-in raw [:edgarSubmission :formData :fundInfo :totAssets])
        {:keys [seriesId seriesName regName repPdEnd repPdDate]} ginfo
        pf (map holding pf)
        d {:fund seriesName
           :sid seriesId
           :cik (if (str/blank? cik) 0 (long (Integer/parseInt cik)))
           :advisor regName
           :date-filed repPdDate
           :date-report repPdEnd
           :nav (read-num nav)
           :holdings pf}]
    d
    ))

(defn extract-pf-file [filename]
  (let [raw (p-file n-port filename)
        pf (extract raw)]
    pf))

(defn extract-pf-str [str]
  (let [raw (p-str n-port str)
        pf (extract raw)]
    pf))


(comment
  (read-num "6530379.26000000")

  (->>  (extract-pf-file "demodata/N-PORT/primary_doc.xml")
        (edgar.edn/edn-save "report/test2.edn"))

;  
  )





