(ns edgar.filing
  (:require
   [hickory.core :as h]
   [hickory.select :as s]))

; text

(defn b [x]
  (or (vector? x) (seq? x) (map? x)))

(defn ch [el]
  (if (map? el)
    (:content el)
    el))

(defn text [el]
  (->> (tree-seq b ch el)
      ;(map :content)
       (filter string?)
      ;(remove nil?)
      ;clojure.string/join
       ))


(defn table [e]
  (let [rows (s/select (s/descendant
                        (s/tag :table)
                        (s/tag :tbody)
                        (s/tag :tr)) e)
        rr {:type :document, :content rows}
        ; header
        h (s/select (s/descendant (s/tag :tr) (s/tag :th)) rr)
        col-count (count h)
        ; rows
        r (s/select (s/child (s/tag :td)) rr)
        r (text r)
        r (partition col-count r)]
     ;(s/tag :td)
                         ; (s/tag :th)


    {;:rows rows
     :c (count rows)
     :col-count col-count
     :header (-> h text)
     :row-count (count r)
     :rows r}))


(defn filing-summary [h]
  {:header
   (->> (s/select (s/descendant (s/class "infoHead")) h)
        (map #(get-in % [:content])))
   :vals
   (->> (s/select (s/descendant (s/class "info")) h)
        (map #(get-in % [:content])))})


(defn filer [h]
  (s/select (s/id "filerDiv") h) ; filer list
)

(defn filing [h]
  (let [[summary docs-table] (s/select (s/id "formDiv") h)]
    {:filing ;(text 
     (-> (filing-summary h)
                 ; (get-in [:element :content])
) ;summary
     :docs (table docs-table)
     ;:filer (filer h)
     }))


(defn parse-filing [body]
    (->> body
         h/parse
         ;h/as-hiccup
         h/as-hickory
         filing
         )
  )

(defn filing-file [{:keys [cik no]}]
  (str "data/filings/" cik "-" no "-index.html"))

(defn load-filing [f]
  (->> f
       filing-file
       slurp
       parse-filing))


(comment

  (load-filing {:cik 1004655
    :no "0001752724-21-069935"})

  ; /Archives/edgar/data/1004655/000175272421069935/primary_doc.xml

  (elrc ["1"
         {:content ["4"]}
         "5"
         {:type :element
          :content "d"}
         "\n "])



 ;  
  )