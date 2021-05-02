(ns edgar.nport
  (:require
   [clojure.string :as str]
   [clojure.xml :as xml]
   [clojure.java.io :as io]
  ;[clojure.data.xml :refer [parse-str]
   [clojure.zip :as zip]
   [clojure.data.zip :as zf]
   [clojure.data.zip.xml :as zip-xml]))



; zippers
; https://blog.korny.info/2014/03/08/xml-for-fun-and-profit.html
; http://clojure-doc.org/articles/tutorials/parsing_xml_with_zippers.html

(defn dl [n-p]
  (let [node-p (first n-p)
        tag-p (-> node-p :tag)
        nodes-c (:content node-p)
        v-c (for [node-c nodes-c]
              [(:tag node-c)
               (first (:content node-c))])]
  ;
    {:tag-p tag-p
     :c (into {} v-c)}
    (into {} v-c)))

(let [r (-> "demodata/a.xml" io/file xml/parse zip/xml-zip)
      g (zip-xml/xml1-> r :b)] ; 
  g
  ;(zf/descendants g)
  (dl g))

(defn dl-p [root & paths]
  (-> (apply zip-xml/xml1-> root paths)
      dl))

(def root (-> "demodata/N-PORT/primary_doc.xml"
              io/file
              xml/parse
              zip/xml-zip))
(def gi (zip-xml/xml1-> root :formData :genInfo))
(dl gi)

(dl-p root :formData :genInfo)
(dl-p root :formData :fundInfo)
(dl-p root :formData :invstOrSecs)