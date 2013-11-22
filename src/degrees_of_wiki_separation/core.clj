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
  ;  (println "is " target " present")
  (some #(= % target) urls))

(def not-nil?
  (complement nil?))

(defn searchable-url?
  [url]
  (def is-valid-url (and
                      (not-nil? url)
                      (or
                        (.startsWith url "http://wikipedia.org")
                        (.startsWith url "http://en.wikipedia.org"))
                      (not (image? url))
                      ))
  (println "is " url " valid " is-valid-url)
  is-valid-url)

(defn make-full-url
  [url]
  (if
    (not (nil? url))
    (if (.startsWith url "/")
      (.concat "http://wikipedia.org" url)
      url)
    url))


(defn find-link
  [url depth target &{:keys [parent]}]
  (java.lang.Thread/sleep 5000)
  (println "searching for " target " on " url " from parent " parent " at depth " depth)
  (if (searchable-url? (make-full-url url))
    (let [links (set (get-links (make-full-url url)))]
      (if
        (and
          ;(> 0 (count links))
          (link-present? links target))
        ((println "found " target " at " url)
          url)
        (if (<= depth 0)
          (println "could not find " target "on " url)
          ((println "searching deeper. Is links == nil?" (nil? links))
            (doseq [link links]
              ;                (is-valid link)
              (find-link link (dec depth) target :parent url)
              ;#(find-link % target (dec depth))
              )))))
    nil))