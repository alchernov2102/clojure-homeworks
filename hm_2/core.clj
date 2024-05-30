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



;; elegant solution - not mine, added for testing and debugging
(defn my-merge-with [f & maps]
  (->> maps 
    ;; add each key-value pair to the list of vectors  
    (apply concat) 
    ;; returns a map {:a [[:a 1] [:a 2]]}   
    (group-by key) 
    ;; get key and reduce a function for every value   
    (map (fn [[k vs]] [k (reduce f (map val vs))])) 
    ;; return map instead of list   
    (into {})
    ))

(comment
  "Returned by (group-by key maps)"
  {:a [[:a 1] [:a 3]], :b [[:b 2]]})

(my-merge-with + {:a 1 :b 2} {:a 3})

;; Tests
(= (my-merge-with * {:a 2, :b 3, :c 4} {:a 2} {:b 2} {:c 5})
   {:a 4, :b 6, :c 20})
(= (my-merge-with - {1 10, 2 20} {1 3, 2 10, 3 15})
   {1 7, 2 10, 3 15})
(= (my-merge-with concat {:a [3], :b [6]} {:a [4 5], :c [8 9]} {:b [7]})
   {:a [3 4 5], :b [6 7], :c [8 9]})

