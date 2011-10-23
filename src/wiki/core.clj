(ns wiki.core
  (:use
    ring.adapter.jetty
    net.cgrand.moustache
    [ring.middleware params stacktrace reload]
    ring.util.response))

(def wiki
  (app
    wrap-params
    wrap-stacktrace
    (wrap-reload ['wiki.core])
    [[title #"(?:[A-Z][a-z]+){2,}"]] "hello tester"
    [&] (constantly (redirect "/MainPage"))))

(defn -main []
  (run-jetty #'wiki {:port 8080}))
