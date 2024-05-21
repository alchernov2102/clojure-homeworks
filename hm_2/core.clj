;; Problem 58, Function Composition
;; Write a function which allows you to create function compositions. 
;;The parameter list should take a variable number of functions, 
;;and create a function applies them from right-to-left.


;; If only one function is provided return itself
;; recursive call of the first function (f) applied to the result of a second
;; function (g) moving to the end of [fs] list.
;; used core.clj for ref
(defn my-comp [f & fs]
  (if (empty? fs)
    f
    (let [g (first fs)
          step (rest fs)]
      (recur (fn [& args]
               (f (apply g args))) step))))

;; Alternatively using composition function for pair of funcs.
(defn comp-pair [f g]
  (fn [& args]
    (f (apply g args))))
    
(defn my-comp [f & fs]
  (if (empty? fs)
    f
    (let [g (first fs)
          step (rest fs)]
      (recur (comp-pair f g) step))))
      

;; Tests
(= [3 2 1] ((my-comp rest reverse) [1 2 3 4]))
(= 5 ((my-comp (partial + 3) second) [1 2 3 4]))
(= true ((my-comp zero? #(mod % 8) +) 3 5 7 9))
(= "HELLO" ((my-comp #(.toUpperCase %) #(apply str %) take) 5 "hello world"))

;; Problem 59, Juxtaposition
;;Take a set of functions and return a new function that takes a variable 
;;number of arguments and returns a sequence containing the result of 
;;applying each function left-to-right to the argument list.
;;returns a vector ((juxt a b c) x) => [(a x) (b x) (c x)]

;; Special case for 2 funcs
(defn double-juxt [f g]
  (fn [& args]
    (cons (apply f args) (map g args))))
    
((double-juxt #(filter odd? %) reverse)[1 2 3 4 5])

;; Using map and function literals
(defn my-juxt [& fs]
  (fn [& args] 
    (if (empty? fs)
      '()
      (map #(apply % args) fs))))
      
;; Using loop and recursion (more verbose)
(defn alt-juxt [& fs]
  (fn [& args]
    (loop [vect '()
           funs fs] 
      (if (empty? funs)
        vect
        (recur (cons (apply (first funs) args) vect) (rest funs))))))
        
((alt-juxt reverse reverse #(filter even? %) butlast)[1 2 3 4])


;; Tests
(= [21 6 1] ((my-juxt + max min) 2 3 5 1 6 4))
(= ["HELLO" 5] ((my-juxt #(.toUpperCase %) count) "hello"))
(= [2 6 4] ((my-juxt :a :c :b) {:a 2, :b 4, :c 6, :d 8 :e 10}))

