(ns gd-client.obj
  (:require [gd-client.core :as gd]
            [clojure.core.strint :refer [<<]]))


(defn get-primary-key
  [obj]
  (first (keys obj)))


(defmulti uri (fn [obj]
               (first (keys obj))))

(defmethod uri :report
  [obj] (-> obj :report :meta :uri))

(defmethod uri :attribute
  [obj] (-> obj :attribute :meta :uri))

(defmethod uri :column
  [obj] (-> obj :column :meta :uri))

(defmethod uri :dataSet
  [obj] (-> obj :dataSet :meta :uri))


(defmethod uri :projectDashboard
  [obj] (-> obj :projectDashboard :meta :uri))

(defmethod uri :reportDefinition
  [obj] (-> obj :reportDefinition :meta :uri))


(defmethod uri :using
  [obj] (->> obj :using :nodes (map #(:link %))))

(defmethod uri :query
  [obj] (->> obj :query :entries (map #(:link %))))


(defmulti category (fn [obj]
                     (get-primary-key obj)))

(defmethod category :report
  [obj]
  (get-primary-key obj))


(defn get
  [id]
  (gd/get (<< "/gdc/md/~{gd/*pid*}/obj/~{id}" )))

(defn used-by
  [id]
  (gd/get (<< "/gdc/md/~{gd/*pid*}/usedby/~{id}" )))

(defn using
  ([obj]
   (let [id (last (clojure.string/split (uri obj) #"\/"))]
    (gd/get (<< "/gdc/md/~{gd/*pid*}/using/~{id}" ))))
  ([what obj]
   (doall (->> (using obj)
               :using :nodes
               (filter (fn [o] (= (:category o) (name what))))
               (map (fn [o](:link o)))
               (pmap gd/get)
               (filter (fn [obj]
                         (= (get-primary-key obj) what)))))))

(defn used-by
  ([obj]
   (let [id (last (clojure.string/split (uri obj) #"\/"))]
    (gd/get (<< "/gdc/md/~{gd/*pid*}/usedby/~{id}" ))))
  ([what obj]
   (doall (->> (used-by obj)
               :usedby :nodes
               (filter (fn [o] (= (:category o) (name what))))
               (map (fn [o](:link o)))
               (pmap gd/get)
               (filter (fn [obj]
                         (= (get-primary-key obj) what)))))))



(defn title
  [obj]
  (-> (clojure.core/get obj (get-primary-key obj))
      :meta
      :title))

(defn last-modified
  [obj]
  (-> (clojure.core/get obj (get-primary-key obj))
      :meta
      :updated))


(defn latest-report-def
  [report]
  (->> report
       :report
       :content
       :definitions
       last
       gd/get))