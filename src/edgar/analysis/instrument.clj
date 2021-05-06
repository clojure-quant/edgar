(ns edgar.analysis.instrument)

(defn r? [{:keys [assetCat invCountry issuerCat]}]
  (and (= "EC" assetCat)
       (= "US" invCountry)
       (= "CORP" issuerCat)))

(defn relevant [pf]
  (let [holdings-pf (->> (:holdings pf)
                         (filter r?))]
    (assoc pf :holdings holdings-pf)))



