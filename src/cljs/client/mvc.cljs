(ns session.client.mvc
  (:use-macros [cljs-jquery.macros :only [$]]))

(defprotocol IMVC
  (view [this])
  (control [this])
  )

(defmulti control2 #(identity (:view (meta %))))

(defmethod control2 :default [] nil)

(defmulti view2 #(identity (:view (meta %))))

(defmethod view2 :dom [arg] ($ arg))

(defmethod view2 :default [arg]  (cond
   (instance? js/Element arg) arg
   (instance? js/jQuery arg) arg
   true (pr-str arg)))


(defn render [m] (let [v (view m)] (control m) v))

(extend-type default IMVC
             (view [this] (cond
                           (instance? js/Element this) this
                           (instance? js/jQuery this) this
                           true (pr-str this)))
             (control [this] this))
