(ns edgar.sec.xml2
  (:require
   [clojure.string :as str]
   [clojure.zip :as zip]
   [clojure.data.xml :as xml]
   [clojure.data.zip.xml :as zx]
   [clojure.data.zip :as dzip]
   [clojure.java.io :as io]
   [into-edn :refer [into-edn]] ; https://github.com/thebusby/into-edn
   ))


; https://github.com/nonsequitur/smex


(defn =tag? [t]
  (fn [loc]
    (when-let [node (zip/node loc)]
      (when-let [node-tag (:tag node)]
      (println "node has a tag!")
      (= (name node-tag) t)))))

(defn tag=
  "Returns a query predicate that matches a node when its is a tag
  named tagname."
  [tagname]
  (fn [loc]
    (or (= tagname (name (:tag (zip/node loc))))
        (filter #(and (zip/branch? %) (= tagname (name (:tag (zip/node %)))))
                 (dzip/children-auto loc)))))


(defn attr
  "Returns the xml attribute named attrname, of the xml node at location loc."
  ([attrname]     (fn [loc] (attr loc attrname)))
  ([loc attrname] (when (zip/branch? loc) (-> loc zip/node :attrs attrname))))


(def ?xml (tag= "?xml"))
(def xbrl (tag= "xbrl"))
(def context (tag= "context"))
(def div (tag= "div"))

(def entity (tag= "entity"))
(def identifier (tag= "identifier"))

(def dei (tag= "dei:TradingSymbol"))


(def nca (tag= "NoncurrentAssetsus-gaap:PreferredStockParOrStatedValuePerShare"))



(def xml-spec
  [  #(zf/xml-> % :xbrl)
   #(zf/xml1-> %)
   ;:status % ; #(zf/xml1-> % (zf/attr :status))
    ;:ident  #(some-> %
    ;                 (zf/xml1-> :ident zf/text)
    ;                 (Long/parseLong))
    ;:title #(zf/xml1-> % :title zf/text)
    ;:title-lang #(zf/xml1-> % :title (zf/attr :lang))
    ;:children [#(zf/xml-> % :children :child)
    ;           {:name #(zf/xml1-> % :name zf/text)
    ;            :age  #(some-> %
    ;                           (zf/xml1-> :age zf/text)
    ;                           (Long/parseLong))}]
    ])




(comment

  (def xml-string (slurp "demodata/Q10/goog-20220930_htm.xml"))
  (count xml-string)
  (subs xml-string 0 1000)
  
  (def nodes
    (some-> xml-string
            xml/parse-str
            zip/xml-zip))
 
  (into-edn xml-spec nodes)
  

  (println (-> nodes first :tag namespace))
  (-> nodes count)
  
  (-> nodes
      zip/down
      zip/right
     ; zip/down
       zip/node
      ; first
      ;rest
     ; count
       :tag
       name
      )

  (defn print-el [n]
    (->>
       first
       first
       :content
       ;:second
       ;:tag
                                        ;name
       ))

  (defn ent-tag [e]
    (if-let [t (:tag e)]
      (name t)
      nil))

  (defn child-tags [e]
  (->> e
       (zip/children)
       (map ent-tag)
       (remove nil?)
       ))

 ; xbrl
 ; context
 ; entity period
 
  
  (zf/xml-> nodes )

  (zf/xml-> nodes
            (tag= "xbrl")
            (tag= "context")
            (tag= "entity"))

  (zf/xml-> nodes xbrl context entity identifier)

  (->> (zf/xml-> nodes xbrl)
       (map child-tags))

 (->> (zf/xml-> nodes
                 (tag= "xbrl")
                 (tag= "schemaRef"))
                                        ;(map child-tags)
      first
      )

  
  (->> (zf/xml-> nodes
                 (tag= "xbrl")
                 (tag= "context"))
       (map child-tags))

    (->> (zf/xml1-> nodes
                 (tag= "xbrl")
                 (tag= "context")
                ; (tag= "period")
                 (attr :id)
                 )
         ;type
          ; println
         )

  
 (->> (zf/xml-> nodes
                 (tag= "xbrl")
                 (tag= "CommonStockSharesOutstanding")
               ;  (tag= "measure")
                 )
                                        ;(map child-tags)
      ;first
      count
; (map zip/children)
      
      )


(->> (zf/xml-> nodes
                 (tag= "xbrl")
                 (tag= "MarketableSecuritiesCurrent")
               ;  (tag= "measure")
                 )
                                        ;(map child-tags)
     first
;     :attrs
      
     )

 (zf/xml-> nodes
           (tag= "xbrl")
           (tag= "InventoryNet")
           zx/text)

  (zf/xml-> nodes
           (tag= "xbrl")
           (tag= "LongTermDebtAndCapitalLeaseObligations")
           zx/text)

  (zf/xml-> nodes
           (tag= "xbrl")
           (tag= "EarningsPerShareBasic")
           zx/text)
  
(-> (zf/xml-> nodes
           (tag= "xbrl")
           (tag= "NetIncomeLoss")
           zx/text
           )
    count)

  
 



 

 
  
  (->> (zf/xml-> nodes xbrl context)
       (map child-tags))
  
  
  
  (->> (zf/xml1-> nodes xbrl context entity identifier)
       first
       :content
;       second
;       :tag
       )

  (->> (zf/xml-> nodes xbrl context)
     ;  (map #(-> % first :tag name))
       (map #(-> % first :attrs))
    
 ;     keys
      println
      )
  
  
                                        ; first
;   count
                                        ;first
   first;   last
;   keys
  #(zf/xml1-> % :xbrl)
   )
   

 ;
  )
