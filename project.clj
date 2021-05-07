(defproject edgar "0.0.1-SNAPSHOT"
  :min-lein-version "2.9.4"
  :managed-dependencies [; dependency conflict resolution
                         [org.clojure/clojure "1.10.3"]
                         [org.clojure/core.async "1.3.618"]
                         [org.slf4j/slf4j-api "2.0.0-alpha1"] ; techml webly
                         [com.cognitect/transit-clj "1.0.324"] ; clj kondo webly
                         [com.cognitect/transit-cljs "0.8.269"]
                         [com.fasterxml.jackson.core/jackson-core "2.12.3"]

                         [org.apache.httpcomponents/httpcore "4.4.14"]
                         [commons-codec "1.15"]
                         [com.taoensso/timbre "5.1.2"]
                         [org.clojure/tools.reader "1.3.5"]
                         [com.taoensso/encore "3.19.0"]
                           
                         [org.ow2.asm/asm "9.1"] ; tech-ml goldly
                         ;[org.ow2.asm/asm-tree "9.1"]
                         ;[org.ow2.asm/asm-analysis "9.1"]
                         ;[org.ow2.asm/asm-commons "9.1"]


                         ;
                         ]
  :dependencies [[techascent/tech.ml.dataset "6.00-beta-7"]
                  ; time
                 [clojure.java-time "0.3.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]

                 ; encoding
                 [org.clojure/data.json "2.1.0"] ; https://github.com/thheller/shadow-cljs/issues/872
                 [luminus-transit "0.1.2"]
                 [cheshire "5.10.0"]  ; JSON parsings and pretty printing
                 [fipp "0.6.23"] ; edn pretty printing
                 [hickory "0.7.1"
                  :exclusions [[org.clojure/clojurescript]]] ; html parser
                 [org.clojure/data.zip "1.0.0"]
                 [clj-http "3.12.1"]
                 [throttler "1.0.0" ; api rate-limits 
                  :exclusions  [[org.clojure/clojure]
                                [org.clojure/core.async]]]; has very old core.async

                 [io.replikativ/datahike "0.3.6"
                  :exclusions [[org.clojure/clojurescript]]
                  ]
                 ;[org.pinkgorilla/goldly "0.2.26"]
                 [org.pinkgorilla/goldly-server "0.2.32"]
                 ]

  :target-path  "target/jar"
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths  ["resources"]
  :repl-options {:welcome (println "Welcome to the magical world of the repl!")
                 :init-ns edgar.repl
                 :init (println "here we are in" *ns*)}
  :profiles {:dev {:dependencies [[clj-kondo "2021.04.23"]]
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

  :aliases {"edgar"
            ["run" "-m" "edgar.app"]
})
