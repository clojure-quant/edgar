(ns edgar.db-build
  (:require
   [clojure.java.io :as io]
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.index :refer [filings-index]])
  (:import java.io.File))


(defn cik-name [f]
  [ (:cik f) (:name f)]
  )

(defn build-db []
  (let [indici (for [year [2019 2020 2021]
                     q [1 2 3 4]]
                 (let [filename (str "data/index/" year "-QTR" q ".tsv")]
                   (if (.exists (io/file filename))
                     (filings-index filename "NPORT-P" nil)
                     [])))
        all (apply concat indici)
        ;all (map #(dissoc % "form") all)
        _ (println "grouping by cik and name")
        manager (group-by cik-name all)
        manager (->> (keys manager)
                     (map (fn [[cik name]]
                            {:name name
                             :cik cik}))
                     (sort-by :name))
        _ (println "mapping..")
        #_db #_(map (fn [[k v]]
              [k  (map #(dissoc % "form" :name :cik :date) v)] 
               ) manager)

        ]
    (edn-save "data/list-raw.edn" all)
    (edn-save "data/manager.edn" manager)))


; report lister

(def re-nport #"(.*)_(.*)_(.*)\.edn")
(defn nport-info [f]
  (let [s (.getName f)
        m (re-find re-nport s)
        [_ cik no fid] m]
    {:cik cik
     :no no
     :fid fid}))

(defn reports-all []
  (let [dir (clojure.java.io/file "data/nport")
        files (.listFiles dir)]
    (->>
     (map nport-info files)
     ;(sort-by :fid)
     )))

(defn reports-for [fid]
  (->> (reports-all)
       (filter #(= fid (:fid %)))
       )
  )

(defn cik-fid [f]
  [(:cik f) (:fid f)])

(defn reps-summary []
  (let [rpts (reports-all)
        g (group-by cik-fid rpts)
        rstats (map (fn [[[cik fid] v]]
      {:cik cik
       :fid fid
       :rpts (count v) }     
           )  g)]
     (edn-save "data/repsum.edn" rstats)
    
    )
  )


(comment

  (build-db)
(reps-summary)
  
  (map (fn [[k v]] 
        (println "**" v) v) {:a 1 :b 2 :c 3})
  

 ; 
  )