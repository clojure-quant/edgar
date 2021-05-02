(ns edgar.download
  (:require
   [clojure.edn :as edn]
   [clj-http.client :as client])
  (:import
   [java.time LocalDate]))

(defn localdate-rdr
  "Takes serialized Java object, tests for LocalDate, and returns LocalDate object."
  [[clazz _ object-string :as input]] ;; [java.time.LocalDate 861096493 "2021-04-22"]
  (if (= 'java.time.LocalDate clazz)
      ;; LocalDate string will be of form yyyy-mm-dd
      ;; which also matches the parse function.
    (java.time.LocalDate/parse object-string)
      ;; just returns input if serialized object is not LocalDate
    input))


;https://www.sec.gov/Archives/edgar/data/320193/000032019321000056/0000320193-21-000056-index.htm  


(def base-url "https://www.sec.gov/Archives/edgar/data/")

(defn filing-url [{:keys [cik no]}]
  (str base-url cik "/" no "-index.html"))


(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))

(defn dl-filing [f]
  (println "dl filing: " f)
  (let [url (filing-url f)
        _ (println "dl url: " url)
        {:keys [body status]} 
        (client/get url 
                    {:max-redirects 5 
                     :redirect-strategy :graceful})
        filename (filing-file f)
        ]
    (if (= status 200)
    (spit filename body)
    (println "error: " status))
    ))

(client/get "https://www.sec.gov/Archives/edgar/data/1004655/0001752724-21-069935-index.html"
            {:max-redirects 5 :redirect-strategy :graceful})

 (dl-filing {:cik 1004655
              :no "0001752724-21-069935"})

(->> (slurp "report/index.edn")
     (clojure.edn/read-string {;:keywordize-keys true
                               :readers {'object localdate-rdr}})
    ;count
     ;first
     (take 10)
     ;(skip 100)
     ;(map dl-filing)
     (map dl-filing)
     )


