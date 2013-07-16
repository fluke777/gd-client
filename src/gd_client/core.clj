(ns gd-client.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def ^:dynamic *default-params* {})
(def ^:dynamic *gd-login*)
(def ^:dynamic *gd-password*)
(def ^:dynamic *pid* nil)

(defn request
  ([method-fun url] (request method-fun url {}))
  ([method-fun url params]
   (let [server-url (:server *default-params*)
         final-params (merge *default-params* params)
         response (try+
                   (method-fun (str server-url url) final-params)
                   (catch [:status 401] e
                     (if (= (clojure.core/get params :retry) false)
                       (throw+ e)
                       (do
                         (request http/get "/gdc/account/token" {:retry false})
                         (request method-fun url params)))))
         format (:content-type response)
         body (:body response)]
     (cond
      (= format :json) (json/parse-string body true)
      :else response))))

(defn post
  ([url]
   (post url {}))
  ([url params]
   (request http/post url params)))

(defn get
  ([url]
   (get url {}))
  ([url params]
    (request http/get url params)))

(defn login
  [user-login password]
  (let [login-body (json/generate-string {:postUserLogin {:login user-login :password password :remember 1}})]
    (post "/gdc/account/login" {:body login-body :retry false})))

(defn with-user
  "login to GD with particular user"
  [user-login password body]
  (binding [clj-http.core/*cookie-store* (clj-http.cookies/cookie-store)
            *default-params* {:server "https://secure.gooddata.com" :content-type :json :accept :json :debug true :debug-body true :throw-entire-message? true}]
    (let [login-body (json/generate-string {:postUserLogin {:login user-login :password password :remember 1}})]
      (login user-login password))
    (body)))

(defn project
  [pid]
  (def ^:dynamic *pid* pid))


(defn connect
  [user-login password]
  (let [store (clj-http.cookies/cookie-store)]
    (def ^:dynamic *default-params* (merge {:server "https://secure.gooddata.com" :content-type :json :accept :json :debug true :debug-body true :throw-entire-message? true :cookie-store store}))
    (def ^:dynamic *gd-login* user-login)
    (def ^:dynamic *gd-password* password)
    (login user-login password)))