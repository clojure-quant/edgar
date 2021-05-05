(ns edgar.job.goldly
  (:require
   [goldly-server.app]
   [edgar.db :as db]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]))

(defn start-fund-list []
  (system-start!
   (goldly/system
    {:id :fund-list
     :state {:funds []}
     :html [:<>
            [:h1 "Mutual Fund (EDGAR database from SEC )"]
            [:button {:on-click ?getfunds} "load all funds"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [f]
                          [:span (pr-str f)])
                        (:funds @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:getfunds [(fn [] (db/fund-list)) [:funds]]}})))

(defn start-fund []
  (system-start!
   (goldly/system
    {:id :fund
     :hidden? true
     :state {:reports []}
     :html [:<>
            [:h1 "Mutual Fund: " ext]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [f]
                          [:span (pr-str f)])
                        (:funds @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-reports [(fn [] (db/reports-for-fund)) [:reports]]}})))

(defn goldly-run []
  (println "adding systems..")
  (start-fund-list)
  (start-fund)
  (println "starting gldly server..")
  (goldly-server.app/goldly-server-run! "jetty" {})

  ;(webly-run! "jetty")
  ;(webly-run! "watch")
  )





