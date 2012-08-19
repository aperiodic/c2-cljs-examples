(defproject c2-cljs-examples "0.1.0-SNAPSHOT"
  :description "An attempt to rewrite c2's examples in clojurescript."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.keminglabs/c2 "0.2.1-SNAPSHOT"]
                 [compojure "1.0.4"]
                 [hiccup "1.0.0"]]
  :plugins [[lein-cljsbuild "0.2.4"]
            [lein-ring "0.7.1"]]
  :ring {:handler c2-cljs-examples.routes/handler}
  :source-paths ["src/clj"]
  :cljsbuild
    {:builds
      [{:source-path "src/cljs"
        :compiler
          {:output-to "resources/public/js/main.js"
           :optimizations :whitespace
           :pretty-print true}}]})
