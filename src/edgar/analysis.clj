(ns edgar.analysis
  (:require
   [edgar.edn :refer [edn-read]]))

(defn nport-file [{:keys [cik no]} {:keys [fid]}]
  (str "data/nport/" cik "_" no "_" fid ".edn"))

(defn load-one [f]
  (let [f (nport-file f f)]
    (edn-read f)))

(defn load-x []
  (let [c (load-one {:cik "100334"
                     :no "0001145549-20-036689"
                     :fid "S000006192"})
        n (load-one {:cik "100334"
                     :no "0001145549-20-076715"
                     :fid "S000006192"})
        p (load-one {:cik "100334"
                     :no "0001145549-20-016610"
                     :fid "S000006192"})]
    {:p p
     :c c
     :n n}))

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
        n-qty (get-in itm [:n :qty])
        ]
    {:cusip (:cusip itm)
     :title (:title itm)
     :diff-p (- c-qty p-qty)
     :diff-n (- n-qty c-qty)
     }
    )
  )


(defn stats [itms]
  (let [c-tot (count itms)
        n-pos (count (filter #(> (:diff-n %) 0) itms))
        n-neg (- c-tot n-pos)
        ]
   {:tot c-tot 
    :n-neg n-neg
    :n-pos n-pos}
  ))

(defn stats-all [itms]
  {:all (stats itms)
   :p-pos (stats (filter #(> (:diff-p %) 0) itms))
   :p-neg (stats (filter #(< (:diff-p %) 0) itms))
   }
  )

; :cusip "N/A"

(defn info [pf]
  {:nav (:nav pf)
   :ct (count (:holdings pf))})



(defn qty-report []
  (let [reps (load-x)
        stats (->>
               reps
               (match-table)
               (vals)
               (filter has-p-c-n)
               (map chg)
               (stats-all))
        ]
      {:advisor (get-in reps [:p :advisor])
       :fund (get-in reps [:p :fund])
       :p-date (get-in reps [:p :date-filed])
       :c-date (get-in reps [:c :date-filed])
       :n-date (get-in reps [:n :date-filed])
       :stats stats}
    ))


(qty-report)



