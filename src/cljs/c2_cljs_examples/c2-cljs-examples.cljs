(ns c2-cljs-examples.core
  (:use-macros [c2.util :only [p pp bind!]])
  (:require [c2.dom :as dom]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [singult.core :as singult])
  (:use [c2.core :only [unify]]
        [c2.maths :only [sin cos Tau extent]]
        [clojure.string :only [join]]))

(def bins [:min :q5 :q10 :q25 :median :q75 :q90 :q95 :max])

(defn boxplots []
  (let [height 400
        width 960
        group-width 30
        box-width 20
        data (repeatedly
               (quot width group-width)
               #(into {} (map vector
                              bins
                              (sort (take (count bins) (repeatedly rand))))))
        scale (scale/linear :domain (extent (flatten (map vals data)))
                            :range [height 0]) ; inverted since origin is at top
        box-width-line (fn [y] [:line {:x1 0, :x2 box-width
                                       :y1 y, :y2 y}])]
    (singult/merge!
      (.getElementById js/document "content")
      [:div#content
       [:svg#main {:style (str "display: block;"
                               "margin: auto;"
                               "height:" height ";"
                               "width:" width ";")}
        ;; aw yeah, inlining stylesheets
        [:style {:type "text/css"}
         (join "\n" ["body {background-color: #222222;}"
                     ".box {fill: #222222; stroke: white;}"
                     "line {stroke: white;}"
                     "line.range {stroke-dasharray: 5,5;}"
                     ])]
        ;; this works
        [:rect.box {:x 0.5, :y 0.5, :width box-width, :height box-width}]
        ;; i don't know why this doesn't (uncomment to see it blow up)
        #_(unify (map-indexed vector data)
                 (constantly
                   [:rect.box {:x 0.5, :y 0.5
                               :width box-width, :height box-width}]))]])))

(event/on-load boxplots)
