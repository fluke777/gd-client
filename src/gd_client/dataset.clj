(ns gd-client.dataset
  (:require [gd-client.core :as gd]
            [clojure.core.strint :refer [<<]]))

(defn get
  [what]
  (gd/get (<< "/gdc/md/~{gd/*pid*}/query/~{(name what)}" )))

(defn using
  [])

(defn used-by
  [])

(defn lines
  [])

(defn get-main-attribute
  [])