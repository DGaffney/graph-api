(ns graph-api.controllers.user
  (:require [clojure.string :as string]
    [graph-api.mongodb :as m]
    [monger.core :as mg]
    [monger.collection :as mc]
    [environ.core :refer [env]]))

(defn create [params]
  (m/insert-doc m/users params)
  (m/get-doc m/users params))

(defn edit [params]
  (m/update-doc m/users {"_id" (:id params)} params))

(defn update-password [params]
  (prn params)
  (m/update-doc (m/get-doc m/users {"name" (:name params)}) (conj (m/get-doc m/users {"name" (:name params)}) {"md5" (:md5 params)}))
  (m/get-doc m/users params))

(defn destroy [params]
  (m/remove-doc m/users params))

(defn index [params]
  (m/get-docs m/users params))
