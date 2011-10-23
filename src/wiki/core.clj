(ns wiki.core
  (:use
    ring.adapter.jetty
    net.cgrand.moustache))

(def wiki
  (app
    [] "hello world"))

(defn -main []
  (run-jetty wiki {:port 8080}))
