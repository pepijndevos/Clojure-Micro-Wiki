(ns wiki.core
  (:use
    ring.adapter.jetty
    net.cgrand.moustache
    [ring.middleware params stacktrace reload]
    ring.util.response
    hiccup.core
    com.ashafa.clutch)
  (:require [clojure.string :as s])
  (:import org.pegdown.PegDownProcessor))

(def db (get-database "wiki"))

(defn wrap-db [f]
  (fn [req]
    (with-db db
      (f req))))

(defn markup [text]
  (s/replace (.markdownToHtml (PegDownProcessor.) (if text text ""))
             #"(?:[A-Z][a-z]+){2,}" "<a href=\"/$0\">$0</a>"))

(defn page [title content rev]
  (html
    [:html
     [:head
      [:title title]]
     [:body
      [:h1 title]
      [:div (markup content)]
      [:hr]
      [:form {:method "POST" :action (str "/" title)}
       [:textarea {:name "content"} content]
       [:br]
       [:input {:type "hidden", :value rev :name "rev"}]
       [:input {:type "submit", :value "Submit!"}]]]]))

(defn show [_ title]
  (let [{:keys [content _rev]} (get-document title)]
    (response
      (page title content _rev))))

(defn update [{{:strs [content rev]} :params} title]
  (if (= rev "")
    (create-document {:content content} title)
    (update-document {:_rev rev :_id title :content content}))
  (show {} title))

(def wiki
  (app
    wrap-params
    ;wrap-stacktrace
    ;(wrap-reload ['wiki.core])
    wrap-db
    [[title #"(?:[A-Z][a-z]+){2,}"]] {:get (delegate show title)
                                      :post (delegate update title)}
    [&] (constantly (redirect "/MainPage"))))

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty wiki {:port port})))
