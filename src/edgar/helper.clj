(ns edgar.helper
  (:require
   [fipp.clojure]
   )
  )


(defn save [filename data]
  (let [s (with-out-str
            (fipp.clojure/pprint data {:width 40}))
        ]
    (spit filename s)))