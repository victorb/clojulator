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

(defn evaluate-input
  [input-to-evaluate]
  (try
    (js/eval (clojure.string/replace input-to-evaluate "x" "*"))
    (catch js/Error e
      e)))

(defn handle-input-change
  [button-label]
  ;; Add a way to use the results when the previous time was evaluated
  (cond
    (= button-label "=")
      (set-results! (evaluate-input (@app-state :input)))
    (= button-label "C")
      (remove-number-from-input!)
    (= button-label "AC")
      (do (reset-results!) (reset-input!))
    :else (add-number-to-input! button-label)))

(defn receive-keypress
  [ev]
  (let
    [pressed-key (.fromCharCode js/String (.-keyCode ev))
     keycode (.-keyCode ev)]
    (cond
      (= keycode, 13) (handle-input-change "=")
    :else (handle-input-change pressed-key))))

(defn receive-special-keypress
  [ev]
   (cond
     (= (.-keyCode ev), 27) (handle-input-change "AC")
     (= (.-keyCode ev), 8)
       (do
         (.preventDefault ev)
         (handle-input-change "C"))))

(events/listen (googdom/getWindow) (.-KEYPRESS events/EventType) receive-keypress)
(events/listen (googdom/getWindow) (.-KEYDOWN events/EventType) receive-special-keypress)

(defn normalize-key-or-button-press
  "Takes either a keyCode or an button-label and normalizes it into a button-label"
  [event-or-label]
  event-or-label)
;; Normalization of the keypress and the button clicks
;;
;; Func for listen for click
(defn handle-button-click
  [button-label]
  (handle-input-change (normalize-key-or-button-press button-label)))

;; Func for listen for keypress
;; (events/listen (googdom/getWindow) (.-KEYPRESS events/EventType) receive-keypress)
;; (defn receive-keypress
;;   [ev]
;;   (handle-input-change (normalize-key-or-button-press (.-keyCode ev))))

;; Func for applying change
;; (defn handle-input-change []
;;   (cond
;;     (= button-label "=")
;;       (set-results! (evaluate-input (@app-state :input)))
;;     (= button-label "C")
;;       (remove-number-from-input!)
;;     (= button-label "AC")
;;       (do (reset-results!) (reset-input!))
;;     :else (add-number-to-input! button-label)))

(defn print-input
  "Simply just print the input"
  [input]
  (println input))

(defn button-view [button-label]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {
        :className "button"
        :onClick #(handle-button-click button-label)
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
  (.clear window.console))
