(ns edgar.import.again
  (:require
   [clojure.java.io :as io]
   [edgar.import.nport :refer [nport]])
  (:import java.io.File))

; nport-edn

(def re-nport #"(.*)_(.*)_(.*)\.edn")
(defn nport-info [f]
  (let [s (.getName f)
        m (re-find re-nport s)
        [_ cik no sid] m]
    {:cik cik
     :no no
     :sid sid}))

; nport-xml

(def re-nport-xml #"(.*)_(.*)\.xml")
(defn nport-info-xml [f]
  (let [s (.getName f)
        m (re-find re-nport-xml s)
        [_ cik no] m]
    {:cik cik
     :no no}))

(defn reimport-nport []
  (let [dir (clojure.java.io/file "data/nport-xml")
        files (.listFiles dir)]
    (->>
     (map nport-info-xml files)
     (map nport))))

(comment
  (reimport-nport)


 ; 
  )