(ns degrees-of-wiki-separation.core-test
  (:require [clojure.test :refer :all ]
            [degrees-of-wiki-separation.core :refer :all ]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 0))))

(comment
  (deftest returns-link-if-found-test
    (testing
      (is (= (find-link "http://en.wikipedia.org/wiki/Albert_Einstein", "http://en.wikipedia.org/wiki/Quantum_mechanics", 2) true))))
  )

(deftest find-link-test
  (testing "")
  (let [result (find-link "http://en.wikipedia.org/wiki/John_McCarthy_(computer_scientist)" 3 "/wiki/LEPS")]
    (println "result:" result)
    (is
      (= "/wiki/Molecular" result))))
