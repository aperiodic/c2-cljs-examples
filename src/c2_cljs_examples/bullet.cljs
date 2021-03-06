(ns c2-cljs-examples.bullet
  (:use-macros [c2.util :only [bind!]])
  (:require [c2.core :as c2]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg]))

(defn half [x] (/ x 2))

(defn crisp
  [shape-attrs]
  (assoc shape-attrs :shape-rendering "crispEdges"))

(defn bullet []
  (let [bar-width 780
        group-height 55
        range-height 40
        group-gutter (half (- group-height range-height))
        measurement-height 15
        marker-height 20
        marker-width 2
        label-margin 150
        title-size 18
        subtitle-size 12
        text-gutter 8
        style (str "body { background-color: #222222 }"
                   ".bullet { font: " subtitle-size "px \"Helvetica Neue\",
                                                    Helvetica, sans-serif }"
                   ".bullet .label { fill: white; text-anchor: end }"
                   ".bullet .marker { stroke: black; stroke-width: 2px }"
                   ".bullet .tick line { stroke: #666; stroke-width: .5px }"
                   ".bullet .range.s0 { fill: #eee }"
                   ".bullet .range.s1 { fill: #cacaca }"
                   ".bullet .range.s2 { fill: #a3a3a3 }"
                   ".bullet .measurement.s0 { fill: #9fb6dc }"
                   ".bullet .measurement.s1 { fill: steelblue }"
                   ".bullet .title { font-size: " title-size "px; font-weight: bold }"
                   ".bullet .subtitle { fill: #999 }")
        data [{:metric "Revenue", :units "USD in thousands"
               :ranges [150 225 300], :measurements [220 270], :markers [250]}
              {:metric "Profit", :units "%"
               :ranges [20 25 30], :measurements [21 23], :markers [26]}
              {:metric "Order Size", :units "USD average"
               :ranges [350 500 600], :measurements [100 320], :markers [550]}
              {:metric "New Customers", :units "count"
               :ranges [1400 2000 2500],
               :measurements [1000 1650], :markers [2100]}
              {:metric "Satisfaction", :units "out of 5"
               :ranges [3.5 4.25 5], :measurements [3.2 4.7], :markers [4.4]}]]
    (bind!
      "#vis"
      [:div#vis [:style {:type "text/css"} style]

       ;; the containing SVG element
       [:svg#main {:style {:display "block" :margin "auto"
                           :width 960, :height (* (count data) group-height)}}

        (c2/unify
          (map-indexed vector data)
          (fn [[i {:keys [metric units ranges measurements markers]}]]
            (let [rightmost (apply max (concat ranges measurements markers))
                  sc (scale/linear :domain [0 rightmost]
                                   :range [0 bar-width])]

              ;; wrapper group for each chart & label
              [:g.bullet
               {:transform (svg/translate [0 (+ (* i group-height)
                                                group-gutter)])}

               ;; the text label
               [:g.label
                {:transform (svg/translate [(- label-margin text-gutter)
                                            title-size])}
                [:text.title metric]
                [:text.subtitle {:dy "1.2em"} units]]

               ;; the rects making up the chart itself
               [:g.chart {:transform (svg/translate [label-margin 0])}

                ;; the grey rects denoting range bins
                (for [[j r] (map-indexed vector (sort > ranges))]
                  [:rect (crisp {:class (str "range s" j)
                                 :width (sc r), :height range-height})])

                ;; the thin blue rects denoting measurements
                (for [[j m] (map-indexed vector (sort > measurements))]
                  [:rect (crisp
                           {:class (str "measurement s" j)
                            :width (sc m), :height measurement-height
                            :y (half (- range-height measurement-height))})])

                ;; the little black goal markers
                (for [[j m] (map-indexed vector (sort > markers))]
                  [:rect.marker
                   (crisp
                     {:x (sc m), :y (half (- range-height marker-height))
                      :width marker-width, :height marker-height})])]])))]])))
