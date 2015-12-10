Clojure是一门支持并发编程的语言，它提供了很多特性让我们非常的方便进行并发程序的开发。

## Future

Future可以让我们在另一个线程去执行任务，它是异步执行的，所以调用了future之后会立即返回。譬如：

```clojure
user=> (future (Thread/sleep 3000) (println "I'am back")) 
#object[clojure.core$future_call$reify__6736 0x7118d905 {:status :pending, :val nil}]
user=> (println "I'am here")
I'am here
nil
user=> I'am back
```

在上面的例子中，sleep会阻塞当前线程的执行，但是因为我们用了future，所以clojure将其放到了另一个线程中，然后继续执行下面的语句。

有时候，我们使用了future之后，还需要知道future的任务执行的结果，知识后就需要用defer来实现了。我们可以使用defer或者`@`来获取future的result，譬如：

```clojure
user=> (let [result (future (println "run only once") (+ 1 1))]
  #_=> (println (deref result))
  #_=> (println @result))
run only once
2
2
nil
```

deref还可以支持timeout设置，如果超过了等待时间，就返回一个默认值。

```clojure
user=> (deref (future (Thread/sleep 100) 0) 10 5)
5
user=> (deref (future (Thread/sleep 100) 0) 1000 5)
0
```

我们也可以使用realized?来判断一个future是否完成

```clojure
user=> (realized? (future (Thread/sleep 1000)))
false
user=> (let [f (future)]
  #_=> @f
  #_=> (realized? f))
true
```

## Delay

Delay可以让我们定义一个稍后执行的任务，并不需要现在立刻执行。

```clojure
user=> (def my-delay
  #_=> (delay (let [msg "hello world"]
  #_=> (println msg)
  #_=> msg)))
#'user/my-delay
```

我们可以通过@或者force来执行delay的任务
```clojure
user=> @my-delay
hello world
"hello world"
user=> (force my-delay)
"hello world"
```

Clojure会将delay的任务结果缓存，所以第二次delay的调用我们直接获取的是缓存结果。

我们可以将delay和future一起使用，定义一个delay操作，在future完成之后，调用delay，譬如：

```clojure
user=> (let [notify (delay (println "hello world"))]
  #_=> (future ((Thread/sleep 1000) (force notify))))
#object[clojure.core$future_call$reify__6736 0x2de625f3 {:status :pending, :val nil}]
user=> hello world
```

## Promise

Promise是一个承诺，我们定义了这个promise，就预期后续会得到相应的result。我们通过deliver来将result发送给对应的promise，如下：

```clojure
user=> (def my-promise (promise))
#'user/my-promise
user=> (deliver my-promise (+ 1 1))
#object[clojure.core$promise$reify__6779 0x30dc687a {:status :ready, :val 2}]
user=> @my-promise
2
```

promise也跟delay一样，会缓存deliver的result

```clojure
user=> (deliver my-promise (+ 1 2))
nil
user=> @my-promise
2
```

我们也可以将promise和future一起使用

```clojure
user=> (let [hello-promise (promise)]
  #_=> (future (println "Hello" @hello-promise))
  #_=> (Thread/sleep 1000)
  #_=> (deliver hello-promise "world"))
Hello world
```

## Atom

在并发编程里面，`a = a + 1`这条语句并不是安全的，在clojure里面，我们可以使用atom完成一些原子操作。如果大家熟悉c语言里面的compare and swap那一套原子操作函数，其实对Clojure的atom也不会陌生了。

我们使用atom创建一个atom，然后使用@来获取这个atom当前引用的值:

```clojure
user=> (def n (atom 1))
#'user/n
user=> @n
1
```

如果我们需要更新该atom引用的值，我们需要通过一些原子操作来完成，譬如：

```clojure
user=> (swap! n inc)
2
user=> @n
2
user=> (reset! n 0)
0
user=> @n
0
```

## Watch

对于一些数据的状态变化，我们可以使用watch来监控，一个watch function包括4个参数，关注的key，需要watch的reference，譬如atom等，以及该reference之前的state以及新的state，譬如：

```clojure
user=> (defn watch-n
  #_=> [key watched old-state new-state]
  #_=> (if (> new-state 1)
  #_=> (println "new" new-state)
  #_=> (println "old" old-state)))
user=> (def wn (atom 1))
#'user/wn
user=> @wn
1
user=> (add-watch wn :a watch-n)
#object[clojure.lang.Atom 0x4b28dcf8 {:status :ready, :val 1}]
user=> (reset! wn 1)
old 1
1
user=> (reset! wn 2)
new 2
2
```

## Validator

我们可以使用validator来验证某个reference状态改变是不是合法的，譬如：

```clojure
user=> (defn validate-n
  #_=> [n]
  #_=> (> n 1))
#'user/validate-n
user=> (def vn (atom 2 :validator validate-n))
#'user/vn
user=> (reset! vn 10)
10
user=> (reset! vn 0)

IllegalStateException Invalid reference state  clojure.lang.ARef.validate (ARef.java:33)
```

在上面的例子里面，我们建立了一个validator，参数n必须大于1，否则就是非法的。

## Ref

在前面，我们知道使用atom，能够原子操作某一个reference，但是如果要操作一批atom，就不行了，这时候我们就要使用ref。

```clojure
user=> (def x (ref 0))
#'user/x
user=> (def y (ref 0))
#'user/y
```

我们创建了两个ref对象x和y，然后在dosync里面对其原子更新

```clojure
user=> (dosync 
  #_=> (ref-set x 1)
  #_=> (ref-set y 2)
  #_=> )
2
user=> [@x @y]
[1 2]
user=> (dosync 
  #_=> (alter x inc)
  #_=> (alter y inc))
user=> (dosync 
  #_=> (commute x inc)
  #_=> (commute y inc))
```

ref-set就类似于atom里面的reset!，alter就类似swap!，我们可以看到，还有一个commute也能进行reference的更新，它类似于alter，但是稍微有一点不一样。

在一个事务开始之后，如果使用alter，那么在修改这个reference的时候，alter会去看有没有新的修改，如果有，就会重试当前事务，而commute则没有这样的检查。所以如果通常为了性能考量，并且我们知道程序没有并发的副作用，那就使用commute，如果有，就老老实实使用alter了。

我们通过@来获取reference当前的值，在一个事务里面，我们通过ensure来保证获取的值一定是最新的:

```clojure
user=> (dosync 
  #_=> (ref-set x (ensure y)))
```

## Dynamic var

通常我们通过def定义一个变量之后，最好就不要更改这个var了，但是有时候，我们又需要在某些context里面去更新这个var的值，这时候，最好就使用dynamic var了。

我们通过如下方式定义一个dynamic var:

```clojure
user=> (def ^:dynamic *my-var* "hello")
#'user/*my-var*
```

dynamic var必须用`*`包裹，在lisp里面，这个叫做earmuffs，然后我们就能够通过binding来动态改变这个var了:

```
user=> (binding [*my-var* "world"] *my-var*)
"world"
user=> (println *my-var*)
hello
user=> (binding [*my-var* "world"] (println *my-var*)
  #_=> (binding [*my-var* "clojure"] (println *my-var*)) (println *my-var*))
world
clojure
world
```

可以看到，binding只会影响当前的stack binding。