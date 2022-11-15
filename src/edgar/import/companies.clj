(ns edgar.import.companies
  (:require
   [edgar.sec.download :refer [download-company-tickers]]
   [edgar.db.edn :as db-edn]))


(defn update-company-tickers []
  (->> (download-company-tickers)
       (db-edn/save-companies)))


(comment
  (update-company-tickers)

;
 ) 
    
