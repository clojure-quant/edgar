(ns edgar.demo
  (:require
   [edgar.parse :refer [aparse hparse]]
   [edgar.filing :refer [fload table filing]]
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

  ;
)
           
     
;(s/select (s/id "formHeader"))
   
    