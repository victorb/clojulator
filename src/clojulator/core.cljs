(ns clojulator.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.dom :as googdom]
            [goog.events :as events]))

(enable-console-print!)

;; state and state modifiers

(defonce app-state
  (atom {:buttons [
       "+" "-" "x" "/"
       "7" "8" "9" "%"
       "4" "5" "6" "C"
       "1" "2" "3" "AC"
       "0" "." "VAT" "="]
    :input ""
    :results ""}))

(defn add-number-to-input!
  [number]
  (swap! app-state assoc :input
    (str (@app-state :input) number)))
(defn remove-number-from-input!
  []
  (swap! app-state assoc :input
       (clojure.string/join
         (butlast (@app-state :input)))))
(defn reset-input! [] (swap! app-state assoc :input ""))
(defn reset-results! [] (swap! app-state assoc :results ""))
(defn set-results! [results]
  (do (reset-results!)
      (.setTimeout js/window #(swap! app-state assoc :results results) 100)))

;; Evaluator

(defn evaluate-input
  [input-to-evaluate]
  (try
    (js/eval (clojure.string/replace input-to-evaluate "x" "*"))
    (catch js/Error e
      "Error")))

;; Input handling

(defn handle-input-change
  "Method that does the right thing depending on input"
  [button-label]
  (cond
    (= button-label "=")
      (set-results! (evaluate-input (@app-state :input)))
    (= button-label "C")
      (remove-number-from-input!)
    (= button-label "AC")
      (do (reset-results!) (reset-input!))
    :else (add-number-to-input! button-label)))

(defn receive-keypress
  "Receives normal letters/numbers from keypress event"
  [ev]
  (let
    [pressed-key (.fromCharCode js/String (.-keyCode ev))
     keycode (.-keyCode ev)]
    (cond
      (= keycode, 13) (handle-input-change "=")
    :else (handle-input-change pressed-key))))

(defn receive-special-keypress
  "Receives special key combinations, like ESC, from keydown event"
  [ev]
   (cond
     (= (.-keyCode ev), 27) (handle-input-change "AC")
     (= (.-keyCode ev), 8)
       (do
         (.preventDefault ev)
         (handle-input-change "C"))))

(defn handle-button-click
  [button-label]
  (handle-input-change button-label))

;; Register for global events
(events/listen (googdom/getWindow) (.-KEYPRESS events/EventType) receive-keypress)
(events/listen (googdom/getWindow) (.-KEYDOWN events/EventType) receive-special-keypress)

;; Views
(defn button-view [button-label]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {
        :className "button"
        :onTouchEnd #(handle-button-click button-label)
        } button-label))))
(defn buttons-view [data owner]
  (reify
    om/IRender
    (render [this]
        (apply dom/div #js {:id "buttons"}
           (om/build-all button-view (:buttons data))))))
(defn input-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (:input data)))))
(defn results-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (:results data)))))

;; Mounting
(om/root input-view app-state
  {:target (. js/document (getElementById "input"))})
(om/root buttons-view app-state
  {:target (. js/document (getElementById "buttons-mount"))})
(om/root results-view app-state
  {:target (. js/document (getElementById "results"))})

;; (swap! app-state update-in [:__figwheel_counter] inc)
;; (defn on-js-reload []
;;   (.clear window.console))
