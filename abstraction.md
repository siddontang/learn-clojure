## Abstraction

在了解sequence之前，我们可以先了解下abstraction，abstraction的概念在很多语言里面都有，譬如Go，interface就是abstraction:


```go
type IA interface {
    DoFunc()
}

type A struct {}

func (a *A) DoFunc() {

}
```

在上面这个例子中，struct A实现了DoFunc函数，我们就可以认为，A实现了IA。

## Sequence Abstraction

Clojure也提供了abstraction的概念，这里我们主要来了解下sequence abstraction。

在Clojure里面，如果这些core sequence function first，rest和cons能够用于某个data structure，我们就可以认为这个data structure实现了sequence abstraction，就能被相关的sequence function进行操作，譬如map，reduce等。

### First

first返回collection里面的第一个元素，譬如：

```
user=> (first [1 2 3])
1
user=> (first '(1 2 3))
1
user=> (first #{1 2 3})
1
user=> (first {:a 1 :b 2})
[:a 1]
```

### Rest

rest返回collection里面，第一个元素后面的sequence，譬如：

```
user=> (rest [1 2 3])
(2 3)
user=> (rest [1])
()
user=> (rest '(1 2 3))
(2 3)
user=> (rest #{1 2 3})
(3 2)
user=> (rest {:a 1 :b 2})
([:b 2])
```

### Cons

Cons则是将一个元素添加到collection的开头，譬如：

```
user=> (cons 1 [1 2 3])
(1 1 2 3)
user=> (cons 1 '(1 2 3))
(1 1 2 3)
user=> (cons 1 #{1 2 3})
(1 1 3 2)
user=> (cons 1 {:a 1 :b 2})
(1 [:a 1] [:b 2])
user=> (cons {:c 3} {:a 1 :b 2})
({:c 3} [:a 1] [:b 2])
```

从上面的例子可以看出，Clojure自身的vector，list等都实现了sequence abstraction，所以他们也能够被一些sequence function调用：

```
user=> (defn say [name] (str "hello " name))
#'user/say
user=> (map say [1 2])
("hello 1" "hello 2")
user=> (map say '(1 2))
("hello 1" "hello 2")
user=> (map say #{1 2})
("hello 1" "hello 2")
user=> (map say {:a 1 :b 2})
("hello [:a 1]" "hello [:b 2]")
user=> (map #(say (second %)) {:a 1 :b 2})
("hello 1" "hello 2")
```

## Collection Abstraction

跟sequence abstraction类似，Clojure里面的core data structure，譬如vector，list等，都实现了collection abstraction。

Collection abstraction通常是用于处理整个data structure的，譬如:

```
user=> (count [1 2 3])
3
user=> (empty? [])
true
user=> (every? #(< % 3) [1 2 3])
false
user=> (every? #(< % 4) [1 2 3])
true
```

