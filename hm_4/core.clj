(ns core
  (:require [ring.adapter.jetty :as jetty]
            [hiccup2.core :as h]
            [compojure.core :as compojur]
            [compojure.route :as route]
            [clojure.pprint :as pp]
            [clojure.string :as st]))
            
(defonce lectures (atom [
                         {:title "How Does REPL Work?"
                          :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi sit amet pretium eros. Proin dignissim enim in mattis eleifend. Vestibulum lectus justo, condimentum et pulvinar a, ultrices molestie massa. Suspendisse porta mollis lorem, sit amet interdum lorem tempus nec. Pellentesque venenatis a felis non efficitur. Nullam vulputate elementum sapien eget porta. Integer id elit porta, tempus est in, tristique nunc. Nullam porta, ex auctor scelerisque pellentesque, dui elit posuere est, sed fermentum nisi quam eu est. Nullam ultrices dapibus ultricies. Phasellus gravida justo quis ante molestie, quis interdum mi placerat."
                          :category "REPL"}
                         {:title "Functional Programming"
                          :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi sit amet pretium eros. Proin dignissim enim in mattis eleifend. Vestibulum lectus justo, condimentum et pulvinar a, ultrices molestie massa. Suspendisse porta mollis lorem, sit amet interdum lorem tempus nec. Pellentesque venenatis a felis non efficitur. Nullam vulputate elementum sapien eget porta. Integer id elit porta, tempus est in, tristique nunc. Nullam porta, ex auctor scelerisque pellentesque, dui elit posuere est, sed fermentum nisi quam eu est. Nullam ultrices dapibus ultricies. Phasellus gravida justo quis ante molestie, quis interdum mi placerat."
                          :category "Functional Approach"}
                         {:title "Collections And Data Structures"
                          :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi sit amet pretium eros. Proin dignissim enim in mattis eleifend. Vestibulum lectus justo, condimentum et pulvinar a, ultrices molestie massa. Suspendisse porta mollis lorem, sit amet interdum lorem tempus nec. Pellentesque venenatis a felis non efficitur. Nullam vulputate elementum sapien eget porta. Integer id elit porta, tempus est in, tristique nunc. Nullam porta, ex auctor scelerisque pellentesque, dui elit posuere est, sed fermentum nisi quam eu est. Nullam ultrices dapibus ultricies. Phasellus gravida justo quis ante molestie, quis interdum mi placerat."
                          :category "Data Structures"}
                         {:title "Concurrency And Parallelism"
                          :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi sit amet pretium eros. Proin dignissim enim in mattis eleifend. Vestibulum lectus justo, condimentum et pulvinar a, ultrices molestie massa. Suspendisse porta mollis lorem, sit amet interdum lorem tempus nec. Pellentesque venenatis a felis non efficitur. Nullam vulputate elementum sapien eget porta. Integer id elit porta, tempus est in, tristique nunc. Nullam porta, ex auctor scelerisque pellentesque, dui elit posuere est, sed fermentum nisi quam eu est. Nullam ultrices dapibus ultricies. Phasellus gravida justo quis ante molestie, quis interdum mi placerat."
                          :category "Concurrency"}
                         {:title "Building Macros"
                          :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi sit amet pretium eros. Proin dignissim enim in mattis eleifend. Vestibulum lectus justo, condimentum et pulvinar a, ultrices molestie massa. Suspendisse porta mollis lorem, sit amet interdum lorem tempus nec. Pellentesque venenatis a felis non efficitur. Nullam vulputate elementum sapien eget porta. Integer id elit porta, tempus est in, tristique nunc. Nullam porta, ex auctor scelerisque pellentesque, dui elit posuere est, sed fermentum nisi quam eu est. Nullam ultrices dapibus ultricies. Phasellus gravida justo quis ante molestie, quis interdum mi placerat."
                          :category "Building Abstractions"}]))
                              

(defn generate-uri [title]
  ;; Get "how-does-repl-work" from the title "How Does REPL Work?"
  (st/lower-case (st/join "-" (re-seq #"[A-Za-z0-9]+" title))))

(defn main-page-body []
  (str
   (h/html [:div [:h1 "Clojure Lectures"] [:hr] [:ul (for [lecture @lectures] [:li [:a {:href (str "/" (generate-uri (:title lecture)))} (:title lecture)]])]])))

(defn lecture-body [lecture]
  (str
   (h/html [:div [:h1 (:title lecture)] [:hr] [:i (:category lecture)]] [:div [:p (:body lecture)]])))
   

  
;; (def generate-routes
;;   (for [lecture @lectures]
;;     (compojur/GET (str "/" (generate-uri (:title lecture))) [] (lecture-body lecture))))


;; FIXME: Think how to define routes dynamically -- done via URL destruct
(compojur/defroutes my-routes
  (compojur/GET "/" [] (main-page-body))
  (compojur/GET "/:id" [id] (lecture-body (first(filter (fn [e] (= (generate-uri (:title e)) id)) @lectures)))))
  ;;(apply compojur/routes generate-routes)
  
      
  
(def id "how-does-repl-work")

(filter (fn [e] (= (generate-uri (:title e)) id)) @lectures)

(def app 
  (compojur/routes 
   my-routes
   (route/not-found (str (h/html [:center [:h2 "404 - NOT FOUND"]])))))
   
                  
(defonce server
  (jetty/run-jetty #'app {:port 3000
                              :join? false}))