(ns gd-client.query
  (:require [gd-client.core :as gd]
            [clojure.core.strint :refer [<<]]))

(defn get
  [what]
  (gd/get (<< "/gdc/md/~{gd/*pid*}/query/~{(name what)}" )))