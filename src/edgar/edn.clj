(ns edgar.edn
  (:require
   [fipp.clojure]
     [clojure.edn :as edn])
  (:import
   [java.time LocalDate])
  )

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