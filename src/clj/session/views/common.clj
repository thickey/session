(ns session.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "session"]
               (include-css "bootstrap/css/bootstrap.css")
               (include-css "css/codemirror.css")
               (include-css "css/cm-theme/ambiance.css")
               ]
              [:body {:style "padding-top:20px;padding-left:60px;background-color:#FFF"}
               content
               [:script {:type "text/javascript" :src "js/codemirror-compressed.js"}]
               [:script {:type "text/javascript" :src "js/cm-util/runmode.js"}]
               [:script {:type "text/javascript" :src "js/subpar.js"}]
               [:script {:type "text/javascript" :src "jquery.js"}]
               [:script {:type "text/javascript" :src "js/flot-flot-f0e78fc/jquery.flot.js"}]
               [:script {:type "text/javascript" :src "bootstrap/js/bootstrap.js"}]
               [:script {:type "text/javascript" :src "js/vendor/jquery.ui.widget.js"}]
               [:script {:type "text/javascript" :src "js/locale.js"}]
               [:script {:type "text/javascript" :src "cljs/bootstrap.js"}]
               ]))
