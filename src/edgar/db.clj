(ns edgar.db
  (:require
   [datahike.api :as d]
   [java-time :as jt]))

; Supported External Backends: LevelDB + PostgreSQL

(def cfg {:store {:backend :file
                  :path "data/datahike"}
          :keep-history? true})

;(d/delete-database cfg)

(when-not (d/database-exists? cfg)
  (d/create-database cfg))

(def conn (d/connect cfg))
;(d/release conn)

(def schema [;; manager
             {:db/ident :manager/cik
              :db/valueType :db.type/long
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :manager/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}

             ;; fund
             {:db/ident :fund/sid
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one
              }
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
             ])

(d/transact conn schema)


(d/transact conn [{:fund/sid  "S001"
                   :fund/name "Templeton Growth"}
                  {:fund/sid  "S002"
                   :fund/name "Templeton Value"}])


(d/q '[:find ?e ?on
       :where
       [?e :fund/name ?on]]
     @conn)


(d/transact conn [{:fund/_reports [:fund/sid "S001"]
                   :report/no "2020-001"
                   ;:report/filed (jt/local-date 2021 02 01)
                   }
                  {:fund/_reports [:fund/sid "S001"]
                   :report/no "2021-001"
                   ;:report/filed (jt/local-date 2021 02 01)
                   }
                   {:fund/_reports [:fund/sid "S002"]
                    :report/no "2020-002"
                   ;:report/filed (jt/local-date 2021 02 01)
                    }
                  {:fund/_reports [:fund/sid "S002"]
                   :report/no "2021-002"
                   ;:report/filed (jt/local-date 2021 02 01)
                   }
                 
                  ])

(d/q '[:find ?e ?on
       :where
       [?e :report/no ?on]]
     @conn)

(defn reports-for-fund [sid]
  (d/pull
   @conn
   '[*]
   [:fund/sid sid])
  )

(reports-for-fund "S002")

(reports-for-fund "S001")




(d/pull
 @conn
 '[*]
 [:fund/sid "S001"])



(d/pull
 @conn
 '[* {:fund/reports
      [* {:offer/task-groups
          [:task-group/name {:task-group/tasks
                             [* {:task/price-unit [:db/ident]
                                 :task/effort-unit [:db/ident]}]}]}]}]
 [:customer/name "Little Shop"])



