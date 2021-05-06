(ns edgar.job.goldly
  (:require
   [goldly-server.app]
   [edgar.db :as db]
   [edgar.job.info :refer [funds-3-reps]]
   [edgar.analysis.report :refer [load-report]]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]))


(defn funds3 []
  (let [funds (db/fund-list-count)
        funds3  (funds-3-reps funds)]
    (println "funds with 3 reports" funds3)
    funds3))


(defn reps-by-id [id]
  (let [id-int (Integer/parseInt id)
        reps  (db/reports-for-fund-by-dbid id-int)]
    (println "reps-by-id" id reps)
    reps))

(defn fund-by-id [id]
  (let [id-int (Integer/parseInt id)
        f  (db/fund-bydbid id-int)]
    (println "fund-by-id" id f)
    f))

(defn report-by-id [id]
  (let [id-int (Integer/parseInt id)
        r  (load-report id-int)]
    (println "report id:" id-int "period: " (:date-filed r))
    r))

(defn start-fund-list []
  (system-start!
   (goldly/system
    {:id :fund-list
     :state {:first true
             :funds []}
     :html [:<>
            [:h1 "Mutual Fund (EDGAR database from SEC )"]
            [:p "showing funds with at least 3 portfolio reports"]
            (when (:first @state)
              (swap! state assoc :first false)
              (?get-funds))
            [:button {:on-click (fn [& _]
                                  (println "getting funds")
                                  (?get-funds))}
             "load all funds"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [{:keys [id name reports]}]
                          [:a {:href (str "/system/fund/" id)}
                           [:span-p-1.w16 name]
                           [:span.p-1 reports]])
                        (:funds @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-funds [funds3 [:funds]]}})))


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
            [:p "Name:" (get-in @state [:fund :fund/name])]
            [:p "SID:" (get-in @state [:fund :fund/sid])]

            [:h1.text-xl.text-red-700 "reports"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [f]
                          [:a {:href (str "/system/report/" (:db/id f))}
                           [:p
                            [:span.p-1 (:report/date-report f)]
                            [:span.p-1 (:report/date-filed f)]
                            [:span.p-1 (:report/no f)]
                            ;[:span (pr-str f)]
                            ]])
                        (:reports @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-reports [reps-by-id [:reports]]
           :get-fund [fund-by-id [:fund]]}})))

(defn start-report []
  (system-start!
   (goldly/system
    {:id :report
     :hidden? true
     :state {:first true
             :report {}}
     :html [:<>
            
            (when (:first @state)
              (swap! state assoc :first false)
              (?get-report ext))

            [:p "Report db-id: " ext 
                " no: " (get-in @state [:report :no])
                " cik: " (get-in @state [:report :cik])
                " sid: " (get-in @state [:report :sid])
                [:a {:href (str "https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK=" (get-in @state [:report :cik]))}
                  " @sec "]
             ]
            
            ; fund data
            [:p "advisor " (get-in @state [:report :advisor])]
            [:p "fund " (get-in @state [:report :fund])]
            [:p "filed " (get-in @state [:report :date-filed])]
            [:p "report " (get-in @state [:report :date-report])]
            [:p "nav " (get-in @state [:report :nav])]

            [:h1.text-xl.text-red-700 "holdings"]
            [:div.flex.flex-row.content-between
             (into [:table]
                   (map (fn [f]
                          [:tr
                           [:td.p-1 (:cusip f)]
                           [:td.p-1 (:title f)]
                           [:td.p-1 (:assetCat f)]
                           [:td.p-1 (:issuerCat f)]
                           [:td.p-1 (:invCountry f)]
                           [:td.p-1 (:valUSD f)]
                           [:td.p-1 (:qty f)]
                           [:td.p-1 (:pctVal f)]
                            ;[:span (pr-str f)]
                           ])
                        (get-in @state [:report :holdings])))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-report [report-by-id [:report]]}})))


(defn goldly-run []
  (println "adding systems..")
  (start-fund-list)
  (start-fund)
  (start-report)
  (println "starting goldly server..")
  (goldly-server.app/goldly-server-run! "jetty" {}))





