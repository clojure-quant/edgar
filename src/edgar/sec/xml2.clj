(ns edgar.sec.xml2
  (:require
   [clojure.string :as str]
   [clojure.zip :as zip]
   [clojure.data.xml :as xml]
   [clojure.data.zip.xml :as zx]
   [clojure.data.zip :as dzip]
   [clojure.java.io :as io]
   [into-edn :refer [into-edn]] ; https://github.com/thebusby/into-edn
   [clojure.pprint :refer [print-table]]
   ))

; nice online viewer for a huge xml document
; https://jsonformatter.org/xml-viewer


; https://github.com/nonsequitur/smex

(defn remove-empty [m]
  (into {}
        (remove (fn [[k v]]
                  (nil? v)) m)))

; (remove-empty {:a 1 :b nil})


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



(defn extract-context-node [n]
  (let [n (first n)
        id (get-in n [:attrs :id])
        ;_ (println "extr" id)
        nz (zip/xml-zip n)
         x (zx/xml1-> nz
                      (tag= "entity")
                      zx/text)
         seg (zx/xml1-> nz
                        (tag= "entity")
                        (tag= "segment")
                      zx/text)
         dts (zx/xml1-> nz
                      (tag= "period")
                      (tag= "startDate")
                      zx/text)
         dte (zx/xml1-> nz
                      (tag= "period")
                      (tag= "endDate")
                      zx/text)
         dti (zx/xml1-> nz
                      (tag= "period")
                      (tag= "instant")
                      zx/text)
        ]
    (remove-empty
     {:id id
      :ref x
      :seg seg
      :dts dts
      :dte dte
      :dti dti})))
 
(defn extract-context [nodes]
  (->> (zx/xml-> nodes
                 (tag= "xbrl")
                 (tag= "context"))
       ;(remove string?)
      (map extract-context-node)
      ; type
;       count ; extract-context-node
   ;    println
       ))

(defn build-ref-dict [nodes]
  (into {}
     (map (juxt :id identity)   
       (extract-context nodes))))




  

(defn measurement-item [name nodes]
  (let [n (first nodes)
        attrs (:attrs n) ; :contextRef :decimals :id :unitRef
        ref (:contextRef attrs)
        id (:id attrs)
       text  (zx/xml1-> nodes zx/text)
        ]
   ; (println (keys attrs))
    (remove-empty
     {:name name
     :id id
     :ref ref
     :text text
     })))


(defn measurement [nodes name]
 (let [nodes (zx/xml-> nodes
                 (tag= "xbrl")
                 (tag= name))]
   (map (partial measurement-item name) nodes)
   ))

(defn link-period [id-dict m]
  (let [period (get id-dict (:ref m))]
   ; (println "linked period: " period)
   (if period
     (merge m period)
     m)
   ))

(defn measurements [nodes names]
  (let [id-dict (build-ref-dict nodes)
        ms (apply concat
             (map (partial measurement nodes) names))
        ]
    ;(println "keys in dict:" (keys id-dict))
    (map
     (partial link-period id-dict)
      ms)
    ))


(defn parse-measurements [xml-string
                          measurement-names]
  (let [nodes (some->
                xml-string
                xml/parse-str
                zip/xml-zip)]
    (measurements
     nodes
     measurement-names)))
  


; instead of using zippers to extract the
; relevant data, it 
; might be better idea to use into-edn
; currently not working.
(def xml-spec
  [  #(zx/xml-> % :xbrl)
   #(zx/xml1-> %)
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

(def dei (tag= "dei:TradingSymbol"))

(def nca (tag= "NoncurrentAssetsus-gaap:PreferredStockParOrStatedValuePerShare"))



  
  (def xml-string (slurp "demodata/Q10/goog-20220930_htm.xml"))
  (count xml-string)
  (subs xml-string 0 1000)
  
  (def nodes
    (some-> xml-string
            xml/parse-str
            zip/xml-zip))
 
  ;(into-edn xml-spec nodes)

  (->>
    (extract-context nodes)
    (print-table
     [;:id
      ;:ref 
      ;:seg 
      :dts 
      :dte
      :dti]
     ))

  (build-ref-dict nodes)

  
  (->>
    (measurement nodes "InventoryNet")
    (print-table [:text :ref])
   )

  (->>
    (concat
     (measurement nodes "InventoryNet")
     (measurement nodes "AccountsPayableCurrent"))
    (print-table [:name :text])
   )

  (->>
    (measurements
     nodes
     ["InventoryNet"
      "AccountsPayableCurrent"])
    (print-table [:name :text
                  :dts :dte :dti]))

    (->>
     (parse-measurements
       xml-string
       ["InventoryNet"
        "AccountsPayableCurrent"
        "RevenueFromContractWithCustomerExcludingAssessedTax"
        "CostOfRevenue"
        "GeneralAndAdministrativeExpense"
        "NetIncomeLoss"
        "EarningsPerShareDiluted"
        ])
    
       (print-table [:name :text
                     :dts :dte :dti]))


    
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
  
  (->> (zf/xml->
        nodes
        (tag= "xbrl")
        (attr "contextRef")
          ; (tag= "CommonStockSharesOutstanding")
                 )
      ;count
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
     

 ;
  )
