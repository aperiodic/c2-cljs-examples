(ns c2-cljs-examples.routes
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response])
  (:use [compojure core]
        [hiccup.middleware :only [wrap-base-url]]
        [c2-cljs-examples.views :only [boxplots bullet index space-pie]]))

(defroutes c2-cljs-examples-routes
  (GET "/" [] (index))
  (GET "/boxplots" [] (boxplots))
  (GET "/bullet" [] (bullet))
  (GET "/space-pie" [] (space-pie))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler
  (-> (handler/site c2-cljs-examples-routes)
    (wrap-base-url)))
