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
  
  (load-report 43999)
  (load-report 21418)
  
   (db/reports-for-fund-by-dbid 6468)
   (db/reports-for-fund-by-dbid 21417)
  
  (load-reports 6468)
  
;
)
