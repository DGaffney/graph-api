(ns graph-api.mongodb
  (:require
    [digest :as d]
    [cheshire.core :as json]
    [monger.core :as mg]
    [monger.collection :as mc]
    [clj-time.core :as jt]
    [monger.operators :refer :all]
    [clojure.java.shell :as shell]
    clojure.core.incubator
    monger.json)
  (:use clj-time.coerce)
  (:import
    [com.mongodb MongoOptions ServerAddress]
    [org.bson.types ObjectId]))

(defn doc-count
  [collection conditions]
  (mc/count collection conditions))

(defn new-document
  [doc]
  (conj doc {"updated_at" (java.util.Date.)}))

(defn insert-doc
  [collection doc]
  (mc/update collection doc (conj {"updated_at" (java.util.Date.)} doc) :upsert true))

(defn remove-doc
  [collection lookup]
  (mc/remove collection lookup)
  lookup)

(defn return-map-from-lookup
  [collection lookup]
  (mc/find-map-by-id collection (ObjectId. (.toString (get (mc/find-one collection lookup) "_id")))))

(defn get-doc
  [collection lookup]
  (prn "Get Doc")
  (conj (return-map-from-lookup collection lookup) {:_id (.toString (:_id (return-map-from-lookup collection lookup)))}))

(defn update-doc
  [collection lookup updates]
  (mc/update collection lookup updates))

(defn get-docs
  [collection lookup]
   (mc/find-maps collection lookup))

(defn push
  [collection lookup pusher]
  (prn pusher)
  (mc/update collection lookup {$addToSet pusher}))

(defn pull
  [collection lookup puller]
  (mc/update collection lookup {$pull puller}))

(defn error
  [message]
  (spit "./logs/error.log" (str "\n" (java.util.Date.) "\n" message "\n\n\n") :append true))

(def branch-name
  (-> (shell/sh "git" "rev-parse" "--abbrev-ref" "HEAD") :out clojure.string/trim-newline))

(def database-name
  (if (= branch-name "production")
    "graph-api-production"
    "graph-api-develop"))

(def nodes "nodes")

(def edges "edges")

(def graphs "graphs")

(def users "users")

(defn connect!
  [database-name]
  (let [^MongoOptions opts (mg/mongo-options :auto-connect-retry true :threads-allowed-to-block-for-connection-multiplier 1500)
        ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)]
    (mg/connect! sa opts))
    (mg/set-db! (mg/get-db database-name)))
