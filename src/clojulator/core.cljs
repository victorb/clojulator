(ns clojulator.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.dom :as googdom]
            [goog.events :as events]))

(enable-console-print!)

(defonce app-state
  (atom {:buttons [
       "+" "-" "x" "/"
       "7" "8" "9" "%"
       "4" "5" "6" "C"
       "1" "2" "3" "AC"
       "0" "." "VAT" "="]
    :input ""
    :results ""}))

(defn receive-keydown
  [ev]
  (println (.getBrowserEvent ev)))

(events/listen (googdom/getWindow) (.-KEYDOWN events/EventType) receive-keydown)

(defn add-number-to-input
  [number]
    (swap! app-state assoc :input
           (str (@app-state :input)
                number)))

(defn remove-number-from-input!
  []
  (swap! app-state assoc :input
         (clojure.string/join
           (butlast (@app-state :input)))))

(defn evaluate-input
  [input-to-evaluate]
  (js/eval (clojure.string/replace input-to-evaluate "x" "*")))

(defn reset-input! [] (swap! app-state assoc :input ""))
(defn reset-results! [] (swap! app-state assoc :results ""))
(defn set-results! [results]
  (do (reset-results!)
      (.setTimeout js/window #(swap! app-state assoc :results results) 100)))

(defn handle-button-press
  [button-label]
  ;; Add a way to use the results when the previous time was evaluated
  (cond
    (= button-label "=")
      (set-results! (evaluate-input (@app-state :input)))
    (= button-label "C")
      (remove-number-from-input!)
    (= button-label "AC")
      (do (reset-results!) (reset-input!))
    :else (add-number-to-input button-label)))

(defn button-view [button-label]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {
        :className "button"
        :onClick #(handle-button-press button-label)
        } button-label))))

(defn buttons-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
        (apply dom/div nil
           (om/build-all button-view (:buttons data)))))))

(defn input-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/input #js {:className "input" :value (:input data)}))))

(defn results-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/input #js {:className "results" :value (:results data)}))))

(om/root input-view app-state
  {:target (. js/document (getElementById "input"))})
(om/root buttons-view app-state
  {:target (. js/document (getElementById "buttons"))})

(om/root results-view app-state
  {:target (. js/document (getElementById "results"))})



;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (events/unlisten (googdom/getWindow) (.-KEYDOWN events/EventType) receive-keydown)
  )
