(ns graph-api.controllers.graph
  (:require [clojure.string :as string]
    [graph-api.mongodb :as m]
    [monger.core :as mg]
    [monger.collection :as mc]
    [graph-api.gexf :as gexf]
    [clojure.java.io :as io]
    [environ.core :refer [env]])
  (:import
    [org.bson.types ObjectId]))

(defn create [params]
  (m/insert-doc m/graphs (conj {:node_ids []} params))
  (m/get-doc m/graphs params))

(defn to-object-id [object-id]
  (ObjectId. object-id))

(defn push-node-id [object-id graph-id]
  (m/push m/graphs {"_id" (ObjectId. graph-id)} {"node_ids" (ObjectId. object-id)}))

(defn add-node [params]
  (mapv push-node-id (get params "node_ids") (into [] (repeat (count (get params "node_ids")) (get params "graph_id"))))
  (m/get-doc m/graphs {"_id" (ObjectId. (get params "graph_id"))}))

(defn drop-node [params]
  (m/pull m/graphs {"_id" (ObjectId. (get params "graph_id"))} {"node_ids" (into [] (map to-object-id (get params "node_ids")))})
  (m/get-doc m/graphs {"_id" (ObjectId. (get params "graph_id"))}))

(defn edit [params]
  (m/update-doc m/graphs {"_id" (:id params)} params))

(defn destroy [params]
  (if (contains? params :_id) (m/remove-doc m/graphs (conj params {:_id (ObjectId. (:_id params))})) (m/remove-doc m/graphs params)))

(defn gexf-file [graph-id]
  (clojure.string/join (with-open [rdr (clojure.java.io/reader (str graph-id ".gexf"))]  (reduce conj [] (line-seq rdr)))))

(defn download [params]
  (gexf/write-file (:graph_id params))
  (let [file (gexf-file (:graph_id params))]
  (clojure.java.io/delete-file (str (:graph_id params) ".gexf"))
  file))

(defn index [params]
  (if (contains? params :_id) (m/get-docs m/graphs (conj params {:_id (ObjectId. (:_id params))})) (m/get-docs m/graphs params)))
