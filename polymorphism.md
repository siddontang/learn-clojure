Clojure虽然是一门函数式编程语言，当也能很容易支持类似OOP那种polymorphism，能让我们写出更好的抽象代码。

## Multimethods

使用multimethod是一种快速在代码里面引入polymorphism的方法，我们可以定义一个dispatching function，然后指定一个dispatching value，通过它来确定调用哪一个函数。

譬如，我们需要计算一个图形的面积，我们知道，如果是一个长方形，那么方法就是`with * heigth`，如果是圆形，那么就是 `PI * radius * radius`。

```clojure
; define a multimethod for area with :Shape keyword.
(defmulti area :Shape)
(defn rect [wd ht] {:Shape :Rect :wd wd :ht ht})
(defn circle [radius] {:Shape :Circle :radius radius})
(defmethod area :Rect [r]
    (* (:wd r) (:ht r)))
(defmethod area :Circle [c]
    (* (. Math PI) (* (:radius c) (:radius c))))
(defmethod area :default [x] :oops)
(def r (rect 4 13))
(def c (circle 12))
```

我们在repl里面执行:

```clojure repl
user=> (area r)
52
user=> (area c)
452.3893421169302
user=> (area {})
:oops
```

## Protocol

从上面multimethod的实现可以，multimethod只是一个polymorphic操作，如果我们想实现多个，那么multimethod就不能满足了，这时候，我们就可以使用protocol。

protocol其实更类似其他语言里面interface，我们定义一个protocol，然后用不同的类型去特化实现，我们以jepsen的代码为例，因为jepsen可以测试很多db，所以它定义了一个db的protocol。

```clojure
(defprotocol DB
  (setup!     [db test node] "Set up the database on this particular node.")
  (teardown!  [db test node] "Tear down the database on this particular node."))
```

上面的代码定义了一个DB的protocol，然后有两个函数接口，用来setup和teardown对应的db，所以我们只需要在自己的db上面实现这两个函数就能让jepsen调用了，伪代码如下:

```clojure
(def my-db 
  (reify DB
    (setup! [db test node] "hello db")
    (teardown! [db test node] "goodbye db")))
```

然后就能直接使用DB protocol了。

```clojure repl
user=> (setup! my-db :test :node)
"hello db"
user=> (teardown! my-db :test :node)
"goodbye db"
```

## Record

有些时候，我们还想在clojure中实现OOP语言中class的效果，用record就能很方便的实现。record类似于map，这点就有点类似于C++ class中的field，然后还能实现特定的protocol，这就类似于C++ class的member function了。

record的定义很简单，我们使用defrecord来定义：

```clojure
user=> (defrecord person [name age])
user.person
```

这里，我们定义了一个person的record，它含有name和age两个字段，然后我们可以通过下面的方法来具体创建一个person:

```clojure repl
; 使用类似java的 . 操作符创建
user=> (person. "siddon" 30)
#user.person{:name "siddon", :age 30}
; 通过 ->person 函数创建
user=> (->person "siddon" 30)
#user.person{:name "siddon", :age 30}
; 通过 map->persion 函数创建，参数是map
user=> (map->person {:name "siddontang" :age 30)
```

因为record其实可以认为是一个map，所以很多map的操作，我们也同样可以用于record上面。

```clojure repl
user=> (def siddon (->person "siddon" 30))
#'user/siddon
user=> (assoc siddon :name "tang")
#user.person{:name "tang", :age 30}
user=> (dissoc siddon :name)
{:age 30}
```

record可以实现特定的protocol，譬如:

```clojure
(defprotocol SayP 
  (say [this]))

(defrecord person [name age]
  SayP
  (say [this] (str "hello " name)))
```

上面我们定义了SayP这个protocol，并且让person这个record实现了相关的函数，然后我们就可以直接使用了。

```clojure repl
user=> (say (->person "siddon" 30))
"hello siddon"
```