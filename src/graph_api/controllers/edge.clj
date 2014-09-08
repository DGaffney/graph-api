(ns graph-api.controllers.edge
  (:require [clojure.string :as string]
    [graph-api.mongodb :as m]
    [monger.core :as mg]
    [monger.collection :as mc]
    [environ.core :refer [env]])
  (:import 
    [org.bson.types ObjectId]))


(defn create [params]
  (m/insert-doc m/edges (conj params {:graph-id (ObjectId. (:graph-id params))}))
  (m/get-doc m/edges (conj params {:graph-id (ObjectId. (:graph-id params))})))

(defn edit [params]
  (m/update-doc m/edges {"_id" (:id params)} params))

(defn destroy [params]
  (m/remove-doc m/edges params))

(defn index [params]
  (m/get-docs m/edges params))
