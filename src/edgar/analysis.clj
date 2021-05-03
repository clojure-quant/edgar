(ns edgar.analysis
  (:require
   [clojure.java.io]
   [edgar.edn :refer [edn-read]])
  (:import java.io.File))

(defn nport-file [{:keys [cik no]} {:keys [fid]}]
  (str "data/nport/" cik "_" no "_" fid ".edn"))

(defn load-one [f]
  (let [f (nport-file f f)]
    (edn-read f)))

(defn add-holding [pos indexed h]
  (let [k (:cusip h)]
    (let [i (or (get indexed k)
                (select-keys h [:title :cusip]))
          m (assoc i pos (dissoc h :title :cusip))]
      (assoc indexed k m))))

(defn match [indexed pf pos]
  (let [holdings-pf (:holdings pf)]
    (reduce (partial add-holding pos)
            indexed
            holdings-pf)))

(defn match-table [{:keys [p c n]}]
  (-> {}
      (match p :p)
      (match c :c)
      (match n :n)))


(defn has-p-c-n [itm]
  (and (contains? itm :p)
       (contains? itm :c)
       (contains? itm :n)))

(defn chg [itm]
  (let [p-qty (get-in itm [:p :qty])
        c-qty (get-in itm [:c :qty])
        n-qty (get-in itm [:n :qty])]
    {:cusip (:cusip itm)
     :title (:title itm)
     :diff-p (- c-qty p-qty)
     :diff-n (- n-qty c-qty)}))


(defn stats [itms]
  (let [c-tot (count itms)
        n-pos (count (filter #(> (:diff-n %) 0) itms))
        n-neg (- c-tot n-pos)]
    {:tot c-tot
     :n-neg n-neg
     :n-pos n-pos}))

(defn stats-all [itms]
  {:all (stats itms)
   :p-pos (stats (filter #(> (:diff-p %) 0) itms))
   :p-neg (stats (filter #(< (:diff-p %) 0) itms))})

; :cusip "N/A"

(defn info [pf]
  {:nav (:nav pf)
   :ct (count (:holdings pf))})



(defn qty-report [reps]
  (let [stats (->>
               reps
               (match-table)
               (vals)
               (filter has-p-c-n)
               (map chg)
               (stats-all))]
    {:advisor (get-in reps [:p :advisor])
     :fund (get-in reps [:p :fund])
     :p-date (get-in reps [:p :date-filed])
     :c-date (get-in reps [:c :date-filed])
     :n-date (get-in reps [:n :date-filed])
     :stats stats}))

; report lister

(def re-nport #"(.*)_(.*)_(.*)\.edn")
(defn nport-info [f]
  (let [s (.getName f)
        m (re-find re-nport s)
        [_ cik no fid] m]
    {:cik cik
     :no no
     :fid fid}))

(defn reports-for [fid]
  (let [dir (clojure.java.io/file "data/nport")
        files (.listFiles dir)
        f (first files)]
    (->>
     (map nport-info files)
     (sort-by :fid)
     (filter #(= fid (:fid %)))
    ;(.getName f)
     )))


(defn calc-3 [replist]
  (if ( > (count replist) 2)
     (let [ [p c n] (take 3 replist)]
       (qty-report {:p (:r p) 
                    :c (:r c) 
                    :n (:r n)})
       )
    []
  ))

(defn calc-rec [reps]
  (if (> (count reps) 2)
    (conj (calc-rec (rest reps))
          (calc-3 reps))
    []
  ))

(defn calc-reports-for [fid]
  (let [reps (reports-for fid)
        reps (map #(assoc % :r (load-one %)) reps)
        reps (sort-by #(get-in % [:r :date-filed]) reps)
        ]
     (calc-rec reps)
     ;(map #(dissoc % :r) reps )
    )
  )

(comment

  (defn qty-report-load [p c n]
    (let [reps {:p (load-one p)
                :c (load-one c)
                :n (load-one n)}]
      (qty-report reps)))

  (qty-report-load
   {:no "0001145549-20-016610" :cik "100334" :fid "S000006192"}
   {:no "0001145549-20-036689" :cik "100334" :fid "S000006192"}
   {:no "0001145549-20-076715" :cik "100334" :fid "S000006192"})


  (calc-reports-for "S000006192")


  ;
  )



