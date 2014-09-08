(ns graph-api.controllers.node
  (:require [clojure.string :as string]
    [graph-api.mongodb :as m]
    [monger.core :as mg]
    [monger.collection :as mc]
    [environ.core :refer [env]]))


(defn create [params]
  (prn params)
  (m/insert-doc m/nodes params)
  (m/get-doc m/nodes params))

(defn edit [params]
  (m/update-doc m/nodes {"_id" (:id params)} params))

(defn destroy [params]
  (m/remove-doc m/nodes params))

(defn index [params]
  (m/get-docs m/nodes params))
