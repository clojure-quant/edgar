(ns edgar.download
  (:require
   [clj-http.client :as client]))

;https://www.sec.gov/Archives/edgar/data/320193/000032019321000056/0000320193-21-000056-index.htm  


(def base-url "https://www.sec.gov/Archives/edgar/data/")

(defn filing-url [{:keys [cik no]}]
  (str base-url cik "/" no "-index.html"))


(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))


; https://www.sec.gov/Archives/edgar/data/51143/
; /Archives/edgar/data/1004655/000175272421069935/primary_doc.xml
; https://www.sec.gov/Archives/edgar/data/51143/000156218021002520/primarydocument.xml

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
      (do (spit filename body)
          body)
    (println "error: " status))
    ))


(comment 
  
(client/get "https://www.sec.gov/Archives/edgar/data/1004655/0001752724-21-069935-index.html"
            {:max-redirects 5 :redirect-strategy :graceful})

  ;
)


