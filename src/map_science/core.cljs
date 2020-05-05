(ns map-science.core
  (:require	[clojure.string :as string]
            [cljs-bean.core :as b]
            [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled defkeyframes]]
            ;; How import libraries?
            ;; Import a NPM library is really easy,
            ;; just want to type the name and use :as
            ;; maybe you will have to use :default instead of :as
            ;; depends of how this library was did exported.
            ;; ["some-library" :as ains]
            ;; For it, in case :as throw an error, use (js/console.log ains)
            ;; to see what kind of exportation have.
            ["react-dom" :as rdom]
            ["react-range" :as rrange :refer [getTrackBackground Range]]
            ["react-router-dom" :as rr]))


;; NUMBER MASTER

(defstyled StyleNumberButton 
  :div 
  {:display "flex"
   :flex-grow "1"
   :flex "0 1 30%"
   :align-items "center"
   :margin "3px"
   :justify-content "center"
   :vertical-align "middle"
   :color "white"
   :border "1px solid black"
   :border-radius "10px"
   :background "gray"
   :height "100px"
   :width "50px"})

(defstyled StyledContainer
  :div 
  {:width "100%"
   :height "100%"
   :text-align "center"})

(defnc aoa 
  [{:keys [active number]}]
  (StyleNumberButton 
    (d/p {:style {:color (if active "red" "blue")}} number)))

(defnc NumberButton 
  [{:keys [number set-current]}]
  (defn pressButton
    [number]
    (set-current number))

  (let [[active set-active] (hooks/use-state false)]
    (d/div {:on-click (fn [] (set-active (not active)) (pressButton number))}
      ($ aoa {:number number
              :active active}))))

(defstyled StyledContainerButton
  :div 
  {:display "flex"
   :flex-flow "row wrap"
   :width "380px"
   :margin "0 auto"})

(defnc NumbersButtonsPanel 
  [{:keys [set-current]}]
  (StyledContainerButton 
    (map #($ NumberButton {:number (inc %1) :set-current set-current}) 
         (take 9 (range)))))

(defnc ScreenPanel
  [{:keys [digits current]}]
  (let [asteriscs (- digits (count current))]
    (d/div
      (d/p (str current (clojure.string/join (repeat asteriscs "*")))))))


(defmulti panel-actions (fn [_ action] (first action)))

(defmethod panel-actions
  ::init [state _]
  (or state {:level 5 
             :cypher "12345" 
             :status "playing" 
             :current "" 
             :blow "" 
             :hit ""}))

(defmethod panel-actions
  ::set-current [state [_ y]]
  (let [current (:current state)
        status (:status state)
        max-size (:level state)]
    (if (and (= status "playing") (> max-size (count current)))
      (merge state {:current (str current y)})
      state)))

(defmethod panel-actions
  ::set-status [state [_ y]]
  (merge state {:status y}))

(defmethod panel-actions
  ::set-level [state [_ y]]
  (merge state {:level y}))

(defmethod panel-actions
  ::set-blow [state [_ y]]
  (merge state {:blow y})) 

(defmethod panel-actions
  ::set-hit [state [_ y]]
  (merge state {:hit y})) 

(defnc Panel []
  (let [[state dispatch] (hooks/use-reducer panel-actions nil #(panel-actions % [::init]))
        set-level #(dispatch [::set-level %])
        set-current #(dispatch [::set-current %])
        set-status #(dispatch [::set-status %])
        set-blow #(dispatch [::set-blow %])
        set-hit #(dispatch [::set-hit %])
        digits (count (:cypher state))]
    (d/div 
      ($ ScreenPanel {:current (:current state) :digits digits})
      ($ NumbersButtonsPanel {:set-current set-current :digits digits}))))


(defnc App []
  (StyledContainer
    ;; create elements out of components
    ($ Panel)))


(defn ^:export start
  []
  (rdom/render ($ App) (js/document.getElementById "app")))
