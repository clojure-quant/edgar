(ns edgar.app
  (:require
   [edgar.db :as db]
   [edgar.job.dummy :refer [add-dummy-data]]
   [edgar.job.info :refer [print-db-info]]
   [edgar.job.goldly :refer [goldly-run]]
   )
  (:gen-class))

(defn -main [job]
  (println "running job: " job)
  (case job    
    "dummy-data" (do (db/create!)
                     (add-dummy-data)
                     (print-db-info)
                     (db/close!)
                     )
    "info"  (do (db/connect!)
                (print-db-info)
                (db/close!))
    
    "goldly" (do (db/connect!)
                 (print-db-info)
                 (goldly-run)
               )
    
    )
  )

