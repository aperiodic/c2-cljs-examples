(ns c2-cljs-examples.bullet
  (:use-macros [c2.util :only [bind!]])
  (:require [c2.core :as c2]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg])
  (:use [c2.util :only [half]]))

(defn bullet []
  (let [data [{:title "Revenue", :subtitle "USD in thousands"
               :ranges [150 225 300], :measures [220 270], :markers [250]}
              {:title "Profit", :subtitle "%"
               :ranges [20 25 30], :measures [21 23], :markers [26]}
              {:title "Order Size", :subtitle "USD average"
               :ranges [350 500 600], :measures [100 320], :markers [550]}
              {:title "New Customers", :subtitle "count"
               :ranges [1400 2000 2500]. :measures [1000 1650], :markers [2100]}
              {:title "Satisfaction", :subtitle "out of 5"
               :ranges [3.5 4.25 5], :measures [3.2 4.7], :markers [4.4]}]
        bar-width 800
        range-height 25
        measure-height 9
        marker-height 15
        label-margin 120]
    (bind!
      "#content"
      [:div#content
       [:style {:type "text/css"}
        (str ".bullet { font: 10px \"Helvetica Neue\" Helvetica sans-serif }"
             ".bullet .labels { fill: white; text-anchor: end }"
             ".bullet .marker { stroke: black; stroke-width: 2px }"
             ".bullet .tick line { stroke: #666; stroke-width: .5px }"
             ".bullet .range.s0 { fill: #eee }"
             ".bullet .range.s1 { fill: #ddd }"
             ".bullet .range.s2 { fill: #ccc }"
             ".bullet .measure.s0 { fill: lightsteelblue }"
             ".bullet .measure.s1 { fill: steelblue }"
             ".bullet .title { font-size: 14px; font-weight: bold }"
             ".bullet .subtitle { fill: #999 ")]
       [:svg#main {:style {:display "block"
                           :margin "auto"
                           :width 960}}]

       ]
      )))
