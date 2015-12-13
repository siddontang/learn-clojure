(ns clojure-stuff.core-test
  (:require [clojure.test :refer :all]
            [clojure-stuff.core :refer :all]))

(deftest my-plus-test
  (testing "Test my plus."
    (is (= (my-plus 1 1) 2))))
