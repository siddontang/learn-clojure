Macro是函数式编程里面很重要的一个概念，在之前，我们已经使用了Clojure里面的一些macro，譬如when，and等，我们可以通过macroexpand获知：

```clojure
user=> (macroexpand '(when true [1 2 3])))
(if true (do [1 2 3]))
user=> (doc when)
-------------------------
clojure.core/when
([test & body])
Macro
  Evaluates test. If logical true, evaluates body in an implicit do.
nil
```

可以看到，when其实就是if + do的封装，很类似C语言里面的macro。

## defmacro

我们可以通过defmacro来定义macro：

```clojure
user=> (defmacro my-plus
  #_=> "Another plus for a + b"
  #_=> [args]
  #_=> (list (second args) (first args) (last args)))
#'user/my-plus
user=> (my-plus (1 + 1))
2
user=> (macroexpand '(my-plus (1 + 1)))
(+ 1 1)
```

macro的定义比较类似函数的定义，我们需要定义一个macro name，譬如上面的my-plus，一个可选择的macro document，一个参数列表以及macro body。body通常会返回一个list用于后续被Clojure进行执行。

我们可以在macro body里面使用任何function，macro以及special form，然后使用macro的时候就跟函数调用一样。但是跟函数不一样的地方在于函数在调用的时候，参数都是先被evaluated，然后才被传入函数里面的，但是对于macro来说，参数是直接传入macro，而没有预先被evaluated。

我们也能在macro里面使用argument destructuring技术，进行参数绑定：

```clojure
user=> (defmacro my-plus2
  #_=> [[op1 op op2]]
  #_=> (list op op1 op2))
#'user/my-plus2
user=> (my-plus2 (1 + 1))
```

## Symbol and Value

编写macro的时候，我们其实就是构建list供Clojure去evaluate，所以在macro里面，我们需要quote expression，这样才能给Clojure返回一个没有evaluated的list，而不是在macro里面就自己evaluate了。也就是说，我们需要明确了解symbol和value的区别。

譬如，现在我们要实现这样一个功能，一个macro，接受一个expression，打印并且输出它的值，可能看起来像这样:

```clojure
user=> (let [result 1] (println result) result)
1
1
```

然后我们定义这个macro：

```clojure
user=> (defmacro my-print
  #_=> [expression]
  #_=> (list let [result expression]
  #_=> (list println result)
  #_=> result))
```
我们会发现出错了，错误为"Can't take value of a macro: #'clojure.core/let"，为什么呢？在上面这个例子中，我们其实想得到的是let symbol，而不是得到let这个symbol引用的value，这里let并不能够被evaluate。

所以为了解决这个问题，我们需要quote let，只是返回let这个symbol，然后让Clojure外面去负责evaluate，如下：

```clojure
user=> (defmacro my-print
  #_=> [expression]
  #_=> (list 'let ['result expression]
  #_=> (list 'println 'result)
  #_=> 'result))
#'user/my-print
user=> (my-print 1)
1
1
```

## Quote

### Simple Quoting

如果我们仅仅想得到一个没有evaluated的symbol，我们可以使用quote:

```clojure
user=> (+ 1 2)
3
user=> (quote (+ 1 2))
(+ 1 2)
user=> '(+ 1 2)
(+ 1 2)
user=> '123
123
user=> 123
123
user=> 'hello
hello
user=> hello

CompilerException java.lang.RuntimeException: Unable to resolve symbol: hello in this context
```

### Syntax Quoting

在前面，我们通过`'`以及quote了解了simple quoting，Clojure还提供了syntax quoting `` ` ``

```clojure
user=> `1
1
user=> `+
clojure.core/+
user=> '+
+
```

可以看到，syntax quoting会返回fully qualified symbol，所以使用syntax quoting能够让我们避免命名冲突。

另一个syntax quoting跟simple quoting不同的地方在于，我们可以在syntax quoting里面使用`~`来unquote一些form，这等于是说，我要quote这一个expression，但是这个expression里面某一个form先evaluate，譬如:

```clojure
user=> `(+ 1 ~(inc 1))
(clojure.core/+ 1 2)
user=> `(+ 1 (inc 1))
(clojure.core/+ 1 (clojure.core/inc 1))
```

这里还需要注意一下unquote splicing:

```clojure
user=> `(+ ~(list 1 2 3))
(clojure.core/+ (1 2 3))
user=> `(+ ~@(list 1 2 3))
(clojure.core/+ 1 2 3)
```

syntax quoting会让代码更加简洁，具体到前面print那个例子，我们let这些都加了quote，代码看起来挺丑陋的，如果用syntax quoting，如下:

```clojure
user=> (defmacro my-print2
  #_=> [expression]
  #_=> `(let [result# ~expression]
  #_=> (println result#)
  #_=> result#))
#'user/my-print2
user=> (my-print2 1)
1
1
```