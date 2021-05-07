(ns edgar.analysis.class)


(defn prct-chg-class [min-pct-diff itm k-a k-b] 
  (let [a-pct (get-in itm [k-a :pctVal])
        b-pct (get-in itm [k-b :pctVal])
        diff-pct (- b-pct a-pct)]
    (cond 
      (> diff-pct min-pct-diff) :buying
      (< min-pct-diff (- diff-pct) ) :selling
      :else :holding
      )
  ))

(defn chg [itm]
    {:cusip (:cusip itm)
     :title (:title itm)
     :past   (prct-chg-class 0.5 itm :p :c)
     :future (prct-chg-class 0.5 itm :c :n)})

(defn future-stats [items]
  (let [unchg (filter #(= :holding (:future %)) items)
        pos   (filter #(= :buying  (:future %)) items)
        neg   (filter #(= :selling (:future %)) items)]
    {:n-all (count items)
     :n-sam (count unchg)
     :n-neg (count neg)
     :n-pos (count pos)}))

(defn stats-all [items]
   (let [unchg (filter #(= :holding (:past %)) items)
         pos   (filter #(= :buying  (:past %)) items)
         neg   (filter #(= :selling (:past %)) items)]
  {:p-all (future-stats items)
   :p-sam (future-stats unchg)
   :p-pos (future-stats pos)
   :p-neg (future-stats neg)}))


(defn make-stats [table]
  (->> table
       (map chg)
       (stats-all)))
  

(comment  

  (prct-chg-class 0.5 {:p {:pctVal 2.6}
                       :c {:pctVal 3.3}
                       :n {:pctVal 2.9}} :c :n)

  (make-stats [{:cusip 1 :title 2 
                :p {:pctVal 2.6}
                :c {:pctVal 3.8}
                :n {:pctVal 2.9}}
               {:cusip 1 :title 2
                :p {:pctVal 2.6}
                :c {:pctVal 1.3}
                :n {:pctVal 0.9}}
               ])
  
; 
) 