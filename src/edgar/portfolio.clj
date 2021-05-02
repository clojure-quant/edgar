(ns edgar.portfolio
  (:require
   [clojure.set :refer [rename-keys]]
   [edgar.xml :refer [p-str p-file]]
   [edgar.helper])
  (:import
   [java.text NumberFormat]
   [java.util Locale])
  )

(def n-port #{:borrowers :monthlyTotReturns :invstOrSecs})

;:valUSD "6530379.26000000"
;:pctVal "0.318938076609"
;:title "EXLSERVICE HOLDINGS INC"
;:invCountry "US"
;:balance "77173.00000000"
;:isRestrictedSec "N"
;:cusip "302081104"

(defn read-num [s]
  (let [format (NumberFormat/getNumberInstance Locale/US)]
    (.parse format s)))

(read-num "6530379.26000000")

(defn holding [h]
  (let [h (select-keys h [:title :balance :valUSD :pctVal :cusip])
        {:keys [balance valUSD pctVal]} h]
    
    (-> (assoc h 
           :balance (read-num balance)
           :valUSD (read-num valUSD)
           :pctVal (read-num pctVal)
           
           )
        (rename-keys {:balance :qty}))
        ))

(defn extract-pf []
  (let [raw (p-file n-port "demodata/N-PORT/primary_doc.xml")
        pf (get-in raw [:edgarSubmission :formData :invstOrSecs])
        cik (get-in raw [:edgarSubmission :headerData :filerInfo :filer :issuerCredentials :cik])
        ginfo (get-in raw [:edgarSubmission :formData :genInfo])
        nav (get-in raw [:edgarSubmission :formData :fundInfo :totAssets])
        {:keys [seriesName repPdEnd repPdDate]} ginfo
        pf (map holding pf)
        d {:fund seriesName
           :cik cik
           :date-filed repPdDate
           :date-report repPdEnd
           :nav (read-num nav)
           :holdings pf}]
    (edgar.helper/save "report/test.edn" d)))

(extract-pf)



