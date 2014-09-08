(ns graph-api.gexf
  (require [clj-time.format :as f]
    [graph-api.mongodb :as m]
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all])
  (:import
    [org.bson.types ObjectId]))
(defn xml-builder
  [set plural singular]
  (let [properties ((keyword plural) set)
        other-properties (dissoc set (keyword plural))
        prop (fn [[k v]] (format "%s=\"%s\" " (name k) v))
        open-tag (str "\n          <" plural " " (clojure.string/join
                                      " "
                                      (map prop other-properties))
                      ">")
        contents (apply str (map (fn [attr]
                                     (str "\n            <" singular " "
                                          (apply str (map prop attr))
                                          "></" singular ">"))
                                 properties))
        ]
    (str open-tag contents "\n          </" plural ">")))

(def gexf-header
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
  <gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\">
    <meta lastmodifieddate=\"" (subs (pr-str (java.util.Date.)) 7 17) "\">
      <creator>Gephi 0.8</creator>
      <description></description>
    </meta>"))

(defn graph-header
  [declarations]
  (clojure.string/join ["\n    <graph defaultedgetype=\"" (or (:defaultedgetype declarations) "undirected") "\" timeformat=\"" (or (:timeformat declarations) "double") "\" mode=\"" (or (:mode declarations) "static") "\">"]))

(defn attribute
  [attribute]
  (clojure.string/join ["<attribute id=\"" (:id attribute) "\" title=\"" (:title attribute)"\" type=\"" (:type attribute) "\"></attribute>"]))

(defn size
  [size]
  (clojure.string/join ["\n          <viz:size value=\"" size "\"></viz:size>"]))

(defn position
  [position-data]
  (clojure.string/join ["\n          <viz:position x=\"" (or (:x position-data) 0) "\" y=\"" (or (:y position-data) 0) "\"></viz:position>"]))

(defn color
    [color]
    (clojure.string/join ["\n          <viz:color r=\"" (or (:r color) 0) "\" g=\"" (or (:g color) 0) "\" b=\"" (or (:b color) 0) "\"></viz:color>"]))


(defn attribute-declarations
  [attributes]
  (xml-builder attributes "attributes" "attribute"))

(def nodes-header
  "\n      <nodes>")

(def nodes-footer
  "\n      </nodes>")

(def edges-header
  "\n      <edges>")

(def edges-footer
  "\n      </edges>")

(def graph-footer
  "\n   </graph>")

(def gexf-footer
  "\n</gexf>")

(defn attvalues
  [attvalues]
  (xml-builder attvalues "attvalues" "attvalue"))

(defn node
  [node-data]
  (clojure.string/join ["\n        <node id=\"" (:id node-data) "\" label=\"" (or (:label node-data) (:id node-data)) "\">" (attvalues {:attvalues (or [] (:attributes node-data))}) (size (or (:size node-data) "1")) (position (or (:position node-data) {})) (color (or (:color node-data) {})) "\n        </node>"]))

(defn edge
  [edge-data]
  (clojure.string/join ["\n        <edge id=\"" (or (:id edge-data) (str (:source edge-data) "-" (:target edge-data))) \"" source=\"" (:target edge-data) \"" target=\"" (:target edge-data) \"" weight=\"" (or (:weight edge-data) "1") \"">" (attvalues {:attvalues (:attributes edge-data)}) "\n        </edge>"]))

(defn write-file [graph-id]
  ;{:nodes [{:id "13734562" :label "Devin" :attributes [{:value "2" :for "Friends"} {:value 7 :for "Number"}] :size 100 :position {:x 10 :y 100} :color {:r 100 :g 80 :b 90}}] :edges [{:source 13731562 :target 13731561}]}
  (let [graph (m/get-doc m/graphs {:_id (ObjectId. graph-id)})]
  (with-open [w (clojure.java.io/writer (str graph-id ".gexf") :append true)]
    (.write w gexf-header)
    (.write w (graph-header (or (:declarations graph) {})))
    (.write w nodes-header)
    (doall
      (for [node-data (m/get-docs m/nodes {:_id {$in (:node_ids graph)}})] 
        (.write w (node node-data))
      )
    )
    (.write w nodes-footer)
    (.write w edges-header)
    (doall
      (for [edge-data (m/get-docs m/edges {:graph-id (ObjectId. (:_id graph))})]
        (.write w (edge edge-data))
      )
    )
    (.write w edges-footer)
    (.write w graph-footer)
    (.write w gexf-footer))))

;(def node-data {:id "13734562" :label "Devin" :attributes [{:value "2" :for "Friends"} {:value 7 :for "Number"}] :size 100 :position {:x 10 :y 100} :color {:r 100 :g 80 :b 90}})
;(def edge-data {:id "13731562"})
;(xml-builder {:class "node" :mode "static" :attributes [{:id "blah" :title "Blah" :type "string"} {:id "wee" :title "Wee" :type "string"}]} "attributes" "attribute")
;(node {:id "13734562" :label "Devin" :attributes [{:value "2" :for "Friends"} {:value 7 :for "Number"} :size 100 :position {:x 10 :y 100} :color {:r 100 :g 80 :b 90}]})
