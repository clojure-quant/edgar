(ns edgar.db.edn
  (:require
    [fipp.clojure]
    [clojure.edn :as edn]
    [clojure.pprint :refer [print-table]])
  (:import
   [java.time LocalDate]))

(defn edn-save [filename data]
  (let [s (with-out-str
            (fipp.clojure/pprint data {:width 40}))]
    (spit filename s)))


(defn localdate-rdr
  "Takes serialized Java object, tests for LocalDate, and returns LocalDate object."
  [[clazz _ object-string :as input]] ;; [java.time.LocalDate 861096493 "2021-04-22"]
  (if (= 'java.time.LocalDate clazz)
      ;; LocalDate string will be of form yyyy-mm-dd
      ;; which also matches the parse function.
    (java.time.LocalDate/parse object-string)
      ;; just returns input if serialized object is not LocalDate
    input))

(defn edn-read [filename]
(->> (slurp filename)
     (clojure.edn/read-string {:readers {'object localdate-rdr}})
    ))



(def company-ticker-lookup
  (atom {}))


(defn company-list->ticker-lookup [companies]
  (into {}
        (map (juxt :ticker identity) companies)))


(defn build-ticker-lookup []
  (->> (edn-read "data/companies.edn")
       (company-list->ticker-lookup)
       (reset! company-ticker-lookup)
       ))


(defn find-company-with-ticker [ticker]
  (get @company-ticker-lookup ticker)
 ) 

(defn save-companies [companies]
  (edn-save "data/companies.edn" companies))

  

(comment
  (build-ticker-lookup)
  (first @company-ticker-lookup)
  (count @company-ticker-lookup)

  (find-company-with-ticker "AAPL")
  (find-company-with-ticker "GOOG")
  (-> (find-company-with-ticker "GOOG") :cik)
  (print-table (vals @company-ticker-lookup))

;
)
  
