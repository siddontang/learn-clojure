Clojure有如下几种基本的数据类型，它们都是immutable的，也就是说，我们不可能更改它们，任何对原有数据结构的修改都会生成一份新的copy。

## Number

Clojure的number包括int，float以及ratio，譬如下面这些都是number：

```
1      ; int
1.0    ; double
1 / 5  ; ratio
```

## String

Clojure的string是用双引号来表示的，譬如`”abc"`，如果字符串里面有双引号，我们使用`\"`来表示，譬如`"\"abc\""`。

## Map

Clojure的map跟其他语言的一样，就是一个kv dictionary，clojure的map有hash map以及sorted map两种，不过通常我们都不用特别区分。

map使用`{}`来表示，map的key可以是keyword，也可以是基本的数据类型，譬如`{:a 1, 2 2, 3.0 3, "4" 4}`，map里面也能嵌套map，譬如`{:a {:b 1}}`。

我们可以通过hash-map创建一个hash map，譬如 `(hasp-map :a 1 :b 2)`，通过get函数来获取map里面的数据，譬如：

```clojure
user=> (get {:a 1 :b 2} :a)
1
user=> (get {:a 1 :b 2} :c)
nil
```

通过get-in获取嵌套map的数据，譬如:

```clojure
user=> (get-in {:a {:b 1}} [:a :b])
1
user=> (get-in {:a {:b 1}} [:a :c])
nil
user=> (get-in {:a {:b 1}} [:b :a])
nil
```

## Keyword

在map里面，我们出现了keyword的概念，keyword使用`:`来表示，通常用在map的key上面。譬如下面这些都是keyword:

```
:a
:hello
:34
:_?
```

keyword能够被当成function，譬如：

```clojure
user=> (:a {:a 1 :b 2})
1
```

它等价于

```clojure
user=> (get {:a 1 :b 2} :a)
1
```

如果keyword不存在，我们也可以指定一个默认值:

```clojure
user=> (:c {:a 1 :b 2} "abc")
"abc"
user=> (get {:a 1 :b 2} :c "abc")
"abc"
```

## Vector

Vector就是数组，以index 0开始，使用`[]`表示。

```clojure
user=> [1 2 3]
[1 2 3]
user=> (get [1 2 3] 0)
1
```

我们可以使用vector来创建一个vector，譬如：

```clojure
user=> (vector 1 2 3)
[1 2 3]
```

使用conj函数往vector里面追加数据:

```clojure
user=> (conj [1 2 3] 4)
[1 2 3 4]
```

## List

List也就是链表，跟vector有一些不同，譬如不能通过get来获取元素。List使用`()`来表示，因为`()`在Clojure里面是作为operations来进行求值的，所以我们需要用`'()`来避免list被求值。

我们也可以使用list函数来构造list，譬如：

```clojure
user=> `(1 2 3)
(1 2 3)
user=> (list 1 2 3)
(1 2 3)
```

List不能使用get，但可以用nth函数，但需要注意out of bound的error。

```clojure
user=> (nth '(1 2 3) 0)
1
user=> (nth '(1 2 3) 4)

IndexOutOfBoundsException   clojure.lang.RT.nthFrom (RT.java:871)
```

我们也能够通过conj函数在list里面追加元素，不过不同于vector，是从头插入的:

```clojure
user=> (conj '(1 2 3) 4)
(4 1 2 3)
```

## Set

Set是唯一值的集合，使用`#{}`表示，我们也可以hash-set函数来进行set的创建：

```clojure
user=> #{1 2 3}
#{1 3 2}
user=> (hash-set 1 2 3 1)
#{1 3 2}
```

我们可以使用set函数将vector或者list转成set，譬如：

```clojure
user=> (set [1 2 3 1])
#{1 3 2}
user=> (set '(1 2 3 1))
#{1 3 2}
```

我们使用conj函数在set里面添加元素:

```clojure
user=> (conj #{1 2} 1)
#{1 2}
user=> (conj #{1 2} 3)
#{1 3 2}
```

contains?用来判断某一个值是否在set里面，譬如：

```clojure
user=> (contains? #{:a :b} :a)
true
user=> (contains? #{:a :b} :c)
false
user=> (contains? #{:a :b nil} nil)
true
```

我们也可以使用get来获取某个元素：

```clojure
user=> (get #{:a :b nil} :a)
:a
user=> (get #{:a :b nil} nil)
nil
```

使用keyword的方式也可以:

```clojure
user=> (:a #{:a :b})
:a
user=> (:c #{:a :b})
nil
```