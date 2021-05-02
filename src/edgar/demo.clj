(ns edgar.demo
  (:require
   [edgar.edn :refer [edn-read]]
   [edgar.filing :refer [fload table filing parse-filing]]
   [edgar.download :refer [dl-filing]]
   )
  ;(:gen-class)
  )


(defn -main
  []
  #_(aparse (slurp "test.txt"))


  (-> ;"0001567619-20-003889"
      ;"0001225208-21-006658"
      "0001752724-20-077860"
      fload
      filing
   )

  (defn dl-parse [f]
    (let [body (dl-filing f)]
      (when body
        (parse-filing body))))
 
  (dl-parse {:cik 1004655
              :no "0001752724-21-069935"})

(->>  (edn-read "report/index.edn")
    ;count
     ;first
     (take 10)
     ;(skip 100)
     ;(map dl-filing)
     (map dl-filing))

  ;
)
           
     
;(s/select (s/id "formHeader"))
   
    