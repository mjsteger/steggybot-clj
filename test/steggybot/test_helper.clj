(ns steggybot.test-helper
  (:require [clojure.test]
            [steggybot.db :as db]
            [steggybot.core :as core]))

(defn setup-database
  ([] (setup-database (core/load-plugin-symbols)))
  ([symbols]
  (let [datomic-uri (core/datomic-uri)]
    (datomic.api/delete-database datomic-uri)
    (db/start (ref {:plugins (core/load-plugins symbols)
                    :datomic-uri datomic-uri})))))

(defn irc-object []
  (ref {:datomic-uri (core/datomic-uri)}))

(defn setup-test []
  (def test-name (str *ns*))
  (fn [f]
    (-> (clojure.string/replace test-name #"-test" "")
        symbol
        list
        setup-database)
    ; Consider grabbing from metadata if more things should be redeffed
    (with-redefs-fn {#'steggybot.bot/respond-with (fn [irc message r] )}
      #(f))))

(defn integration-test [message]
  ((steggybot.bot/privmsg-callback (core/load-plugins)) (irc-object) message))

(defn get-database []
  (-> (core/datomic-uri) datomic.api/connect datomic.api/db))
