(ns edgar.analysis.behavior
  (:require
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.analysis.instrument :refer [relevant]]
   [edgar.analysis.report :refer [load-reports]]))

(defn add-holding [pos indexed h]
  (let [k (:cusip h)]
    (let [i (or (get indexed k)
                (select-keys h [:title :cusip :assetCat :issuerCat :invCountry]))
          m (assoc i pos (dissoc h :title :cusip :assetCat :issuerCat :invCountry))]
      (assoc indexed k m))))

(defn match [indexed pf pos]
  (let [pf (relevant pf)
        holdings-pf (:holdings pf)
        ;holdings-pf (filter #(= "EC" (:assetCat %)) holdings-pf)
        ]
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


(defn analysis-file [{:keys [fund c-date]}]
  (str "data/analysis/" fund "_" c-date ".edn"))

(defn qty-report [reps]
  (let [table (->>
               reps
               (match-table)
               (vals)
               (filter has-p-c-n))
        stats (->> table
                   (map chg)
                   (stats-all))
        report {:advisor (get-in reps [:p :advisor])
                :fund (get-in reps [:p :fund])
                :p-date (get-in reps [:p :date-report])
                :c-date (get-in reps [:c :date-report])
                :n-date (get-in reps [:n :date-report])
                :stats stats
                :matrix table
                }]
    (edn-save (analysis-file report) (assoc report :matrix table))
    report))




(defn calc-3 [replist]
  (if (> (count replist) 2)
    (let [[p c n] (take 3 replist)]
      (qty-report {:p p
                   :c c
                   :n n}))
    []))

(defn calc-rec [reps]
  (if (> (count reps) 2)
    (conj (calc-rec (rest reps))
          (calc-3 reps))
    []))


(defn add [agg line path]
  (let [a (or (get-in agg path) 0)
        l (or (get-in line path) 0)]
    (+ a l)))

(defn add-one [agg line]
  {:all   {:n-pos (add agg line [:all :n-pos])
           :n-neg (add agg line [:all :n-neg])
           :tot   (add agg line [:all :tot])}
   :p-pos {:n-pos (add agg line [:p-pos :n-pos])
           :n-neg (add agg line [:p-pos :n-neg])
           :tot   (add agg line [:p-pos :tot])}
   :p-neg {:n-pos (add agg line [:p-neg :n-pos])
           :n-neg (add agg line [:p-neg :n-neg])
           :tot   (add agg line [:p-neg :tot])}})

 (defn add-summary [lines]
   (let [])
   {:p-date "agg"
    :c-date "agg"
    :n-date "agg"
    :stats (reduce add-one (map :stats lines))
   })

(defn p-safe [line path-a path-b]
  (let [a (get-in line path-a)
        b (get-in line path-b)
        ]
   (when (> b 1)
      (int (/ (* 100 a) b)))
  ))

(defn p [line]
  {:keep-buy (p-safe line [:stats :p-pos :n-pos]
                          [:stats :p-pos :tot])
   :keep-sell (p-safe line [:stats :p-neg :n-neg]
                      [:stats :p-neg :tot])
   })


(defn calc-behavior [fund-db-id]
  (let [reps (load-reports fund-db-id)
        reps (sort-by :date-report reps)
        lines (calc-rec reps)
        sum-line (add-summary lines)
        ]
    (assoc (p sum-line)
           :reps (conj lines sum-line))
    ))

(comment

  (calc-behavior 496)




  ;
  )



