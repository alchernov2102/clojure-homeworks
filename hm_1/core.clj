(ns homework1.core)

(def result (atom {:correct 0 :incorrect 0}))
(defmacro =check [left right]
  `(let [left# ~left
         right# ~right]
     (if (= right# left#)
       (swap! result update :correct inc)
       (do
         (println "missmatch:" ~(str (first left)) left# right#)
         (swap! result update :incorrect inc)))
     left#))

;; Functions

;; reduce examples
(defn my-reduce [fun init coll]
  ;; if collection is empty
  (if (empty? coll)
    init
    (if (empty? (rest coll))
      ;; base
      (fun init (first coll)) 
      (my-reduce fun (fun init (first coll)) (rest coll)))))
      
  

;; nth examples
(defn my-nth [coll n]
  (if (empty? coll)
    nil
    (if (= n 0)
      (first coll)
      (my-nth (rest coll) (- n 1)))))

;; max/min examples
(defn my-max [coll]
  ;;col empty
  (if (empty? coll)
    nil
    ;; len == 1
    (if (empty? (rest coll))
      (first coll)
      ;; len > 1
      (if (> (first coll) (my-max (rest coll)))
        (first coll)
        (my-max (rest coll))))))

(defn my-min [coll]
  ;;col empty
  (if (empty? coll)
    nil
    ;; len == 1
    (if (empty? (rest coll))
      (first coll)
      ;; len > 1
      (if (< (first coll) (my-min (rest coll)))
        (first coll)
        (my-min (rest coll))))))

;; count examples
(defn my-count [coll]
  ;; base case
  (if (empty? coll)
    0
    (+ 1 (my-count (rest coll)))))

(defn my-take [n coll]
  (if  (= n 0)
    nil
    (cons (first coll) (my-take (dec n) (rest coll)))))

(defn my-concat [coll1 coll2]
  (if (empty? coll1)
    coll2
    (if (empty? coll2)
      coll1
      ;; both colls are not empty
      (cons (first coll1) (my-concat (rest coll1) coll2)))))

(defn my-merge [map1 map2]
  (if (empty? map1)
    map2)
  (if (empty? map2)
    map1
    (conj map1 map2)))
    
    
;; For n maps
;; example from core.clj/merge
(defn n-merge [& ms]
  (reduce #(conj %1 %2) ms))


(defn my-group-by [f coll])

(defn my-keys [ascoll]
  (if (empty? ascoll)
    '()
    ;; take only the first value each time (k)
    (let [k (first(first ascoll))]
      (cons k (my-keys (rest ascoll))))))
          
    
(defn my-vals [ascoll]
  (if (empty? ascoll)
    '()
    ;; take only the second value each time (v)
    (let [v (last (first ascoll))]
      (cons v (my-vals (rest ascoll))))))

;; alternatively without let

(defn alt-vals [ascoll]
  (if (empty? ascoll)
    '()
    (cons (last (first ascoll)) (alt-vals (rest ascoll)))))


(defn my-select-keys [map keys] 
  ;; for every key in keys
  (reduce (fn[acc k] 
            (let [value (k map)]
              (if (nil? value)
                ;; if value is not found in the map return '{}
                acc
                ;; return pair {:key value} if found
                (assoc acc k value))))
          {}
          keys))
          
;; Alternatively using macro for

(defn alt-select-keys [map keys]
  (into {} (for [k keys]
    (let [acc {}
          value (k map)]
      (if (nil? value)
        acc
        (assoc acc k value))))))
        
(alt-select-keys {:a 1 :b 2 :c 3} [:a :b :d])  

(defn my-filter [pred coll]
  (if (empty? coll)
    '()
    (if (pred (first coll))
      (cons (first coll) (my-filter pred (rest coll)))
      (my-filter pred (rest coll)))))

;; Tests

(=check (my-nth [10 20 30 40] 2) 30)
;; (=check (my-nth [1 2 3 4] 10) nil) ; Assuming nil for out of bounds
(=check (my-nth [1 2 3 4] 3) 4)

(=check (my-reduce + 0 [1 2 3 4]) 10)
(=check (my-reduce str "" ["a" "b" "c"]) "abc")
;;(=check (my-reduce + 0 (range 10000)) 49995000)

(=check (my-max [5 3 9 1]) 9)
(=check (my-min [5 3 9 1]) 1)
(=check (my-max [-5 -3 -9 -1]) -1)
(=check (my-min [-5 -3 -9 -1]) -9)
(=check (my-max []) nil)
(=check (my-min []) nil)

(=check (my-filter even? [1 2 3 4 5 6]) [2 4 6])
(=check (my-filter #(> (count %) 3) ["hi" "hello" "hey" "greetings"]) ["hello" "greetings"])
(=check (my-filter #(and (even? %) (> % 10)) [12 2 13 14 3]) [12 14])

(=check (my-count [1 2 3 4 5]) 5)
(=check (my-count [[1 2] [3 4] [5]]) 3)
(=check (my-count []) 0)

(=check (my-take 3 [5 4 3 2 1]) [5 4 3])

(=check (my-merge {:a 1 :b 2} {:b 3 :c 4}) {:a 1 :b 3 :c 4})
(=check (my-merge {:foo "bar"} {:foo "baz", :hello "world"}) {:foo "baz", :hello "world"})
(=check (my-merge {} {:a 1}) {:a 1})

(=check (my-group-by :type [{:type :a :value 1} {:type :b :value 2} {:type :a :value 3}])
        {:a [{:type :a :value 1} {:type :a :value 3}], :b [{:type :b :value 2}]})
(=check (my-group-by even? [1 2 3 4 5 6]) {true [2 4 6], false [1 3 5]})
(=check (my-group-by count ["one" "two" "three" "four"]) {3 ["one" "two"], 5 ["three"], 4 ["four"]})

(=check (my-keys {:a 1 :b 2 :c 3}) [:a :b :c])
(=check (my-keys {:foo "bar" :baz "qux"}) [:foo :baz])
(=check (my-keys {}) [])

(=check (my-concat [1 2] [3 4]) [1 2 3 4])
;;(=check (count (my-concat (range 5000) (range 5000 10000))) 10000)

(=check (my-vals {:a 1 :b 2 :c 3}) [1 2 3])
(=check (my-vals {:foo "bar" :baz "qux"}) ["bar" "qux"])
(=check (my-vals {}) [])

(=check (my-select-keys {:a 1 :b 2 :c 3} [:a :c]) {:a 1 :c 3})
(=check (my-select-keys {:name "Alice" :age 30 :gender "Female"} [:name :age]) {:name "Alice", :age 30})
(=check (my-select-keys {:foo "bar" :hello "world"} [:foo]) {:foo "bar"})

(println "Test results:" @result)

