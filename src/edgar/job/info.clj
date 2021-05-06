(ns edgar.job.info
  (:require
   [edgar.db :as db]))


(defn funds-3-reps [funds]
  (->> (map (fn [[id name c]]
              {:id id
               :name name
               :reports c}
              ) funds)
       (filter #(> (:reports %) 2))
       )
  )


(defn print-db-info []
  (println "loading db stats..")
  (let [funds (db/fund-list-count)
        mgrs (db/manager-list)
        rps (db/all-reports)]
    (println "info")
    (println "fund list: " funds)
    (println "min 3 reps: " (funds-3-reps funds))
(println "funds: " (count funds)
         "mgrs: " (count mgrs)
         "reports: " (count rps))
    ))

(comment 
  
  (print-db-info)
  
  ;
  )

