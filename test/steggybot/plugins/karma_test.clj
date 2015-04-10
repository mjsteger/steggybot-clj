(ns steggybot.plugins.karma-test
  (:require [clojure.test :refer :all]
            [steggybot.plugins.karma :as karma]))

(deftest handle-karma
  (testing "+"
    (is (= (karma/handle-karma (ref {}) "+" "thing")
           "TODO: thing"))))
