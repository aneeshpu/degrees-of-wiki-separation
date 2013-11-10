(ns degrees-of-wiki-separation.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn get-page
  [url]
  (html/html-resource (java.net.URL. url)))

(defn get-links
  [url]
  (map #(get % :href ) (map #(get % :attrs ) (html/select (get-page url) [:a ]))))


(defn is-link-present
  [url target]
  (reduce #(or %1 %2) (map #(if (= % target)
                              true false) (get-links url))))
