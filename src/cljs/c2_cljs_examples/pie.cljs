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

(defn- title-case
  [s]
  (-> s
    (str/replace \- \space)
    (str/split #" ")
    (->>
      (map str/capitalize)
      (str/join " "))))

(defn- css-safe
  [s]
  (.replace s "'" ""))

(def stars {:sun 1.9884e30})

(def planets {:mercury 3.301e23
              :venus   4.867e24
              :earth   5.972e24
              :mars    6.419e23
              :jupiter 1.899e27
              :saturn  5.685e26
              :uranus  8.682e25
              :neptune 1.024e26})

(def other-bodies {:ganymede 1.482e23
                   :titan 1.345e23
                   :callisto 1.076e23
                   :io 8.93e22
                   :earth's-moon 7.35e22
                   :europa 4.8e22
                   :triton 2.15e22
                   :eris 1.67e22
                   :pluto 1.471e22
                   :haumea 4.006e21
                   :titania 3.526e21
                   :oberon 3.014e21
                   :makemake 3e21
                   :rhea 2.3166e21
                   :iapetus 1.937e21
                   :quaoar 1.6e21})

(def besides-sun (merge planets other-bodies))

(def body-colors (str ".earth, .mercury, .europa, .oberon {fill: #1b9e77}"
                      ".saturn, .mars, .titan, .eris, .rhea {fill: #d95f02}"
                      ".neptune, .ganymede,"
                        ".triton, .makemake {fill: #7570b3}"
                      ".uranus, .callisto, .pluto, .iapetus {fill: #e7298a}"
                      ".jupiter, .earths-moon, .titania {fill: #66a61e}"
                      ".sun, .venus, .io, .haumea, .quaoar {fill: #e6ab02}"
                      ".all-other-bodies, .everything-else {fill: #dbdbdb}"))

(defn mass-charts []
  (let [radius 300
        margin (/ radius 10)
        half-pie (+ radius margin)
        pie-width (* 2 half-pie)
        title-size 48
        group-height (+ pie-width title-size margin)
        format-data (fn [m]
                      (for [[b mass] (sort-by second m)]
                        {:name (name b), :mass mass}))
        charts [{:name "The Solar System"
                 :children (format-data
                             {:sun (:sun stars)
                              :everything-else (apply + (vals besides-sun))})}
                {:name "Excluding the Sun"
                 :children (format-data
                             (-> planets
                               (dissoc :mars)
                               (dissoc :mercury)
                               (assoc :all-other-bodies
                                      (apply +
                                             (:mars planets)
                                             (:mercury planets)
                                             (vals other-bodies)))))}
                {:name "Also Excluding Planets"
                 :children (format-data other-bodies)}]
        make-slices (fn [dm]
                      (filter #(= 1 (get-in % [:partition :depth]))
                              (partition dm :value :mass, :size [Tau 1])))
        quarter-turn-ccw (svg/rotate (-> Tau (/ 4), (* -1), rad->deg))
        ->pie-slice (fn [{name :name, mass :mass, {:keys [x dx]} :partition}]
                      [:g.slice
                       [:path {:class (css-safe name)
                               :d (svg/arc :outer-radius radius
                                           :start-angle (* -1 x)
                                           :end-angle  (* -1 (+ x dx)))}]])
        ->legend (fn [i-max [i {name :name}]]
                   (let [text-size 20
                         [i i-max] (map inc [i i-max])
                         i->y (fn [i] (+ (* i text-size (/ 3 2)) text-size))
                         y-off (- (i->y i-max) (i->y i))]
                     [:g.legend-entry {:transform (svg/translate [0 y-off])}
                      [:rect {:class (css-safe name)
                              :attrs {:shape-rendering "crispEdges"}
                              :width text-size, :height text-size}]
                      [:text {:x (* text-size (/ 4 3))
                              :y (* text-size (/ 85 100))
                              :font-size text-size}
                       (title-case name)]]))]
    (bind!
      "#content"
      [:div#content
       ;; aw yeah, inlining stylesheets
       [:style {:type "text/css"}
        (str "body {background-color: #222222}"
             "path {fill: #222222; stroke: #dbdbdb; stroke-width: 2px}"
             "text {fill: white}"
             body-colors)]
       (vec
         (concat
           [:svg#main {:width 960, :height (* group-height 3)}]
           (for [[i id data] (map vector (range)
                                         ["all" "planets" "solid"]
                                         charts)]
             [:g.figure
              {:transform (svg/translate [0 (* i group-height)])}
              [:text.title {:x margin, :y (+ title-size (/ margin 2))
                            :font-size title-size}
               (:name data)]
              [:g {:transform (svg/translate [0 (+ margin title-size)])}
               [:g.chart
               {:id id,
                :transform (str (svg/translate [half-pie half-pie])
                                quarter-turn-ccw)}
               (c2/unify (make-slices data) ->pie-slice)]
              [:g.legend
               {:transform (svg/translate [pie-width 0])}
               (c2/unify (map-indexed vector (make-slices data))
                         (partial ->legend (count (make-slices data))))]]])))])))

(event/on-load mass-charts)
