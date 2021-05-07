(ns edgar.analysis.report
  (:require
   [edgar.db :as db]
   [edgar.edn :refer [edn-read edn-save]])
  )

(defn- fund-sid [f]
  (let [fund-id  (first (:fund/reports f))
        fund-db-id (:db/id fund-id)
        fund (db/fund-bydbid fund-db-id)]
    (:fund/sid fund)))

(defn nport-file [cik no sid]
  (str "data/nport/" cik "_" no "_" sid ".edn"))


(defn- load-report-impl [sid f]
  (let [cik (:db/id (:report/cik f))
        no (:report/no f)
        f (nport-file cik no sid)]
    (println "loading: " f)
    (edn-read f)))

(defn load-report [report-db-id]
  (let [r (db/report report-db-id)
        sid (fund-sid r)]
  (load-report-impl sid r)
  ))

(defn load-reports [fund-db-id]
  (let [reports (db/reports-for-fund-by-dbid fund-db-id)
        sid (fund-sid (first reports))]
    (map (partial load-report-impl sid) reports)
  ))

(comment
  ; data/nport/1282693_0001145549-21-012804_S000036810.edn
  ; data/nport/1282693_0001145549-21-012804.edn
  
  (db/report 43999)
  (load-report 43999)
  (load-report 21418)
  
  
   (db/fund "S000058036") ; :date-filed "2021-04-27", :date-report "2021-02-28"
   (db/reports-for-fund-by-dbid 496)
   (db/reports-for-fund-by-dbid 29922)
  
  (load-reports 496)

   (->> (load-reports 5841)
        ;(map #(select-keys % [:date-filed :date-report :date-fiscal]))
        ;(map :holdings)
        first
       ; :holdings
        )
  
;
)
