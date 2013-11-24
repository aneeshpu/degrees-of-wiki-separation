(ns degrees-of-wiki-separation.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))



(defn image?
  [url]
  (some #(.endsWith url %) ["jpg" "jpeg" "gif"]))


(defn get-page
  [url]
  (if
    (and
      (not= nil url)
      (not (image? url)))
    (try
      (html/html-resource
        (java.net.URL.
          (if
            (.startsWith url "http:")
            url
            (.concat "http:" url))))
      (catch java.io.FileNotFoundException fofe
        (.printStackTrace fofe))
      (catch Exception e
        (.printStackTrace e)))))

(defn get-links
  [url]
  (println "getting links from " url)
  (map #(get % :href )
    (map #(get % :attrs )
      (html/select
        (get-page url)
        [:div#bodyContent :a ]))))


(defn link-present?
  [urls target]
  (or
    (some #(= % target) urls)
    (some #(= % (.concat "http://en.wikipedia.org" target)) urls)))

(def not-nil?
  (let [blah (complement nil?)]
    (println "inside not-nil?-------------" blah)
    blah))

(defn searchable-url?
  [url]
  (and
    (not-nil? url)
    (or
      (.startsWith url "http://wikipedia.org")
      (.startsWith url "http://en.wikipedia.org"))
    (not (image? url))
    ))

(defn make-full-url
  [url]
  (if
    (not (nil? url))
    (if (.startsWith url "/")
      (.concat "http://wikipedia.org" url)
      url)
    url))


(defn find-link
  [source depth target & {:keys [parent]}]
  (java.lang.Thread/sleep 2000)
  (println "searching for " target " on " source " from parent " parent " at depth " depth)
  (if (searchable-url? (make-full-url source))
    (let [links (set (get-links (make-full-url source)))]
      (if (link-present? links target)
        source
        (if (<= depth 0)
          nil
          (some #(not-nil? (find-link % (dec depth) target :parent source)) links)
          ))))
  nil)