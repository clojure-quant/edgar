(ns edgar.sec.download
  (:require
   [clojure.string]
   [clojure.set]
   [clj-http.client :as client]
   [clj-http.cookies]
   [throttler.core]
   [cheshire.core :as cheshire] ; JSON Encoding
   ))

; https://www.sec.gov/edgar/sec-api-documentation
; https://www.sec.gov/os/accessing-edgar-data


; download helpers

(def my-cs (clj-http.cookies/cookie-store))

(defn dl [url & [filename]]
  (println "dl url: " url)
  (let [{:keys [body status]}
        (client/get url
                    {:max-redirects 5
                     :redirect-strategy :graceful
                     :cookie-store my-cs
                     :headers {"User-Agent"
                               "Mozilla/5.0 (custom build )"
; Sample Company Name AdminContact@<sample company domain>.com
                               }
                     })]
    (if (= status 200)
      (do (println "dl success.") 
        ;(when filename 
          ;  (spit filename body))
          body)
      (println "dl error: " status))))


; SEC permits only Current max request rate: 10 requests/second.
(def dl-t
  (throttler.core/throttle-fn dl 5 :second))



; company list

(defn download-company-tickers []
  (let [url "https://www.sec.gov/files/company_tickers.json"
        body (dl url)
        body-edn   (cheshire/parse-string body true)
        body-edn-vals  (vals body-edn)
        body-keys-ok (map #(clojure.set/rename-keys % {:cik_str :cik}) body-edn-vals)
        ]
   (into [] body-keys-ok)
    ))


; submission JSON:
; Each entity’s current filing history is available at the following URL:
; https://data.sec.gov/submissions/CIK##########.json
; Where the ########## is the entity’s 10-digit Central Index Key (CIK), including leading zeros.
;  contains metadata such as current name, former name, and stock exchanges and ticker symbols of publicly-traded companies.
; property path contains at least one year’s of filing or to 1,000 (whichever is more) of the most recent filings  in a compact columnar data array.
; If the entity has additional filings, files will contain an array of additional JSON files
; and the date range for the filings each one contains.

(defn download-submissions [cik]
  (let [cik10 (format "%010d" cik)
        url (str "https://data.sec.gov/submissions/CIK" cik10 ".json") ; cik number formatted to have 10 digit leading zeros
        _ (println "downloading submissions from url: " url)
        body (dl-t url)
        body-edn (cheshire/parse-string body true)
        ]
 body-edn
))
    
(defn filings-recent [submissions]
  (let [recent (get-in submissions [:filings :recent])]
    (map (fn [form
              date-filing date-report date-accept
              xbrl xbrl-il
              no-film no-file no-access
              primary primary-desc
              act size items]
         {:form form
          :date-filing date-report
          :date-report date-report
          :date-accept date-accept
          :xbrl xbrl
          :xbrl-il xbrl-il
          :no-film no-film
          :no-file no-file
          :no-access no-access
          :primary primary
          :primary-desc primary-desc
          :act act
          :size size
          :items items
          })
       (:form recent)
       (:filingDate recent)
       (:reportDate recent)
       (:acceptanceDateTime recent)
       (:isXBRL recent)
       (:isInlineXBRL recent)
       (:filmNumber recent)
       (:fileNumber recent)
       (:accessionNumber recent)
       (:primaryDocument recent)
       (:primaryDocDescription recent)
       (:act recent)
       (:size recent)
       (:items recent)
       )))


(defn filings-recent-type [submissions form]
  (->>
    (filings-recent submissions)
    (filter #(= (:form %) form))))


; FILING DOWNLOAD ***************************************************************************

; https://www.sec.gov/Archives/edgar/data/51143/000156218021002520/primarydocument.xml


(def base-url "https://www.sec.gov/Archives/edgar/data/")

(defn url-filing [cik no-access filename]
  (let [no-nodash (clojure.string/replace no-access #"-" "")
        url-filing-dir (str base-url cik "/" no-nodash)]
    (if filename
      (str url-filing-dir "/" filename)
      url-filing-dir)))

(defn url-filing-index [{:keys [cik no]}]
  (str base-url cik "/" no "-index.html"))


;https://www.sec.gov/Archives/edgar/data/
;1004655/000175272421069935/primary_doc.xml

; example files of a filing directory:
; FilingSummary.xml     - contains info about the files that are released
; goog-20220930_cal.xml
; goog-20220930_lab.xml
; goog-20220930_def.xml
; goog-20220930_pre.xml
; goog-20220930.xsd
; 0001652044-22-000090-xbrl.zip
; googexhibit3101q32022.htm
; googexhibit3102q32022.htm
; goog-20220930.htm
; googexhibit3201q32022.htm

;  https://data.sec.gov/api/xbrl/companyfacts/CIK##########.json

(defn url-company-facts [cik]
  (let [base-url "https://data.sec.gov/api/xbrl/companyfacts/CIK"
        cik10 (format "%010d" cik)]
  (str base-url cik10 ".json")))

(defn download-company-facts [cik]
  (let [url (url-company-facts cik)
        body (dl url)
        body-edn   (cheshire/parse-string body true)
        ;body-edn-vals  (vals body-edn)
        ;body-keys-ok (map #(clojure.set/rename-keys % {:cik_str :cik}) body-edn-vals)
        ]
                                        ;(into [] body-keys-ok)
    body-edn
    ))

; bulkdata
; https://www.sec.gov/Archives/edgar/daily-index/bulkdata/submissions.zip

   


;; EXPERIMENTS 
(comment

  (client/get "https://www.sec.gov/Archives/edgar/data/1004655/0001752724-21-069935-index.html"
              {:max-redirects 5 :redirect-strategy :graceful})

  ; throtteling
  
 (def plust (throttler.core/throttle-fn + 5 :second))

  ; this should be fast
  (time
   (map #(plust 1 %) (range 2)))

  (time
   (map #(plust 1 %) (range 20)))
  
  ; company tickers
  (def ctl
    (download-company-tickers))

  ctl
  (count ctl)
  (get ctl 1)
  (first ctl)
  (-> ctl first :cik_str type)
  

   ; submissions
  (format "%010d" 1234)
  (download-submissions 1652044)
  (def sgoog (download-submissions 1652044))
  
  (-> sgoog
      ;(dissoc :filings)  println
      :filings
      :recent
;      :form
      keys
;      count
      println
      )

  (-> (filings-recent sgoog)
      last
      println)

  (-> (filings-recent-type sgoog "10-Q")
      first
      println
      )
  {:date-filing 2022-09-30
   :primary goog-20220930.htm ; in url
   :no-file 001-37580
   :act 34, :size 11978687,
   :no-film 221330920,
   :no-access 0001652044-22-000090,; in url
   :form 10-Q,
   :primary-desc 10-Q,
   :date-accept 2022-10-25T21:32:59.000Z,
   :date-report 2022-09-30}
  ;  https://www.sec.gov/Archives/edgar/data/1652044/000165204422000090/goog-20220930.htm
 
  
   ;  https://www.sec.gov/ix?doc=/Archives/edgar/data/1652044/000165204422000090/goog-20220930.htm

  
  ; filings

  (println 
    (filing-url
      {:cik 1004655
       :no "0001752724-21-069935"}))

  (println
    (filing-url
      {:cik 1652044
       :no "0001652044-22-000090"}))

 (println
    (filing-url
      {:cik 1652044
       :no "0001652044-22-000090"}))
 
 (println
    (filing-url
      {:cik 1652044
       :no "0001652044-22-000090"
       :filename "bongo.xml"
       }))


    (download-filing
      {:cik 1652044
       :no "0001652044-22-000090"
       :filename "goog-20220930.xsd"
       })

 

  (println 
   (company-facts-url 1652044))

  (def facts-goog
    (download-company-facts 1652044))

  (keys facts-goog)
  (:entityName facts-goog)
  (->  facts-goog
       :facts
       :us-gaap
       keys
       )
 
  


  ;
  )


