(ns map-science.core
  (:require	[clojure.string :as string]
            [cljs-bean.core :as b]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            ;; How import libraries?
            ;; Import a NPM library is really easy,
            ;; just want to type the name and use :as
            ;; maybe you will have to use :default instead of :as
            ;; depends of how this library was did exported.
            ;; ["some-library" :as ains]
            ;; For it, in case :as throw an error, use (js/console.log ains)
            ;; to see what kind of exportation have.
            [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled defkeyframes]]
            ["react-dom" :as rdom]
            ["react-range" :as rrange :refer [getTrackBackground Range]]
            ["react-router-dom" :as rr]))


;; NUMBER MASTER

(defstyled StyleNumberButton 
  :div 
  {:display "flex"
   :justify-content "center"
   :vertical-align "middle"
   :color "white"
   :border "1px solid black"
   :background "gray"
   :height "50px"
   :width "50px"})

(defnc NumberButton 
  [{:keys [number set-current]}]
  (defn pressButton
    [number]
    (set-current number))

  (let [[active set-active] (hooks/use-state false)]
    (d/p {:on-click (fn [] (set-active (not active)) (pressButton number)) 
          :style {:color (if active "red" "blue")}} 
         number)))


(defnc ButtonsPanel 
  [{:keys [set-current]}]
  (map #(StyleNumberButton 
          ($ NumberButton {:number (inc %1) :set-current set-current})) 
       (take 9 (range))))


(defmulti panel-actions (fn [_ action] (first action)))

(defmethod panel-actions
  ::init [state _]
  (or state {:level 1 
             :cypher "" 
             :status "waiting" 
             :current "" 
             :blow "" 
             :hit ""}))

(defmethod panel-actions
  ::set-current [state [_ y]]
  (merge state {:current (str (:current state) y)}))

(defmethod panel-action
  ::set-status [state [_ y]]
  (merge state {:status y}))

(defmethod panel-action
  ::set-level [state [_ y]]
  (merge state {:level y}))

(defmethod panel-acton
  ::set-blow [state [_ y]]
  (merge state {:blow y})) 


(defmethod panel-acton
  ::set-hit [state [_ y]]
  (merge state {:hit y})) 

(defnc Panel []
  (let [[state dispatch] (hooks/use-reducer panel-actions nil #(panel-actions % [::init]))
        set-level #(dispatch [::set-level %])
        set-current #(dispatch [::set-current %])
        set-status #(dispatch [::set-status %])
        set-blow #(dispatch [::set-blow %])
        set-hit #(dispatch [::set-hit %])]
    ($ ButtonsPanel {:set-current set-current})))


(defnc App []
  (d/div
    ;; create elements out of components
    ($ Panel)))


(defn ^:export start
  []
  (rdom/render ($ App) (js/document.getElementById "app")))