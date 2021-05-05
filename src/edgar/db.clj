(ns edgar.db
  (:require
   [datahike.api :as d]
   [java-time :as jt]))

; Supported External Backends: LevelDB + PostgreSQL

(def cfg {:store {:backend :file
                  :path "data/datahike"}
          :keep-history? false})

(def schema [;; manager
             {:db/ident :manager/cik
              :db/valueType :db.type/long
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :manager/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :manager/funds
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}

             ;; fund
             {:db/ident :fund/sid
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :fund/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :fund/reports
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}

             ;; report
             {:db/ident :report/no
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :report/cik
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :report/sid
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :report/date-filed
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :report/date-report
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}])

; db.type/instant 	java.util.Date

(when-not (d/database-exists? cfg)
  (d/create-database cfg))

(def conn (d/connect cfg))
(d/transact conn schema)


(defn add-report [{:keys [cik advisor
                          sid fund
                          no date-filed date-report]}]
  (d/transact conn [{:manager/cik cik
                     :manager/name advisor}
                     {:fund/sid sid
                     :fund/name fund}
                    {:report/no no
                     :report/cik cik
                     :report/sid sid
                     :report/date-filed  date-filed ; (jt/local-date 2021 02 01)
                     :report/date-report date-report
                     ;:report/date-saved (java.util.Date.)
                    }
                    {:manager/cik cik
                     :manager/_funds [:fund/sid sid]}
                     
                    {:fund/sid sid
                     :fund/_reports [:report/no no]}
                    ]))

;(d/entity @conn [:fund/sid "S000006198"])
;(d/entity @conn [:report/no "xx458"])

(defn report-existing? [no]
  (let [s (d/q '[:find ?e
                 :in $ ?no
                 :where [?e :report/no ?no]]
               @conn
               no)]
    (not (empty? s))
    ))

#_(defn reports-for-fund [sid]
  (d/pull @conn
          '[*]
          [:report/sid sid]))

(defn reports-for-fund [sid]
 (d/q '[:find ?report ; ?fund
        :in $ ?sid
        :where [?fund  :fund/sid ?sid]
               [?report :fund/reports ?fund]]
      @conn
      sid))

(d/q '[:find ?report ; ?fund
       :in $ ?sid
       :where
       [?fund :fund/sid ?sid] ; lookup db id for report
       ;[?fund :manager/funds ?sid]
       ;[?fund :fund/name ?n]
       [?report :fund/reports ?fund]]
     @conn
     "S000006198")



(defn funds-of-manager [cik]
  (d/q '[:find  ?funds ; ?manager
         :in $ ?cik
         :where
         [?manager  :manager/cik ?cik]
         [?funds :manager/funds  ?manager]]
       @conn
       cik))

(defn manager [cik]
  (d/pull @conn
          '[*]
          [:manager/cik cik]))


(defn fund-list []
  (d/q '[:find ?id ?name
         :where
         [?id :fund/name ?name]]
       @conn))

(defn manager-list []
  (d/q '[:find ?id ?name
         :where
         [?id :manager/name ?name]]
       @conn))

(defn all-reports []
  (d/q '[:find ?id ?no
         :where
         [?id :report/no ?no]]
       @conn))


(defn report-no [no]
  (d/pull @conn
          '[*]
          [:report/no no]))

(defn fund [sid]
  (d/pull @conn
          '[*]
          [:fund/sid sid]))



(d/q '[:find ?e
       :where [?e :report/no "xx123"]]
     @conn)



(comment

  (d/release conn)
  (d/delete-database cfg)

  (add-report {:cik 100334
               :advisor "American Century Mutual Funds, Inc."
               :fund " uuuuu Equity Fund"
               :sid "S000004198"
               :no "xx458"
               :date-filed "2019-10-31"
               :date-report "2019-10-31"})

  (add-report {:cik 100334
               :advisor "American Century Mutual Funds, Inc."
               :fund " iiiii Equity Fund"
               :sid "S000006198"
               :no "xx499"
               :date-filed "2019-10-31"
               :date-report "2019-10-31"})
  
   (add-report {:cik 100334
                :advisor "American Century Mutual Funds, Inc."
                :fund " iiiii Equity Fund"
                :sid "S000006198"
                :no "x7499"
                :date-filed "2018-10-31"
                :date-report "2018-10-31"})

  (report-no "xx458")
  (report-no "xx499")
  (report-no "x7499")
  (report-existing? "xx499")
  (report-existing? "xx458")
  (all-reports)
  (reports-for-fund "S000006198")
  (reports-for-fund "S000004198")
   
  ; fund
  (fund "S000006198")
  (fund "S000004198")
  (fund-list)

  ; manager 
  (manager 100334)
  (funds-of-manager 100334)
  (manager-list)

  
  (d/q '[:find ?o ?on
                  :in $ ?c
                  :where
                  [?c :fund/reports ?o]
                  [?o :report/no ?on]
         ]
       @conn
        19 
       )

  
  (d/pull @conn '[* ]
        [:fund/sid "S000006198"])






(d/q '[:find ?x
       :in $
       :where [?x :manager/funds _]]
     @conn)



 ; 
  )



