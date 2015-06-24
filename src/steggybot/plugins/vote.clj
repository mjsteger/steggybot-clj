(ns steggybot.plugins.vote
  "Sends a PING back given .pong"
  (:require [steggybot.db :as db]
            [datomic.api :as datomic]
            [clojure.algo.generic.functor :refer [fmap]]
            [steggybot.parse :refer [extract-word find-regex-uses]]))

(defn votes-about [db name]
  (def results (-> '[:find ?e :in $ ?name :where [?e :vote.entry/name ?name]]
                   (datomic/q db name)))
  (->> results
       (map first)
       (map #(datomic/entity db %))))


(defn votes-of [db name]
  (def results (-> '[:find ?e :in $ ?name :where [?e :vote.entry/voter ?name]]
                   (datomic/q db name)))
  (->> results
       (map first)
       (map #(datomic/entity db %))))

(defn sum-votes [votes]
  (->> votes
       (map :vote.entry/count)
       (reduce +)))

(defn commit-vote [irc message total]
  (def conn (db/get-conn irc))
  (datomic/transact conn [{:db/id (datomic/tempid :db.part/vote)
                            :vote.entry/name (:text message)
                            :vote.entry/count total
                            :vote.entry/voter (:nick message)}])
  (str "voted " total " for " (:text message)))

(defn handle-plus-minus [irc vote message]
  (def magnitude 1)
  (when magnitude
    (def total (if (= \+ (first vote)) magnitude (- 0 magnitude)))
    (commit-vote irc message total)
    ))

(defn handle-normal-vote [irc message parsed-message]
  (def magnitude 1)
  (def voted-for (first parsed-message))
  (def total (if (= "++" (last parsed-message)) magnitude (- 0 magnitude)))
  (commit-vote irc (merge message {:text voted-for}) total)
  (str "voted for " voted-for ": " (last parsed-message)))

(defmulti handle-vote
  (fn [_ c _] c))

(defmethod handle-vote :default [_ _ _] nil)

(defmethod handle-vote "score" [irc _ message]
  (def votes (-> irc
                  db/get-conn
                  datomic/db
                  (votes-about (:text message))))
  (str (:text message) " scores " (sum-votes votes) "."))

(defmethod handle-vote "show" [irc _ message]
  (def votes (-> irc
                 db/get-conn
                 datomic/db
                 (votes-about (:text message))))
  (def summary (->> votes
                    (group-by :vote.entry/voter)
                    (fmap sum-votes)))
  (str "summary for " (:text message) ": " (str summary)))

(defmethod handle-vote "by" [irc _ message]
  (def votes (-> irc
                 db/get-conn
                 datomic/db
                 (votes-of (:text message))))
  (def summary (->> votes
                    (group-by :vote.entry/name)
                    (fmap sum-votes)))
  (str "votes by " (:text message) ": " (str summary)))

(def plugin {:author "jneen/steggy"
             :doc {"vote" ".vote ++/-- : vote on things\\
                          can also use thing{++,--} to vote for/against a thing"
                   "vote-score" ".vote score <thing> : show the score for <thing>"
                   "vote-show" ".vote show <thing> : show votes about <thing>"
                   "vote-by" ".vote by <person> : show <person>'s votes"}
             :schema "vote.edn"
             :commands {"vote" (fn [irc message]
                                 (def first-char (-> message :text first))
                                 (let [handler (if ((set "+-") first-char)
                                                 handle-plus-minus
                                                 handle-vote)]
                                   (apply handler (concat [irc] (extract-word message)))))}
             :regex { :regex #"([^+]+)([-|+]{2})" :handler handle-normal-vote
                      }})
