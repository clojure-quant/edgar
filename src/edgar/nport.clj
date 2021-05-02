(ns edgar.nport
  (:require
   
  [edgar.xml :refer [p-str p-file]]
   [edgar.helper]
   ))


(def n-port #{:borrowers :monthlyTotReturns :invstOrSecs})

;:valUSD "6530379.26000000"
;:pctVal "0.318938076609"
;:title "EXLSERVICE HOLDINGS INC"
;:invCountry "US"
;:balance "77173.00000000"
;:isRestrictedSec "N"
;:cusip "302081104"


(defn holding [h]
  (select-keys h [:title :balance :valUSD :pctVal])
  )

(defn extract-pf []
  (let [raw (p-file n-port "demodata/N-PORT/primary_doc.xml")
        pf (get-in raw [:edgarSubmission :formData :invstOrSecs])

        pf (map holding pf)
        ]
    


    (edgar.helper/save "report/test.edn" pf)
 ))

(extract-pf)



