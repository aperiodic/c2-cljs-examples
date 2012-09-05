(ns c2-cljs-examples.space-pie
  (:use-macros [c2.util :only [bind!]])
  (:require [c2.core :as c2]
            [c2.event :as event]
            [c2.svg :as svg]
            [clojure.string :as str])
  (:use [c2.layout.partition :only [partition]]
        [c2.maths :only [sin cos Tau]]))

;;
;; A General Utility Not Yet in C2
;;

(defn- deg [rad] (-> rad (/ Tau), (* 360)))

;;
;; Utilities Specific to our Data
;;

(defn- title-case
  "Dumb title case, which capitalizes every whitespace-separated word. Good
  enough for our data!"
  [s]
  (-> s
    (str/replace \- \space)
    (str/split #" ")
    (->>
      (map str/capitalize)
      (str/join " "))))

(defn- keyword->css-class
  "This converts the keywords of the mass maps we def below to css class names,
  and its behavior is undefined for any other inputs. It relies on the fact that
  the only non-css-safe thing that appears in those keywords is the apostrophe
  in the keyword :earth's-moon."
  [s]
  (.replace s "'" ""))

(defn color->css
  [[entity color]]
  (str "." (keyword->css-class (name entity)) " {fill: " (name color) "}"))

(defn colors->css
  [colors]
  (apply str (map color->css colors)))

;;
;; Our Color Palette Function
;;

(def the-palette
  "Derived from Cynthia Brewer's color brewer; all blemishes my own.
  http://colorbrewer2.org"
  {:blue {:base "#174d69", :light "#2a86d2"}
   :green {:base "#66a61e", :dark "#376b00"}
   :grey {:dark "#606060", :light "#999999"}
   :orange {:base "#d95f02", :dark "#923700" }
   :pink {:base "#e7298a", :dark "#97004f"}
   :purple {:base "#7570b3", :dark "#36298d", :darkest "#2f0058"}
   :teal {:base "#1b9e77"}
   :white {:base "#dbdbdb"}
   :yellow {:base "#e6ab02", :dark "#a47400"}})

(defn palette
  "Takes a keyword specifying a color in the palette map defined above, with an
  optional preceding modifier that must be defined for that color."
  ([color]
   (palette :base color))
  ([modifier color]
   {:pre [(contains? the-palette color)
          (contains? (color the-palette) modifier)]}
   (get-in the-palette [color modifier])))

;;
;; Masses of Various Bodies in the Solar System
;;

(def stars {:the-sun 1.9884e30})

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

;;
;; Utilities for Defining Charts from the Mass Data
;;

(defn sum-mass [mass-map] (apply + (vals mass-map)))

(defn format-data
  [m]
  (for [[b mass] (sort-by second m)]
    {:name (name b), :mass mass}))

;;
;; Charts from the Mass Data
;;

(def summary
  {:name "The Solar System by Mass"
   :children (format-data
               {:the-sun (:the-sun stars)
                :everything-else (sum-mass (merge planets other-bodies))})
   :colors {:the-sun (palette :yellow)
            :everything-else (palette :white)}})

(def no-sun
  {:name "Excluding the Sun"
   :children (format-data
               (-> planets
                 (dissoc :mars :mercury) ; these two are too small to see
                 (assoc :all-lesser-bodies
                        (sum-mass (merge (select-keys planets [:mars :mercury])
                                         other-bodies)))))
   :colors {:jupiter (palette :green)
            :saturn (palette :orange)
            :neptune (palette :purple)
            :uranus (palette :teal)
            :earth (palette :yellow)
            :venus (palette :pink)
            :all-lesser-bodies (palette :white)}})

(def no-planets
  {:name "Also Excluding Planets"
   :children (format-data other-bodies)
   :colors {:ganymede (palette :purple)
            :titan (palette :orange)
            :callisto (palette :pink)
            :io (palette :green)
            :earth's-moon (palette :yellow)
            :europa (palette :teal)
            :triton (palette :dark :purple)
            :eris (palette :dark :orange)
            :pluto (palette :blue)
            :haumea (palette :dark :pink)
            :titania (palette :dark :green)
            :oberon (palette :dark :yellow)
            :makemake (palette :light :grey)
            :rhea (palette :darkest :purple)
            :iapetus (palette :light :blue)
            :quaoar (palette :dark :grey)}})

(def charts [summary no-sun no-planets])

;;
;; Define the CSS
;;

(def style (str "body { background-color: #222222 }"
                "path { fill: #222222; stroke: #dbdbdb; stroke-width: 2px }"
                "rect { stroke: #dbdbdb; stroke-width: 2px }"
                "text { fill: white }"
                (->> charts
                  (map :colors)
                  (map colors->css)
                  (apply str))))

;;
;; Turn Those Data into Pictures!
;;

(defn space-pie []
  (let [width 840
        radius 300
        margin (/ radius 10)
        half-pie (+ radius margin)
        pie-width (* 2 half-pie)
        title-size 48
        group-height (+ pie-width title-size (* 2 margin))
        make-slices (fn [dm]
                      (filter #(= 1 (get-in % [:partition :depth]))
                              (partition dm :value :mass, :size [Tau 1])))

        ;; a function for creating a slice of the pie chart
        ->slice (fn [{name :name, mass :mass, {:keys [x dx]} :partition}]
                  [:g.slice
                   [:path {:class (keyword->css-class name)
                           :d (svg/arc :outer-radius radius
                                       :start-angle (* -1 x)
                                       :end-angle  (* -1 (+ x dx)))}]])

        ;; a function for creating an entry in the legend
        ->legend (fn [i-max [i {name :name}]]
                   (let [text-size 19
                         [i i-max] (map inc [i i-max])
                         i->y (fn [i] (+ (* i text-size (/ 3 2)) text-size))
                         y-off (- (i->y i-max) (i->y i))]
                     [:g.legend-entry {:transform (svg/translate [0 y-off])}
                      [:rect {:class (keyword->css-class name)
                              :attrs {:shape-rendering "crispEdges"}
                              :width text-size, :height text-size}]
                      [:text {:x (* text-size 1.33), :y (* text-size 0.85)
                              :font-size text-size}
                       (title-case name)]]))]
    (bind!
      "#vis"
      [:div#vis [:style {:type "text/css"} style]

       ;; the containing SVG element
       [:svg#main {:style {:display "block", :margin "auto"
                           :width width, :height (* group-height
                                                    (count charts))}}

        (for [[i data] (map vector (range) charts)]
          [:g.figure {:transform (svg/translate [0 (* i group-height)])}

           ;; the title of the chart
           [:text.title {:x margin, :y (+ title-size (/ margin 4))
                         :font-size title-size}
            (:name data)]

           ;; the pie chart itself
           [:g {:transform (svg/translate [0 (+ margin title-size)])}
            [:g.chart
             {:transform (str (svg/translate [half-pie half-pie])
                              (svg/rotate (-> Tau (/ 4), (* -1), deg)))}
             (c2/unify (make-slices data) ->slice)]

            ;; the chart's legend
            [:g.legend
             {:transform (svg/translate [pie-width 0])}
             (c2/unify (map-indexed vector (make-slices data))
                       (partial ->legend (count (make-slices data))))]]])]])))
