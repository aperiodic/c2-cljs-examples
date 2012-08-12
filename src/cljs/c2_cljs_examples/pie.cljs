(ns c2-cljs-examples.pie
  (:use-macros [c2.util :only [bind!]])
  (:require [c2.core :as c2]
            [c2.event :as event]
            [c2.svg :as svg]
            [clojure.string :as str])
  (:use [c2.layout.partition :only [partition]
                             :rename {partition partition-data}]
        [c2.maths :only [sin cos Tau]]))

(defn- rad->deg [rad] (-> rad (/ Tau), (* 360)))

(def stars {:sun {:mass 1.9891e30}})

(def planets {:mercury {:mass 3.301e23}
              :venus   {:mass 4.867e24}
              :earth   {:mass 5.972e24}
              :mars    {:mass 6.419e23}
              :jupiter {:mass 1.889e27}
              :saturn  {:mass 5.685e26}
              :uranus  {:mass 8.682e25}
              :neptune {:mass 1.024e26}})

(def minor-bodies {:eris {:mass 1.67e22}
                   :pluto {:mass 1.471e22}
                   :haumea {:mass 4.006e21}
                   :makemake {:mass 3e21}
                   :sedna {:mass 1e21}
                   :ceres {:mass 9.3e20}
                   :vesta {:mass 2.6e20}})

(def solar-system (merge stars planets minor-bodies))

(def body-colors (str ".jupiter, .mars, .sedna {fill: #1b9e77}"
                      ".saturn, .mercury, .ceres {fill: #d95f02}"
                      ".neptune, .eris {fill: #7570b3}"
                      ".uranus, .pluto {fill: #e7298a}"
                      ".earth, .haumea, .vesta {fill: #66a61e}"
                      ".venus, .makemake, .sun {fill: #e6ab02}"))

(defn mass-charts []
  (let [format-data-map (fn [m]
                          (for [[b attrs] (sort-by (comp :mass second) m)]
                                  {:name (name b), :mass (:mass attrs)}))
        charts [{:name "Planets"
                 :children (format-data-map planets)}
                {:name "Dwarf Planets & Asteroids"
                 :children (format-data-map minor-bodies)}
                {:name "Solar System Objects at Least as Massive as Vesta"
                 :children (format-data-map solar-system)}]
        make-slices (fn [dm]
                      (filter #(= 1 (get-in % [:partition :depth]))
                              (partition dm :value :mass, :size [Tau 1])))
        radius 300
        margin 30
        quarter-turn-ccw (svg/rotate (-> Tau (/ 4), (* -1), rad->deg))
        ->pie-slice (fn [{name :name, mass :mass, {:keys [x dx]} :partition}]
                      [:g.slice
                       [:path {:class name
                               :d (svg/arc :outer-radius radius
                                           :start-angle x
                                           :end-angle  (+ x dx))}]])]
    (bind!
      "#content"
      [:div#content
       ;; aw yeah, inlining stylesheets
       [:style {:type "text/css"}
        (str "body {background-color: #222222}"
             "path {fill: #222222; stroke: #dbdbdb; stroke-width: 2px}"
             body-colors)]
       (-> (for [[i id data] (map vector (range)
                                         ["planets" "minor" "all"]
                                         charts)]
             (let [x-offset (+ radius margin)
                   y-offset (+ radius margin (* 2 i radius) (* 2 i margin))]
               [:g.chart
                {:id id, :transform (str (svg/translate [x-offset y-offset])
                                         quarter-turn-ccw)}
                (c2/unify (make-slices data) ->pie-slice)
                [:g.legend
                 {:transform (svg/translate [(+ (* radius 2) (* margin 2)) 0])}
                 (c2/unify (make-slices data)
                           )]]))
         (conj {:width 960, :height 1980})
         (conj :svg#main)
         vec)])))

(event/on-load mass-charts)
