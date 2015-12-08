# Why Clojure

首先，为什么选择Clojure？第一个原因当然在于jepsen是用Clojure编写的，但除此之外，Clojure也有其他吸引我去学习的地方。

+ Lisp。Clojure是一门Lisp方言，如果喜欢函数式编程的同学一下子就会非常喜欢。虽然我没有任何函数式编程的经验，但以前因为受到《计算机程序的构造与解释》这本书的影响，一直想找个机会好好学习一门Lisp语言。
+ JVM。最开始的Clojure是是运行在JVM上面的，当然现在也支持了其他平台（譬如.net），运行在JVM上面的好处就在于跨平台了，并且能很方便的使用java的library。笔者之前也没有任何java开发经验，正好也能对java相关的函数了解一点。
+ Concurrency。函数式编程语言天生就是支持并发编程的，因为数据都是immutability的。Clojure还提供了Software Transactional Memory, Agent等，让并发编程更加简单。


# Start Clojure

下载下来的Clojure包就是一个JAR文件，我们可以直接用java运行，在这里，笔者使用的是Clojure 1.7.0，进入Clojure目录之后，运行:

```
java -cp clojure-1.7.0.jar clojure.main
Clojure 1.7.0
user=> (+ 1 2) 
3
```

不过多数时候，我们都是使用[lein](http://leiningen.org/)来进行Clojure的项目构建以及REPL的执行，笔者使用的是最新的lein版本，在mac下面，直接

```
brew install leiningen
```

即可安装，安装成功之后，运行lein repl就可以进行REPL。

```
lein repl
nREPL server started on port 56289 on host 127.0.0.1 - nrepl://127.0.0.1:56289
REPL-y 0.3.7, nREPL 0.2.10
Clojure 1.7.0
user=> 
```

我们通过lein建立第一个Clojure工程。

```
lein new app clojure-noob
```

进入clojure-noob目录，运行lein run，我们会得到如下输出：

```
lein run
Hello, World!
```

我们可以在src/clojure_noob/core.clj这个文件里面进行编辑，将World换成Clojure，如下：

```
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, Clojure!"))
```

再次运行lein run，得到：

```
lein run
Hello, Clojure!
```

通过lein run的方式可以很方便的执行代码，但是如果要将我们的代码share出去，就需要生成一个jar文件了，我们使用lein uberjar来生成jar，生成的jar文件为target/uberjar/clojure-noob-0.1.0-SNAPSHOT-standalone.jar，我们可以在java里面直接运行了。

```
java -jar target/uberjar/clojure-noob-0.1.0-SNAPSHOT-standalone.jar 
Hello, Clojure!
```