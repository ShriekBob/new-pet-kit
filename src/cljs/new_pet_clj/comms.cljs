(ns new-pet-clj.comms
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [new-pet-clj.config :as conf]
            [re-frame.core :as rf]
            [taoensso.timbre :as t]))

(def url (if conf/DEBUG
           "http://localhost:3449/api/"
           "https://www.newpetkit.com/api/"))

(defn create-cart
  [kit]
  (do (rf/dispatch [:set-cart-status :loading])
      (rf/dispatch [:set-cart {}])
      (go (let [endpoint (str url "create-cart")
                response (<! (http/post endpoint {:transit-params kit
                                                  :with-credentials? false}))
                cart (or (:cart (:body response)) {})
                cart-status (or (:status (:body response)) :inactive)]
            (do (rf/dispatch [:set-cart cart])
                (rf/dispatch [:set-cart-status cart-status]))))))
