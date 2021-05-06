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
            [:button {:on-click (fn [& _]
                                 (println "getting funds")
                                 (?getfunds)) }
             "load all funds"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [[id name]]
                          [:a {:href (str "/system/fund/" id)}
                          [:span name]])
                        (:funds @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:getfunds [(fn [] (db/fund-list)) [:funds]]}})))


(defn reps-by-id [id]
  (let [id-int (Integer/parseInt id)
        reps  (db/reports-for-fund-by-dbid id-int)
        ]
   (println "reps-by-id" id reps)
    reps
  ))

(defn fund-by-id [id]
  (let [id-int (Integer/parseInt id)
        f  (db/fund-bydbid id-int)]
    (println "fund-by-id" id f)
    f))



(defn start-fund []
  (system-start!
   (goldly/system
    {:id :fund
     :hidden? true
     :state {:first true
             :reports []}
     :html [:<>
            [:h1 "Mutual Fund db-id: " ext]
            (when (:first @state)
              (swap! state assoc :first false)
              (?get-fund ext)
              (?get-reports ext))
            
            ; fund data
            [:p (get-in @state [:fund :fund/name])]
            [:p (get-in @state [:fund :fund/sid])]

            [:h1.text-xl.text-red-700 "reports"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [f]
                          [:p 
                            [:span (:report/date-report f)]
                            [:span (:report/date-filed f)]
                            [:span (:report/no f)]
                            ;[:span (pr-str f)]
                          ]
                          )
                        (:reports @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-reports [reps-by-id [:reports]]
           :get-fund [fund-by-id [:fund]]
           }})))

(defn goldly-run []
  (println "adding systems..")
  (start-fund-list)
  (start-fund)
  (println "starting gldly server..")
  (goldly-server.app/goldly-server-run! "jetty" {})

  ;(webly-run! "jetty")
  ;(webly-run! "watch")
  )





