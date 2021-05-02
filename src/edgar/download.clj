(ns edgar.download
  (:require
   [clojure.string]
   [clj-http.client :as client]
   [clj-http.cookies]
   [throttler.core]))

(def my-cs (clj-http.cookies/cookie-store))

(defn dl [url & [filename]]
  (println "dl url: " url)
  (let [{:keys [body status]}
        (client/get url
                    {:max-redirects 5
                     :redirect-strategy :graceful
                     :cookie-store my-cs
                     :headers {"User-Agent"
                               "Mozilla/5.0 (custom build )"}
                     })]
    (if (= status 200)
      (do (println "dl success.") 
        ;(when filename 
          ;  (spit filename body))
          body)
      (println "error: " status))))


; Current max request rate: 10 requests/second.
(def dl-t (throttler.core/throttle-fn dl 5 :second))


;https://www.sec.gov/Archives/edgar/data/320193/000032019321000056/0000320193-21-000056-index.htm  
; https://www.sec.gov/Archives/edgar/data/51143/
; /Archives/edgar/data/1004655/000175272421069935/primary_doc.xml
; https://www.sec.gov/Archives/edgar/data/51143/000156218021002520/primarydocument.xml


(def base-url "https://www.sec.gov/Archives/edgar/data/")

(defn filing-url [{:keys [cik no]}]
  (str base-url cik "/" no "-index.html"))

(defn dl-filing [f]
  (println "dl filing: " f)
  (let [url (filing-url f)]
    (dl-t url)))

;https://www.sec.gov/Archives/edgar/data/
;1004655/000175272421069935/primary_doc.xml

(defn primary-url [{:keys [cik no]}]
  (let [no-nodash (clojure.string/replace no #"-" "")]
  (str base-url cik "/" no-nodash "/primary_doc.xml")))

(defn dl-primary [f]
  (println "dl primary: " f)
  (let [url (primary-url f)]
    (dl-t url)))

(comment

  (client/get "https://www.sec.gov/Archives/edgar/data/1004655/0001752724-21-069935-index.html"
              {:max-redirects 5 :redirect-strategy :graceful})

  (def plust (throttler.core/throttle-fn + 5 :second))

  ; this should be fast
  (time
   (map #(plust 1 %) (range 2)))

  (time
   (map #(plust 1 %) (range 20)))

  (primary-url
  {:cik 1004655
   :no "0001752724-21-069935"})


  ;
  )


