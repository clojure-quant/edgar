(ns edgar.job.goldly
  (:require
   [goldly-server.app]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly :refer [def-ui]]
   
   [edgar.db :as db]
   [edgar.job.info :refer [funds-min-reps]]
   [edgar.analysis.report :refer [load-report]]
   [edgar.analysis.instrument :refer [relevant]]
   [edgar.analysis.behavior :refer [calc-behavior]]
   ))


(defn funds-min [min-no]
  (let [funds (db/fund-list-count)
        funds-min  (funds-min-reps funds min-no)]
    (println "funds with min " min-no " reports" funds-min)
    funds-min))


(defn reps-by-id [id]
  (let [id-int (Integer/parseInt id)
        reps  (db/reports-for-fund-by-dbid id-int)]
    (println "reps-by-id" id reps)
    ;reps
    (sort-by :report/date-filed reps)
    ))

(defn fund-by-id [id]
  (println "getting fund: " id)
  (let [id-int (Integer/parseInt id)
        f  (db/fund-bydbid id-int)]
    (println "returning fund: " f)
    f))

(defn fund-behavior [id]
  (println "getting behavior: " id)
  (let [id-int (Integer/parseInt id)
        b  (calc-behavior id-int)]
    (println "returning behavior: " b)
    b))


(defn report-by-id [id all?]
  (println "getting report " id "all:" all?)
  (let [id-int (Integer/parseInt id)
        r  (load-report id-int)]
    (println "report id:" id-int "period: " (:date-filed r))
    (if all?
      r
      (relevant r))
    ))

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
              (?get-funds 4))
            [:button.bg-yellow-800.m-2
             {:on-click (fn [& _]
                                  (println "getting funds")
                                  (?get-funds 5))}
             "load funds with min 5 reports"]
            [:button.bg-yellow-800.m-2
             {:on-click (fn [& _]
                          (println "getting funds")
                          (?get-funds 6))}
             "load funds with min 6 reports"]
            [:div.flex.flex-row.content-between
             (into [:div.flex.flex-col.justify-start]
                   (map (fn [{:keys [id name reports]}]
                          [:a {:href (str "/system/fund/" id)}
                           [:span-p-1.w16 name]
                           [:span.p-1 reports]])
                        (:funds @state)))]]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-funds [funds-min [:funds]]}})))


(defn start-fund []
  (system-start!
   (goldly/system
    {:id :fund
     :hidden true
     :state {:first true
             :reports []}
     :html [:<>
            [:h1 "Mutual Fund db-id: " ext]
            (when (:first @state)
              (swap! state assoc :first false)
              (?get-fund ext)
              (?get-behavior ext)
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
                        (:reports @state)))]
            
            [:div
             [:p.text-xl.text-blue-700 "behavior"]
             [:p "keep-buying: " (get-in @state [:behavior :keep-buy])]
             [:p "keep-selling: " (get-in @state [:behavior :keep-sell])]
 
              (into [:table]
                   (map (fn [b]
                          [:tr
                             [:a {:on-click (fn [& _]  
                                              (swap! state assoc :matrix (get-in b [:matrix]))
                                              )}
                              [:td.p-2 (:p-date b)]
                             [:td.p-2 (:c-date b)]
                             [:td.p-2 (:n-date b)]]
                           ; all
                           [:td.w-6.border.border-round (get-in b [:stats :p-all :n-all])]
                           ; past sam
                           [:td.bg-yellow-300.border.border-round.w-6 (get-in b [:stats :p-sam :n-all])]
                           [:td.bg-yellow-300..w-6 (get-in b [:stats :p-sam :n-sam])]
                           [:td.w-6.text-red-900 (get-in b [:stats :p-sam :n-neg])]
                           [:td.w-6.text-blue-900 (get-in b [:stats :p-sam :n-pos])]
                           ; past up
                           [:td.w-6.bg-blue-300.border.border-round (get-in b [:stats :p-pos :n-all])]
                           [:td.w-6 (get-in b [:stats :p-pos :n-sam])]
                           [:td.w-6.text-red-900 (get-in b [:stats :p-pos :n-neg])]
                           [:td.w-6.bg-blue-300.text-blue-900 (get-in b [:stats :p-pos :n-pos])]
                            ;past down
                           [:td.w-6.bg-red-300.border.border-round (get-in b [:stats :p-neg :n-all])]
                           [:td.w-6 (get-in b [:stats :p-neg :n-sam])]
                           [:td.w-6.bg-red-300.text-red-900 (get-in b [:stats :p-neg :n-neg])]
                           [:td.w-6.text-blue-900 (get-in b [:stats :p-neg :n-pos])]                           
                           ]
                         )
                           (get-in @state [:behavior :reps])))]
             
             (let [matrix (or (:matrix @state) [])]
               (when (> (count matrix) 0)
                [:p.text-xl.text-blue-700 "behavior matrix"]
                (into [:table] 
                  (map (fn [m]
                      [:tr
                        [:td (:title m)] 
                        [:td (get-in m [:p :qty])]
                        [:td (get-in m [:c :qty])]
                        [:td (get-in m [:n :qty])]
                        [:td (get-in m [:p :valUSD])]
                        [:td (get-in m [:c :valUSD])]
                        [:td (get-in m [:n :valUSD])]
                        [:td (get-in m [:p :pctVal])]
                        [:td (get-in m [:c :pctVal])]
                        [:td (get-in m [:n :pctVal])]]
                         ) matrix))))

             ]
     :fns {;:incr (fn [_ s] (inc s))
           }}
    {:fns {:get-reports [reps-by-id [:reports]]
           :get-fund [fund-by-id [:fund]]
           :get-behavior [fund-behavior [:behavior]]
           }})))

(defn start-report []
  (system-start!
   (goldly/system
    {:id :report
     :hidden true
     :state {:first true
             :report {}}
     :html [:<>
            
            (when (:first @state)
              (swap! state assoc :first false)
              (?get-report ext false))

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
            [:p "fiscal " (get-in @state [:report :date-fiscal])]
            [:p "nav " (get-in @state [:report :nav])]
            [:button.p-2.bg-yellow-400.border.border-round
               {:on-click (fn [& _]
                            (?get-report ext true)
                            )} "display all categories"]
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


(def goldly-config 
 {:goldly {:systems-ns []}})

(defn goldly-run []
  (println "adding systems..")
  (start-fund-list)
  (start-fund)
  (start-report)
  (println "starting goldly server..")
  (goldly-server.app/goldly-server-run! "jetty" goldly-config))





