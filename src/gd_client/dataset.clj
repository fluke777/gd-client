(ns gd-client.dataset
  (:require [gd-client.core :as gd]
            [gd-client.obj :as obj]
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
  [dataset]
  (let [all-columns (doall (->> (gd/get (obj/uri dataset))
                            (obj/using :attribute)
                            (pmap #(obj/using :column %))))
        attr-columns (filter (fn [coll]
              (every? #(->> % (obj/title) (re-find #"col\.f_")) coll)) all-columns)]
    (when (seq attr-columns)
      (first(obj/used-by :attribute (gd/get(obj/uri (first (first attr-columns)))))))))