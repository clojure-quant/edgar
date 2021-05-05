(ns edgar.job.info
  (:require
   [edgar.db :as db]))


(defn print-db-info []
  (println "loading db stats..")
  (let [funds (db/fund-list)
        mgrs (db/manager-list)
        rps (db/all-reports)]
    (println "info")
    (println "fund list: " funds)
    (println "funds: " (count funds)
             "mgrs: " (count mgrs)
             "reports: " (count rps))))