(ns c2-cljs-examples.boxplots
  (:use-macros [c2.util :only [bind!]])
  (:require [c2.core :as c2]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg])
  (:use [c2.maths :only [extent]]))

(defn crisp
  "This makes things look nice and sharp using a magic SVG attribute."
  [m]
  (assoc-in m [:attrs :shape-rendering] "crispEdges"))

(defn boxplots []
  (let [height 500
        width 960
        group-width 30
        box-width 17
        half-box-width (/ box-width 2)
        bins [:q10 :q25 :median :q75 :q90]
        data (repeatedly
               (quot width group-width)
               #(into {} (map vector
                              bins
                              (sort (take (count bins) (repeatedly rand))))))
        scale (scale/linear :domain (extent (flatten (map vals data)))
                            ;; range inverted because origin is at top of frame
                            :range [(dec height) 0])
        box-width-line (fn [y] [:line (crisp {:x1 0, :x2 box-width
                                              :y1 y, :y2 y})])]
    (bind!
      "#vis"
      [:div#vis

       ;; inline CSS
       [:style {:type "text/css"}
        (str "body { background-color: #222222 }"
             ".box { fill: #222222; stroke: #cfcfcf }"
             "line { stroke: #cfcfcf }"
             "line.range { stroke-dasharray: 5,5 }")]

       ;; the containing SVG element
       [:svg#main {:style {:display "block", :margin "auto"
                           :width width, :height height}}

        (c2/unify
          (map-indexed vector data)
          (fn [[i {:keys [q10 q25 median q75 q90]}]]

            ;; a group for each box
            [:g.boxplot {:transform (svg/translate [(inc (* i group-width)) 1])}

             ;; dashed line from the 10th percentile to the 90th
             ;; this goes under the box, but the box has a fill, so NBD
             [:line.range (crisp {:x1 half-box-width, :x2 half-box-width
                                  :y1 (scale q90), :y2 (scale q10)})]

             ;; solid lines at the 10th & 90th percentiles
             (box-width-line (scale q10))
             (box-width-line (scale q90))

             ;; box from 25th to 75th percentile
             [:rect.box (crisp {:x 0, :y (scale q75)
                                :width box-width,
                                :height (- (scale q25) (scale q75))})]

             ;; line across the box at the median
             (box-width-line (scale median))]))]])))
