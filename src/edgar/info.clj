(ns edgar.info
  (:require
   [clojure.core.async :refer [thread go <!]]
    ;[goldly-server.app]
   [edgar.db :refer [fund-list manager-list all-reports]])
  (:gen-class))

(defn -main []
  (let [funds (fund-list)
        mgrs (manager-list)
        rps (all-reports)
        ]
    (println "info")
    (println "fund list: " funds)
    (println "funds: " (count funds) 
             "mgrs: " (count mgrs)
             "reports: " (count rps)
             )))
  
