(ns c2-cljs-examples.core
  (:use-macros [c2.util :only [p pp bind!]])
  (:require [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg])
  (:use [c2.core :only [unify]]
        [c2.maths :only [sin cos Tau extent]]
        [clojure.string :only [join]]))

(defn trivial-bind []
  (bind! "#content"
         [:p "This is meaningful content"]))

(event/on-load trivial-bind)
