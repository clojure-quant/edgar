(ns edgar.xml
  (:require
   [clojure.string :as str]
   [clojure.xml :as xml]
   [clojure.java.io :as io]))


(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))


(defn has-text [{:keys [tag attrs content]}]
  (and content
       (= (count content) 1)
       (string? (first content))))

(defn get-text [{:keys [tag attrs content]}]
  ;(println "get text" tag)
  (let [t (first content)]
    t))

(defn node-data [{:keys [tag attrs content]
                  :or {attrs {}}
                  :as n}]
  (if content
    (assoc {} tag
           (if (has-text n) 
             (if (empty? attrs)
               (get-text n)
             (merge attrs {:text (get-text n)}))
             attrs))
    (assoc {} tag attrs)))

;tree

(defn recurse? [{:keys [tag attrs content] :as n}]
  (and content
       (not (has-text n))))

(defn is-vec? [vecs tag]
  (contains? vecs tag))

(defn node-tree [vecs {:keys [tag content] :as n}]
  ;(println "p: " n)
  (let [my (node-data n)]
    (if (recurse? n)
      (if (is-vec? vecs tag)
        (assoc {} tag (into []
                            (map (comp first vals (partial node-tree vecs)) content)))
        (let [d (apply merge (map (partial node-tree vecs) content))
              d-t (assoc {} tag d)
              ]
          ;(println "d:" d "my: " my)
        (merge my d-t
             ;(into {} d)
               
               )))
      
      my)))

(defn p-str [vecs str]
  (let [p (partial node-tree vecs)]
  (-> str string->stream xml/parse p)))

(defn p-file [vecs f]
   (let [p (partial node-tree vecs)]
  (-> f io/file xml/parse p)))

(comment

  (defn p [str]  
    (p-str #{:financials} str))

  (p " <name>EXLSERVICE HOLDINGS INC</name>")


  (p "  <invstOrSec g=\"8\">
              </invstOrSec>
             ")
  (p "<financials>
            <fin b=\"c\"> </fin>
            <fin b=\"b\"> </fin>
            <fin b=\"a\"> </fin>
           </financials>")
  
  (p "<identifiers>
           <isin value= \"US3020811044 \"/>
           <isin2 value= \"US3020811044 \"/>
         </identifiers>")

; <invstOrSecs>
(p "
      <invstOrSec g=\"8\">
        <name>EXLSERVICE HOLDINGS INC</name>
        <lei>81E3DTNF6OSH489ZOV15</lei>
        <title>EXLSERVICE HOLDINGS INC</title>
        <cusip>302081104</cusip>
        <identifiers>
          <isin value=\"US3020811044\"/>
        </identifiers>
 </invstOrSec>
")
; </invstOrSecs>
  
  
 ; 
  )