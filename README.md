# BtCrawler

一个磁力爬虫，基于SpringBoot和Netty框架实现。将爬取到的infohash存到数据库，结合爬取到的时间做一个排行榜，得出一段时间内最活跃的infohash。

#### 参考资料

- 对于DHT协议，我看的[BitTorrent的DHT协议(译自官方版本)](https://blog.csdn.net/xxxxxx91116/article/details/7970815)。对于文章最后说的vote请求，可能不完全正确，具体参考[这个回答](https://lists.ibiblio.org/pipermail/bittorrent/2011-January/002414.html) 。
- 刚开始写的时候是只用Netty框架的，后来参考了[@BrightStarry](https://github.com/BrightStarry/zx-bt)的[项目](https://github.com/BrightStarry/zx-bt)引入SpringBoot和多个端口支持，学习了他的链式处理消息的方式，并在这个的基础上优化和更改了部分实现。

#### 需要注意

- nid编码问题：nid是每个node由20个随机字节组成，但是编码(bencode)和传输时需要转化为**字符串**。所以需要*new String(nid,**charset**)*;这个charset只能是**ISO_8859_1**。参考：

  ```java
  byte[] nid = RandomUtils.nextBytes(20);
  boolean utf8 = Arrays.equals(nid, new String(nid, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8));//false
  boolean ascii = Arrays.equals(nid, new String(nid, StandardCharsets.US_ASCII).getBytes(StandardCharsets.US_ASCII));//false
  boolean iso_8859_1 = Arrays.equals(nid, new String(nid, StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1));//true
  ```

- Netty的*BootStrap*大多用在TCP的客户端引导类，由于UDP是无连接的，所以可以看作是一个服务器。但是毕竟不是ServerBootStrap，所以不管NioEventLoopGroup的初始化参数设置为多大，一个BootStrap只有一个线程处理数据，需要自己使用线程池来处理数据。

- 运行程序需要一个有公网IP的机器，一般的云服务器都有。

#### 个人想法

- 爬虫不需要实现协议中的路由表部分，只需要不断的发送FindNode请求和解析对方响应的FindNodeReply然后解析出新的Node继续请求，形成一个循环。

#### 不足之处

- 处理其他节点的消息时，很容易就超出了线程池的最大数量，现在的处理方式是ThreadPoolExecutor内置的拒绝策略：*DiscardOldestPolicy* ，可能会放弃处理announce_peer的线程，降低效率。



