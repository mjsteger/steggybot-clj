(defproject steggybot "0.1.0-SNAPSHOT"
  :description "steggybot:  "
  :url "https://github.com/GoodGuide/steggybot-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [irclj "0.5.0-alpha4"]
                 [com.datomic/datomic-free "0.9.5067" :exclusions [joda-time]]
                 [org.clojure/algo.generic "0.1.0"]
                 [clojail "1.0.6"],
                 [org.clojure/tools.namespace "0.2.7"]
                 [overtone/at-at "1.2.0"]
                 [clj-http "1.0.1"]]
  :main steggybot.core)
