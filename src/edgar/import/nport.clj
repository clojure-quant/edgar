(ns edgar.import.nport
  (:require
   [clojure.java.io :as io]
   [edgar.edn :refer [edn-read edn-save]]
   [edgar.sec.download :refer [dl-primary]]
   ;[edgar.sec.filing :refer [parse-filing]]
   [edgar.sec.portfolio :refer [extract-pf-str]]
   [edgar.db :as db]))

(defn nport-file-xml [{:keys [cik no]}]
  (str "data/nport-xml/" cik "_" no ".xml"))

(defn nport-download [f]
  (let [f-xml (nport-file-xml f)]
    (if (.exists (io/file f-xml))
      (println "skipping download. file existing: " f)
      (let [body (dl-primary f)]
        (if body
          (spit f-xml body)
          (println "nport-download failed: " f))))))


(defn nport-file [{:keys [cik sid no]}]
  (str "data/nport/" cik "_" no "_" sid ".edn"))

(defn save [{:keys [sid cik no] :as pf}]
  (if (and cik sid no)
    (do (println "saving cik" cik "sid:" sid " no:" no)
        (db/add-report pf)
        (edn-save (nport-file pf) pf))
    (println "not saving. missing cik/sid/no" 
             (select-keys pf [:cik :sid :no]) pf)))

(defn nport-import [f]
  (let [f-xml (nport-file-xml f)
        body (slurp f-xml)
        pf (when body
             (-> (extract-pf-str body)
                 (assoc :no (:no f))))]
    (if pf
      (save pf)
      (println "no portfolio. not saving: " f))))


(defn nport [f]
  (try (do (nport-download f)
           (nport-import f))
       (catch Exception e
         (println "nport exception: " f ))))


(comment

    (nport {:cik 2110
            :name "Clayton Street Trust"
            :no "0001145549-19-046178"})
  
  ; parse error:
  (nport {:cik 1660765
          :name "Clayton Street Trust"
          :no "0001741773-19-001031"})
  
  (nport {:cik 1004655
          :no "0001752724-21-069935"})

  (nport {:cik 1217286
          :name "JPMorgan Trust I" 
          :no "0001752724-20-034694"})
  
  (nport {:cik 1217286
          :name "JPMorgan Trust I"
          :no "0001752724-20-057878"})


  ;
  )
