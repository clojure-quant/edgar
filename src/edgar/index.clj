(ns edgar.index
  (:require
   [clojure.set :refer [rename-keys]]
   [tech.v3.dataset :as ds]))

(def re-filing #"edgar\/data\/(.*)\/(.*)\-index.html")
(defn filing-no [s]
  (-> (re-find re-filing s)
      (nth 2)))

(defn load-index [filename]
  (let [d (ds/->dataset filename
                        {:header-row? false
                         :file-type :csv
                         :separator \|})]
    (-> d
        (assoc "date" (d "column-3"))
        (assoc "form" (d "column-2"))
        (assoc "cik" (d "column-0"))
        (assoc "no" (map filing-no (d "column-5")))
        (assoc "Name" (d "column-1"))
        (dissoc "column-0")
        (dissoc "column-1")
        (dissoc "column-2")
        (dissoc "column-3")
        (dissoc "column-4")
        (dissoc "column-5"))))

(defn filter-cik [cik d]
  (if cik
    (ds/filter-column d "cik" #(= cik %))
    d))

(defn filings-index [file-name form cik]
  (let [d (load-index file-name)]
    (println "index " file-name)
    (->>
     (ds/filter-column d "form" #(= form %))
     (filter-cik cik)
     (ds/mapseq-reader)
    ;count
    ; (doall)
     (take 100000)
     (map #(rename-keys % {"cik" :cik
                           "no" :no
                           "date" :date
                           "Name" :name
                           ;"form" :f
                           })))))

(comment

  ; NPORT-P
  ; NPORT-P/A
  ; N-Q

  (filings-index "data/index/2021-QTR2.tsv"
                 "NPORT-P"
                 1004655)

  (rename-keys {"cik" 1
                "form" 2} {"cik" :cik
                           "form" :form})


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
