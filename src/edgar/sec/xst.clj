(ns edgar.sec.xst
  (:require
   [clojure.java [io :as io]]
   [com.vincit.clj-xsd.core :as xsd]
   [ edgar.sec.download :as dl]
  ))

; https://github.com/rihteri/clj-xsd

; this library has the problem that it expects schema definition in a namespace.


(defn str->stream [s]
   (io/input-stream (java.io.ByteArrayInputStream. (.getBytes s))))


(comment
   (dl-filing 

  (def goog-xsd
    (dl/download-filing
      {:cik 1652044
       :no "0001652044-22-000090"
       :filename "goog-20220930.xsd"
       }))

  (def linkbase-xsd
    (dl/dl "http://www.xbrl.org/2003/xbrl-linkbase-2003-12-31.xsd"))
    
  
 (def goog-xml
    (dl/download-filing
      {:cik 1652044
       :no "0001652044-22-000090"
       :filename "goog-20220930_htm.xml"
       }))

                                        ; def - linkbase

 (def names ["goog-20220930.htm"
             "goog-20220930_cal.xml"
             "goog-20220930_def.xml"
             "goog-20220930_htm.xml"
             "goog-20220930_lab.xml"
             "goog-20220930_pre.xml"])




 
 (defn download-save-xml [cik no filename]
   (let [m {:cik cik :no no}
         mdl (assoc m :filename filename)
         body (dl/download-filing mdl)
         file-out (str "data/" filename)
         ]
     (spit file-out body)
     (println "saved file: " file-out)
     ))

 (doall
  (map #(download-save-xml 1652044 "0001652044-22-000090" %) names))
 

(->>
 (dl/dl "http://www.xbrl.org/2003/xbrl-linkbase-2003-12-31.xsd")
 (spit "data/xbrl-linkbase.xsd"))
     
 
(def schema 
  (xsd/read-schema
   ;(str->stream #_goog-xsd linkbase-xsd)
   (str->stream (slurp "data/demo.xsd"))
   ))

linkbase-xsd

schema
goog-xml

(xsd/parse schema (str->stream goog-xml))

(count goog-xml)
(count linkbase-xsd)

;
  )
