(ns edgar.index
  (:require
   [edgar.helper]
   [clojure.set :refer [rename-keys]]
   [tech.v3.dataset :as ds]))

(def re-filing #"edgar\/data\/(.*)\/(.*)\-index.html")
(defn filing-no [s]
  (-> (re-find re-filing s)
      (nth 2)))

(defn load-index [filename]
  (let [a (ds/->dataset filename
                        {:header-row? false
                         :file-type :csv
                         :separator \|})]
  (-> a
      (assoc "date" (a "column-3"))
      (assoc "form" (a "column-2"))
      (assoc "cik" (a "column-0"))
      (assoc "no" (map filing-no (a "column-5")))
      (assoc "Name" (a "column-1"))
      (dissoc "column-0")
      (dissoc "column-1")
      (dissoc "column-2")
      (dissoc "column-3")
      (dissoc "column-4")
      (dissoc "column-5"))
  ))


(defn p-filings [file-name]
  (let [a (load-index file-name)]
  (->> 
     (ds/filter-column a "form" #(= "NPORT-P" %))
     (ds/mapseq-reader)
    ;count
    ; (doall)
      (take 100)
     (map #(rename-keys % {"cik" :cik
                           "no" :no
                           "date" :date
                           "Name" :name
                           ;"form" :f
                           }))
  
     (edgar.helper/save "report/index.edn")
     )))

(comment
  
(p-filings "data/index/2021-QTR2.tsv")
  

(println (ds/head a))
(ds/brief a)
(ds/row-count a)
(take 2 a)
(ds/descriptive-stats a)
(a "column-1")
(a 1)
(ds/dataset->data a)
(a "form")

(println (sort (keys (ds/group-by-column a "form"))))


  (filing-no "edgar/data/1000152/0001000152-21-000004-index.html")
  ;
  )
