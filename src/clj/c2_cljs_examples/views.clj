(ns c2-cljs-examples.views
  (:use [hiccup.element :only [javascript-tag]]
        [hiccup.page :only [html5 include-js]]))

; When using {:optimizations :whitespace}, the Google Closure compiler combines
; its JavaScript inputs into a single file, which obviates the need for a
; "deps.js" file for dependencies. However, true to ":whitespace", the compiler
; does not remove the code that tries to fetch the (nonexistent) "deps.js" file.
; Thus, we have to turn off that feature here by setting CLOSURE_NO_DEPS.
;
; Note that this would not be necessary for :simple or :advanced optimizations.
(defn- include-clojurescript
  [path]
  (list (javascript-tag "var CLOSURE_NO_DEPS = true;")
        (include-js path)))

(defn index []
  (html5
    [:head
      [:title "C2 Clojurescript Examples"]]
    [:body
      [:div#content
       [:a {:href "boxplots"} "Boxplots"]
       [:a {:href "space-pie"} "Pie Charts (Advanced)"]
       [:a {:href "bullet"} "Bullet"]]]))

(defn boxplots []
  (html5
    [:head
     [:title "Boxes & Whiskers"]
     (include-clojurescript "/js/main.js")]
    [:body
     [:div#content]
     (javascript-tag "c2_cljs_examples.boxplots.boxplots();")]))

(defn bullet []
  (html5
    [:head
     [:title "Bullet Charts (Sleek & Speedy like the Trains of Tomorrow)"]
     (include-clojurescript "/js/main.js")]
    [:body
     [:div#content]
     (javascript-tag "c2_cljs_examples.bullet.bullet();")]))

(defn space-pie []
  (html5
    [:head
     [:title "Pie Charts regarding the Congealed Remains of an Ancient Disk of Dust"]
     (include-clojurescript "/js/main.js")]
    [:body
     [:div#content]
     (javascript-tag "c2_cljs_examples.space_pie.space_pie();")]))
