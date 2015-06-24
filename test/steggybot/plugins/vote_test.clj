(ns steggybot.plugins.vote-test
  (:require [clojure.test :refer :all]
            [steggybot.plugins.vote :as vote]
            [steggybot.test-helper :as test]
            [steggybot.bot :as bot]
            [steggybot.core :as core]
            ))

(use-fixtures :each (test/setup-test))

(defn get-votes-about [name]
  (vote/sum-votes (vote/votes-of (test/get-database) name)))

(deftest basic-voting
  (vote/handle-plus-minus (test/irc-object) "+" {:text "steggy" :nick "steggy"})
  (testing "with a single vote"
    (is (= 1 (get-votes-about "steggy")))))

(deftest basic-passive-voting
  (vote/handle-normal-vote (test/irc-object) {:text "steggy++" :nick "steggy"} ["steggy" "++"])
  (testing "with a passive vote"
    (is (= 1 (get-votes-about "steggy")))))

(deftest basic-integration-voting
  (test/integration-test {:text "steggy++" :nick "steggy"})
  (testing "with basic integration"
    (is (= 1 (get-votes-about "steggy")))))
