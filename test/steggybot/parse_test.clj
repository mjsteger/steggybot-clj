(ns steggybot.parse-test
  (:require [clojure.test :refer :all]
            [steggybot.parse :as parse]))

(deftest extract-command
  (testing "when there is a command"
    (is (= (parse/extract-command {:text ".foo bar baz"})
           ["foo" {:command "foo" :text "bar baz"}])))

  (testing "when there is a non-word command"
    (is (= (parse/extract-command {:text ".++ jneen"})
           ["++" {:command "++" :text "jneen"}]))

  (testing "when there is not a command"
    (is (nil? (parse/extract-command {:text "foo bar baz"}))))))

(deftest extract-word
  (testing "when there are words"
    (is (= (parse/extract-word {:text "foo bar baz"})
           ["foo" {:text "bar baz"}])))

  (testing "when there is only whitespace"
    (is (nil? (parse/extract-word {:text "     "})))))

(deftest find-regex-uses
  (testing "with simple match"
    (is (= (parse/find-regex-uses {:text "testfoo"} #"(test).*") '("test"))))
  (testing "with no match"
    (is (= (parse/find-regex-uses {:text "testfoo"} #"asht.*") nil))))
