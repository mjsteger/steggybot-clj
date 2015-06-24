(ns steggybot.parse
  "Parsing utilities")

(defn find-regex-uses [message regex]
  (let [matches (drop 1 (re-matches regex (:text message)))]
        (when-not (empty? matches)
          matches)))


(defn extract-command [message]
  (when-let [[_ cmd rest-of-text]
           (re-find #"^[.](\S+)\s*(.*)$" (:text message))]
    [cmd (assoc message :text rest-of-text
                        :command cmd)]))

(defn extract-word [message]
  (when-let [[_ word rest-of-text]
           (re-find #"^(\S+)\s*(.*)$" (:text message))]
    [word (assoc message :text rest-of-text)]))
