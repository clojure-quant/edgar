(ns edgar.analysis
  (:require
   [edgar.edn :refer [edn-read]]
   ))

 (defn nport-file [{:keys [cik no]} {:keys [fid]}]
   (str "data/nport/" cik "_" no "_" fid ".edn"))

 (defn load-one [f]
   (let [f (nport-file f f)]
     (edn-read f)))

 (defn load-x []
   (let [a (load-one {:cik "100334"
                      :no "0001145549-20-036689"
                      :fid "S000006192"})
         b (load-one {:cik "100334"
                      :no "0001145549-20-076715"
                      :fid "S000006192"})
         c (load-one {:cik "100334"
                      :no "0001145549-20-016610"
                      :fid "S000006192"})
         ]
     {:a a
      :b b
      :c c}
     )  
   )

; :cusip "N/A"

 (defn info [pf]
   {:nav (:nav pf)
    :ct (count (:holdings pf))
    }
   )

 (->> (load-x)
      vals
     (map info  )
  
  )



