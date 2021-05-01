(ns edgar.nport
   (:require [clojure.xml :as xml]
             [clojure.java.io]
             [clojure.data.xml :refer [parse-str]]
            ; [tupelo.core tupelo.forest]
             )
  )


(defn parse [s]
  (let [f (clojure.java.io/input-stream "myfile.txt")] 
  (clojure.xml/parse f)
  ;(parse-str s)
  ))


(def d (parse  "demodata/N-PORT/primary_doc.xml"))

d
; (slurp

; zippers
; https://blog.korny.info/2014/03/08/xml-for-fun-and-profit.html

(defn child-of [node tag]
  (filter #(= tag (:tag %)) (:content node)
  ))

(let [h (child-of d :headerData)
      f (first (child-of d :formData))

      g (child-of f :genInfo)
      ]
  f
  
  )