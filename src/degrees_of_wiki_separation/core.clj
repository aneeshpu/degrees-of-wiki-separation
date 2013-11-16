(ns degrees-of-wiki-separation.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn is-image
  [url]
  (some #(.endsWith url %) ["jpeg" "gif"]))

(defn get-page
  [url]
  (if
    (and
      (not= nil url)
      (not (is-image url)))
    (try
      (html/html-resource
        (java.net.URL.
          (if
            (.startsWith url "http:")
            url
            (.concat "http:" url))))
      (catch java.io.FileNotFoundException fofe
        (println (.getMessage fofe))))))

  (defn get-links
    [url]
    (map #(get % :href )
      (map #(get % :attrs )
        (html/select
          (get-page url)
          [:a ]))))


  (defn is-link-present
    [urls target]
    (reduce #(or %1 %2) (map #(= % target) urls)))

  (def not-nil?
    (complement nil?))

  (defn is-valid
    [url]
    (and (not-nil? url)
      (not (.startsWith url "#"))
      (or (.startsWith url "http://")
        (.startsWith url "//"))))


  (defn find-link
    [url target depth]
    (println "searching for " target " on " url)
    (let [links (get-links url)]
      (if (and (> 0 (count links)) (is-link-present links target))
        (println "found " target " at " url)
        (if (<= depth 0)
          (println "could not find " target "on " url)
          ((println "searching deeper. Is links == nil?" (nil? links))
            (doseq [link links]
              (if (is-valid link)
                (find-link link target (dec depth)))
              ;#(find-link % target (dec depth))
              ))))))