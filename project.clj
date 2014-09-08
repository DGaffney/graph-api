(defproject graph-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
     [org.clojure/tools.trace "0.7.8"]
     [clj-time "0.5.0"]
     [cheshire "5.3.1"]
     [environ "0.5.0"]
     [compojure "1.1.5"]
     [org.clojure/data.json "0.2.1"]
     [speclj "2.9.0"]
     [ring "1.1.8"]
     [commons-codec/commons-codec "1.9"]
     [noir "1.3.0-beta1"]
     [com.novemberain/monger "1.3.4"]
     [clj-http "1.0.0"]
     [ring/ring-json "0.3.1"]
     [digest "1.3.0"]]
  :main ^:skip-aot graph-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
