(ns session.client.loop
  (:require
   [session.client.subscribe :as subscribe]
   [session.client.mvc :as mvc]
   [session.client.editor :as editor]
   [cljs.reader :as reader]
   [session.client.loop-creator :as loop-creator])
  (:use-macros [cljs-jquery.macros :only [$]])
  (:require-macros [fetch.macros :as pm]))


(defn render-loop-output [x]
  (let [y (session.client.mvc/view x)]
    (if (string? y)
      (try
        (let [d (. js/document (createElement "div"))]
          (. js/CodeMirror (runMode y "text/x-clojure" d))
          d)
       (catch js/Error e (js/alert (pr-str [y e]))))
      y)))


(deftype Loop [model dom]
  session.client.subscribe/ISubscribe
  (receive [this msg]
    (reset! (:output model) (:data msg)))
  ILookup
  (-lookup [o k] (model k))
  (-lookup [o k not-found] (model k not-found))
  session.client.mvc/IMVC
  (view [model]
    (let [v (let [id (:id model)]
       ($
        [:div {:id id :style "position:relative"}
         ($ [:i.icon-remove { :style "position:absolute;top:2px;right:-20px"}  ""]
             (click #(subscribe/send!
                      {:op :delete-loop :id id :data {:id id}})))
         [:div.span12 {:style "padding-top:0px;border-right:dotted #555 1px;border-left:dotted #555 1px"}

          [:div.row.loop-container {:style "margin-left:0px;padding-bottom:5px;border-bottom: dotted #555 1px"}
           [:i.icon-chevron-right {:height "18px" :style "opacity:.8;position:absolute;left:0px;top:2px"} ""]
           [:textarea {:id (str "area" id)}
            @(:input model)]]
          [:div.row {:style "position:relative;margin-left:5px;padding-top:5px;padding-left:0px;font-family: Monaco, Menlo, 'Andale Mono', 'lucida console', 'Courier New', monospace;color:#AAA"}
           [:i.icon-chevron-left {:height "18px" :style "opacity:.8;position:absolute;left:-25px;top:7px"} ""]
           [:div.loopout.cm-s-default {:style "background-color:#FFF"} (render-loop-output @(:output model))]
           ]







               ]
         [:div.row (session.client.mvc/render (loop-creator/LoopCreator. id (atom nil)))]

         ]
        (data "model" model)))]
      (reset! dom v)
      v))
  (control [this]
    (let [dom-elt @dom]
      (subscribe/subscribe! (:id model) this)
      (let [
            id (:id model) editor (atom [])]

        ($ dom-elt (on "post-render" #(reset! editor (editor/create-editor (str "area" id)))))
        ($ dom-elt (on "click" ".loop-deleter" #($ dom-elt (trigger "delete-loop"))))
        ($ dom-elt (on "evaluate-input"
                       #(do
                          ($ dom-elt (find ".loopout") (html ""))
                          (reset!
                           (:input model)
                           (.  @editor (getValue)))
                          (subscribe/send! {:op :evaluate-clj :id (:id model) :data @(:input model)}))))
        (add-watch (:output model) :update-output
                   (fn [key atom old new]
                     (let [viewable (mvc/view new)]
                       ($ dom-elt (find ".loopout") (html "")
                         (append
                          ($ [:div.cm-s-default {:style "background-color:#FFF"}
                              (render-loop-output new)]))))))))))
