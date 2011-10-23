(ns wiki.core
  (:use
    ring.adapter.jetty
    net.cgrand.moustache
    [ring.middleware params stacktrace reload]
    ring.util.response
    hiccup.core))

(defn page [title content]
  (html
    [:html
     [:head
      [:title title]]
     [:body
      [:h1 title]
      [:div content]]]))

(defn show [_ title]
  (response
    (page title "hello world")))

(def wiki
  (app
    wrap-params
    wrap-stacktrace
    (wrap-reload ['wiki.core])
    [[title #"(?:[A-Z][a-z]+){2,}"]] (delegate show title)
    [&] (constantly (redirect "/MainPage"))))

(defn -main []
  (run-jetty #'wiki {:port 8080}))
