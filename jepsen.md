[jepsen](https://github.com/aphyr/jepsen)是一个分布式测试库，我们可以使用它对某个分布式系统执行一系列操作，并最终验证这些操作是否正确执行。

jepsen已经成功验证了很多分布式系统，我们可以在它的源码里面看到相关系统的测试代码，包括mysql-cluster，zookeeper，elasticsearch等。

为什么要研究jepsen，主要在于我们需要进行分布式数据库[tidb](https://github.com/pingcap/tidb)的测试，自己写一套分布式测试框架难度比较大，并且还不能保证在分布式环境下面自身测试框架是不是对的，于是使用一个现成的，经过验证的测试框架就是现阶段最好的选择了，于是我们就发现了jepsen。

jepsen是使用clojure进行开发的，所以这也就是为什么我要学习clojure的原因，不过比较郁闷的是，学了几天，还是没有看懂太多的代码，只能慢慢不断摸索了。

## Design

一个Jepsen的测试通过会在一个control node上面运行相关的clojure程序，control node会使用ssh登陆到相关的系统node（jepsen叫做db node）进行一些测试操作。

当我们的分布式系统启动起来之后，control node会启动很多进程，每一个进程都能使用特定的client访问到该分布式系统。一个generator为每一个进程生成一系列的操作，让其执行。每一个操作都会被记录到history里面。在执行操作的同时，另一个nemesis进程会尝试去破坏这个分布式系统，譬如使用iptable断开网络连接等。

最后，当所有操作执行完毕之后，jepsen会使用一个checker来分析验证history并且生成相关的报表。

从上面可以看出，jepsen的设计原理其实很简单，就是对分布式系统执行一系列操作，并且同时不停的破坏系统，最后通过验证操作的结果来检验整个分布式系统的健壮性。

## Install

Jepsen的安装使用不是一件很容易的事情，因为它需要一个control node，五个db node来测试，幸运的是，我们有docker，现在docker支持了docker in docker技术，所以我们可以很方便的使用一个docker来运行五个docker。

Jepsen已经提供了相关的docker image，我们可以直接使用:

```
docker run --privileged -t -i tjake/jepsen
```

但有时候我们需要测试自己的case，所以需要提供volumn的支持，于是我稍微修改了一下，使用了一个定制的docker:

```
FROM tjake/jepsen

RUN mkdir /jepsen_dev
VOLUME /jepsen_dev

ADD ./bashrc /root/.bashrc
```

在启动的时候，我们可以将自己的test case mount到docker里面，便于使用，使用docker build构建:

```
docker build -t jepsen_dev .
```

### Example test

构建好jepsen的docker环境之后，我们就可以编写简单地测试了，参考它的文档，我们建立一个meowdb的工程，使用jepsen 0.0.6版本，然后在meowdb_test.clj里面写上如下代码:

```clojure
(ns jepsen.meowdb-test
  (:require [clojure.test :refer :all]
            [jepsen.core :refer [run!]]
            [jepsen.meowdb :as meowdb]))

(def version
  "What meowdb version should we test?"
  "1.2.3")

(deftest basic-test
  (is (:valid? (:results (run! (meowdb/basic-test version))))))
```

然后在meowdb.clj里面：

```clojure
(ns jepsen.meowdb
  "Tests for MeowDB"
  (:require [clojure.tools.logging :refer :all]
            [clojure.core.reducers :as r]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [knossos.op :as op]
            [jepsen [client :as client]
                    [core :as jepsen]
                    [db :as db]
                    [tests :as tests]
                    [control :as c :refer [|]]
                    [checker :as checker]
                    [nemesis :as nemesis]
                    [generator :as gen]
                    [util :refer [timeout meh]]]
            [jepsen.control.util :as cu]
            [jepsen.control.net :as cn]
            [jepsen.os.debian :as debian]))
            
(defn basic-test
  "A simple test of MeowDB's safety."
  [version]
  tests/noop-test)
```

我们启动docker:

```
docker run --privileged -t -i -v meowdb:/jepsen_dev --name jepsen jepsen_dev
```

然后执行`lein test`，如果没有啥意外，我们会输出如下类似的结果：

```
INFO  jepsen.store - Wrote /jepsen_dev/meowdb/store/noop/20151211T094940.000Z/history.txt
INFO  jepsen.store - Wrote /jepsen_dev/meowdb/store/noop/20151211T094940.000Z/results.edn
INFO  jepsen.core - Everything looks good! ヽ(‘ー`)ノ

{:valid? true,
 :linearizable-prefix [],
 :worlds ({:model {}, :fixed [], :pending #{}, :index 0})}
 
Ran 1 tests containing 1 assertions.
0 failures, 0 errors.
```

这里仅仅是对jepsen的一个简单介绍，后续我还需要仔细研究，争取早日能用到tidb上面。
