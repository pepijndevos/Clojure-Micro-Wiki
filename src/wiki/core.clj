(ns wiki.core
  (:use
    ring.adapter.jetty
    net.cgrand.moustache
    [ring.middleware params stacktrace reload]
    ring.util.response
    hiccup.core
    com.ashafa.clutch))

(def db (get-database "wiki"))

(defn wrap-db [f]
  (fn [req]
    (with-db db
      (f req))))

(defn page [title content]
  (html
    [:html
     [:head
      [:title title]]
     [:body
      [:h1 title]
      [:div content]]]))

(defn show [_ title]
  (let [{:keys [content _rev]} (get-document title)]
    (response
      (page title content))))

(def wiki
  (app
    wrap-params
    wrap-stacktrace
    (wrap-reload ['wiki.core])
    wrap-db
    [[title #"(?:[A-Z][a-z]+){2,}"]] (delegate show title)
    [&] (constantly (redirect "/MainPage"))))

(defn -main []
  (run-jetty #'wiki {:port 8080}))
