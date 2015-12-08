Clojure的语法非常的简单，只要熟悉Lisp，几乎可以无缝使用Clojure了。

## Form

Clojure的代码是由一个一个form组成的，form可以是基本的数据结构，譬如number，string等，也可以是一个operation，对于一个operation来说，合法的结构如下:

```
(operator operand1 operand2 ... operandn)
```

第一个是operator，后面的就是该operator的参数，譬如`(+ 1 2 3)`，operator就是“+”， 然后参数为1， 2， 3，如果我们执行这个form，得到的结果为6。

## Control Flow

Clojure的control flow包括if，do和when。

### If

If的格式如下:

```
(if boolean-form
    then-form
    optional-else-form)
```

如果boolean-form为true，就执行then-form，否则执行optional-else-form，一些例子：

```
user=> (if false "hello" "world")
"world"
user=> (if true "hello" "world")
"hello"
user=> (if true "hello")
"hello"
user=> (if false "hello")
nil
```

### Do

通过上面的if可以看到，我们的then或者else只有一个form，但有时候，我们需要在这个条件下面，执行多个form，这时候就要靠do了。

```
user=> (if true
  #_=> (do (println "true") "hello")
  #_=> (do (println "false") "world"))
true
"hello"
```

在上面这个例子，我们使用do来封装了多个form，如果为true，首先打印true，然后返回“hello”这个值。

### When

When类似if和do的组合，但是没有else这个分支了，

```
user=> (when true
  #_=> (println "true")
  #_=> (+ 1 2))
true
3
```

### nil, true, false

Clojure使用nil和false来表示逻辑假，而其他的所有值为逻辑真，譬如：

```
user=> (if nil "hello" "world")
"world"
user=> (if "" "hello" "world")
"hello"
user=> (if 0 "hello" "world")
"hello"
user=> (if true "hello" "world")
"hello"
user=> (if false "hello" "world")
"world"
```

我们可以通过`nil?`来判断一个值是不是nil，譬如：

```
user=> (nil? nil)
true
user=> (nil? false)
false
user=> (nil? true)
false
```

也可以通过`=`来判断两个值是否相等：

```
user=> (= 1 1)
true
user=> (= 1 2)
false
user=> (= nil false)
false
user=> (= false false)
true
```

我们也可以通过and和or来进行布尔运算，or返回第一个为true的数据，如果没有，则返回最后一个，而and返回第一个为false的数据，如果都为true，则返回最后一个为true的数据，譬如：

```
user=> (or nil 1)
1
user=> (or nil false)
false
user=> (and nil false)
nil
user=> (and 1 false 2)
false
user=> (and 1 2)
2
```

## def

我们可以通过def将一个变量命名，便于后续使用，譬如:

```
user=> (def a [1 2 3])
#'user/a
user=> (get a 1)
2
```