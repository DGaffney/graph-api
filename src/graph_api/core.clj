(ns graph-api.core
  (:gen-class)
  (:use compojure.core
        clojure.stacktrace)
  (:require [ring.adapter.jetty :as jetty]
    [clojure.string :as string]
    [compojure.handler :as handler]
    [monger.collection :as mc]
    [environ.core :refer [env]]
    [digest :as d]
    [graph-api.mongodb :as m]
    [graph-api.controllers.user :as controllers.user]
    [graph-api.controllers.graph :as controllers.graph]
    [graph-api.controllers.edge :as controllers.edge]
    [graph-api.controllers.node :as controllers.node]
    [noir.response :as response]
    [cheshire.core :refer [generate-string]]
    [cheshire.generate :refer [add-encoder encode-str]]))

(add-encoder org.bson.types.ObjectId encode-str)
(use '[ring.middleware.json :only [wrap-json-response]]
     '[ring.util.response :only [response]])


(defn respond   [response]
  (response/json response))

(defroutes app
  (ANY "/v1/user/index.json" {params :params} (respond (controllers.user/index params)))
  (ANY "/v1/user/create.json" {params :params} (respond (controllers.user/create params)))
  (ANY "/v1/user/edit.json" {params :params} (respond (controllers.user/edit params)))
  (ANY "/v1/user/update_password.json" {params :params} (respond (controllers.user/update-password params)))
  (ANY "/v1/user/destroy.json" {params :params} (respond (controllers.user/destroy params)))
  (ANY "/v1/graph/index.json" {params :params} (respond (controllers.graph/index params)))
  (ANY "/v1/graph/create.json" {params :params} (respond (controllers.graph/create params)))
  ;(ANY "/v1/graph/add_node.json" {params :params} (respond (controllers.graph/add-node params)))
  (ANY "/v1/graph/add_node.json" request (respond (controllers.graph/add-node (:json-params request))))
  (ANY "/v1/graph/drop_node.json" {params :params} (respond (controllers.graph/drop-node params)))
  (ANY "/v1/graph/edit.json" {params :params} (respond (controllers.graph/edit params)))
  (ANY "/v1/graph/destroy.json" {params :params} (respond (controllers.graph/destroy params)))
  (ANY "/v1/graph/download.gexf" {params :params} (controllers.graph/download params))
  (ANY "/v1/edge/index.json" {params :params} (respond (controllers.edge/index params)))
  (ANY "/v1/edge/create.json" {params :params} (respond (controllers.edge/create params)))
  (ANY "/v1/edge/edit.json" {params :params} (respond (controllers.edge/edit params)))
  (ANY "/v1/edge/destroy.json" {params :params} (respond (controllers.edge/destroy params)))
  (ANY "/v1/edge/count.json", {params :params} (respond (controllers.edge/count params)))
  (ANY "/v1/node/index.json" {params :params} (respond (controllers.node/index params)))
  (ANY "/v1/node/create.json" request (respond (controllers.node/create (:json-params request))))
  ;(ANY "/v1/node/create.json" {params :params} (respond (controllers.node/create params)))
  (ANY "/v1/node/edit.json" {params :params} (respond (controllers.node/edit params)))
  (ANY "/ping" request)
  (ANY "/v1/node/destroy.json" {params :params} (respond (controllers.node/destroy params))))

(defn start
  []
  (m/connect! m/database-name)
  (println "Started")
  (jetty/run-jetty (-> app handler/site ring.middleware.json/wrap-json-params) {:port 8080}))

(defn -main
  [& args]
  (start))
