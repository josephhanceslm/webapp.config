(ns webapp.config
(:require-macros [cljs.core.async.macros :refer [go]])
(:require [cljs-http.client :as http]
          [cljs.core.async :refer [<!]]
          [reagent.core :as reagent :refer [atom]]))

(defonce app-config 
; atom to hold the environment data
  (reagent/atom {:response nil :status nil}))

(defn get-config [url ratom]
; Read the specified configuration and store it in the configuration atom
  (swap! ratom assoc :status (:status nil))
  (swap! ratom assoc :response (:body nil))
  (go (let [response (<! (http/get url
    {:with-credentials? false}))]
    (swap! ratom assoc :status (:status response))
    (if (= 200 (:status response))
      (swap! ratom assoc :response (:body response))))))

(defn get-config-map [] 
  ; Gets the entire map of config file items
  (@app-config :response))

(defn get-config-status [] 
  ; Gets the status of the config file.  200 indicates good, anything else an error
  (@app-config :status))

(defn- get-config-keys-local [config-map] (keys config-map))
(defn get-config-keys [] 
  ; Get the list of config item keys
  (get-config-keys-local (get-config-map)))

(defn get-config-item [item] 
  ; Get a single config item
  (item (@app-config :response)))

(get-config "edn/config.edn" app-config)
