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
      [:title "c2-cljs-examples"]
      (include-clojurescript "/js/main.js")]
    [:body
      [:div#content]]))
