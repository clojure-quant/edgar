(defproject edgar "0.0.1-SNAPSHOT"

  :min-lein-version "2.9.4" ; nrepl 0.7.0


  :dependencies [[techascent/tech.ml.dataset "6.00-beta-7"]

                 ; dependency conflict resolution
                 [commons-codec "1.15"]
                 [org.clojure/data.xml "0.0.8"]
                 [com.fasterxml.jackson.core/jackson-core "2.12.3"] ; cheshire + jsonista
                 [org.ow2.asm/asm "9.1"] ; core.asymc tools.reader
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.610"]
                 [com.taoensso/timbre "5.1.2"] ; clj/cljs logging
                  ; time
                 [clojure.java-time "0.3.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]

                 ; encoding
                 [org.clojure/data.json "2.1.0"] ; https://github.com/thheller/shadow-cljs/issues/872
                 [luminus-transit "0.1.2"]
                 [cheshire "5.10.0"]  ; JSON parsings and pretty printing
                 [fipp "0.6.23"] ; edn pretty printing
                 [hickory "0.7.1"] ; html parser
                 [org.clojure/data.zip "0.1.1"]
                 [clj-http "3.12.0"]
                 [throttler "1.0.0" ; api rate-limits 
                  :exclusions  [[org.clojure/clojure]
                                [org.clojure/core.async]]]; has very old core.async
                 
                   [clojure.java-time "0.3.2"]
                 [io.replikativ/datahike "0.3.6"]
                 ;[org.pinkgorilla/goldly-server "0.2.26"]
                 ]

  :target-path  "target/jar"
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths  ["resources"]
  :profiles {:dev {:dependencies [[clj-kondo "2021.03.31"] ; 
                                  [ring/ring-mock "0.4.0"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]]
                   :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
                   :cloverage    {:codecov? true
                                  ;; In case we want to exclude stuff
                                  ;; :ns-exclude-regex [#".*util.instrument"]
                                  ;; :test-ns-regex [#"^((?!debug-integration-test).)*$$"]
                                  }
                   ;; TODO : Make cljfmt really nice : https://devhub.io/repos/bbatsov-cljfmt
                   :cljfmt       {:indents {as->                [[:inner 0]]
                                            with-debug-bindings [[:inner 0]]
                                            merge-meta          [[:inner 0]]
                                            try-if-let          [[:block 1]]}}}}

  :aliases {"import"
            ["run" "-m" "edgar.app"]
            
            "info"
            ["run" "-m" "edgar.info"]
            }
  
  )
