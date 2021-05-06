(ns edgar.app
  (:require
   [edgar.db :as db]
   [edgar.job.dummy :refer [add-dummy-data]]
   [edgar.job.info :refer [print-db-info]]
   [edgar.job.goldly :refer [goldly-run]]
   [edgar.import.index :refer [import-index import-index-demo]])
  (:gen-class))

(defn -main [job]
  (println "running job: " job)
  (case job
    "dummy-data" (do (db/create!)
                     (add-dummy-data)
                     (print-db-info)
                     (db/close!))
    "info"  (do (db/connect!)
                (print-db-info)
                (db/close!))

    "goldly" (do (db/connect!)
                 (print-db-info)
                 (goldly-run))

    "import" (do (db/connect!)
                 (import-index)
                 (db/close!))

    "import-demo" (do (db/connect!)
                      (import-index-demo)
                      (db/close!))))

