(ns c2-cljs-examples.routes
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response])
  (:use [compojure core]
        [hiccup.middleware :only [wrap-base-url]]
        [c2-cljs-examples.views :only [index]]))

(defroutes c2-cljs-examples-routes
  (GET "/" [] (index))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler
  (-> (handler/site c2-cljs-examples-routes)
    (wrap-base-url)))
