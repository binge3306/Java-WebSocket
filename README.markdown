[![Build Status](https://travis-ci.org/ck1125/Java-WebSocket.png?branch=master)](https://travis-ci.org/ck1125/Java-WebSocket)
Java WebSockets
===============


该项目包含用java写的websocket 服务器和客户端。该项目还实现了Java NIO，能够实现非阻塞事件驱动模型
This repository contains a barebones WebSocket server and client implementation
written in 100% Java. The underlying classes are implemented `java.nio`, which allows for a
non-blocking event-driven model (similar to the
[WebSocket API](http://dev.w3.org/html5/websockets/) for web browsers).

下面是实现 websocket 协议版本。
通常Draft17是经常使用的。现在被chrome16+和IE10支持，可能以后其他的浏览器也会添加对该协议的支持。
Implemented WebSocket protocol versions are:

 * [RFC 6455](http://tools.ietf.org/html/rfc6455)
 * [Hybi 17](http://tools.ietf.org/id/draft-ietf-hybi-thewebsocketprotocol-17.txt)
 * [Hybi 10](http://tools.ietf.org/id/draft-ietf-hybi-thewebsocketprotocol-10.txt)
 * [Hixie 76](http://tools.ietf.org/id/draft-hixie-thewebsocketprotocol-76.txt)
 * [Hixie 75](http://tools.ietf.org/id/draft-hixie-thewebsocketprotocol-75.txt)

[Here](https://github.com/TooTallNate/Java-WebSocket/wiki/Drafts) some more details about protocol versions/drafts. 


##Build
You can build using Ant or Maven but there is nothing against just putting the source path ```src/main/java ``` on your applications buildpath.
你可以使用ant 或者是maven编译
使用maven
maven clean compile (install)
maven eclipse:eclipse
###Ant

``` bash
ant 
```

will create the javadoc of this library at ```doc/``` and build the library itself: ```dest/java_websocket.jar```

The ant targets are: ```compile```, ```jar```, ```doc``` and ```clean```

###Maven

To use maven just add this dependency to your pom.xml:
```xml
<dependency>
    <groupId>org.java-websocket</groupId>
    <artifactId>Java-WebSocket</artifactId>
    <version>1.3.0</version>
</dependency> 
```

Running the Examples
-------------------

**Note:** If you're on Windows, then replace the `:` (colon) in the classpath
in the commands below with a `;` (semicolon).

编译完成之后，可以启动chat server
After you build the library you can start the chat server (a `WebSocketServer` subclass):

``` bash
java -cp build/examples:dist/java_websocket.jar ChatServer
```

启动chat server 之后，可以启动一些client来测试
Now that the server is started, you need to connect some clients. Run the
Java chat client (a `WebSocketClient` subclass):

``` bash
java -cp build/examples:dist/java_websocket.jar ChatClient
```
如何是启动java swing GUI 你可以向所有其他的连接发送消息，也可以收到其他客户端发送的消息
The chat client is a simple Swing GUI application that allows you to send
messages to all other connected clients, and receive messages from others in a
text box.

你也可以打开chat.html 页面，如果打开的浏览器 不支持 websocket协议，那么就会调用Flash插件来模拟websocket（模拟器的地址是http://github.com/gimite/web-socket-js）
In the example folder is also a simple HTML file chat client `chat.html`, which can be opened by any browser. If the browser natively supports the WebSocket API, then it's
implementation will be used, otherwise it will fall back to a
[Flash-based WebSocket Implementation](http://github.com/gimite/web-socket-js).


下面是如何实现你自己的websocket server
Writing your own WebSocket Server
---------------------------------

org.java_websocket.server.WebSocketServer 这个抽象类实现了server一段的websocket协议。
websocket server除了通过http建立socket连接，本身不做任何事情。你可以写一个子类，实现你自己的目的。


The `org.java_websocket.server.WebSocketServer` abstract class implements the
server-side of the
[WebSocket Protocol](http://www.whatwg.org/specs/web-socket-protocol/).
A WebSocket server by itself doesn't do anything except establish socket
connections though HTTP. After that it's up to **your** subclass to add purpose.



org.java_websocket.server.WebSocketClient 这是一个连接websocket 客户端的抽象类。
其构造函数是通过ws 连接到服务器的。
其`onOpen`, `onClose`, `onMessage` and `onIOError` 将在整个生命周期中，都有效。
而且必须在你的子类中写具体实现。
Writing your own WebSocket Client
---------------------------------

The `org.java_websocket.server.WebSocketClient` abstract class can connect to
valid WebSocket servers. The constructor expects a valid `ws://` URI to
connect to. Important events `onOpen`, `onClose`, `onMessage` and `onIOError` 
get fired throughout the life of the WebSocketClient, and must be implemented 
in **your** subclass.

WSS Support
---------------------------------
This library supports wss.
To see how to use wss please take a look at the examples.<br>

If you do not have a valid **certificate** in place then you will have to create a self signed one.
Browsers will simply refuse the connection in case of a bad certificate and will not ask the user to accept it.
So the first step will be to make a browser to accept your self signed certificate. ( https://bugzilla.mozilla.org/show_bug.cgi?id=594502 ).<br>
If the websocket server url is `wss://localhost:8000` visit the url `https://localhost:8000` with your browser. The browser will recognize the handshake and allow you to accept the certificate. This technique is also demonstrated in this [video](http://www.youtube.com/watch?v=F8lBdfAZPkU).

The vm option `-Djavax.net.debug=all` can help to find out if there is a problem with the certificate.

It is currently not possible to accept ws and wss connections at the same time via the same websocket server instance.

For some reason firefox does not allow multible connections to the same wss server if the server uses a different port than the default port(443).


If you want to use `wss` on the android platfrom you should take a look at [this](http://blog.antoine.li/2010/10/22/android-trusting-ssl-certificates/).

I ( @Davidiusdadi ) would be glad if you would give some feedback whether wss is working fine for you or not.


软件支持。
Minimum Required JDK
--------------------

`Java-WebSocket` is known to work with:

 * Java 1.5 (aka SE 6)
 * Android 1.6 (API 4)

Other JRE implementations may work as well, but haven't been tested.


Testing in Android Emulator
---------------------------

Please note Android Emulator has issues using `IPv6 addresses`. Executing any
socket related code (like this library) inside it will address an error

``` bash
java.net.SocketException: Bad address family
```

You have to manually disable `IPv6` by calling

``` java
java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
```

somewhere in your project, before instantiating the `WebSocketClient` class. 
You can check if you are currently testing in the Android Emulator like this

``` java
if ("google_sdk".equals( Build.PRODUCT )) {
  // ... disable IPv6
}
```


Getting Support
---------------

If you are looking for help using `Java-WebSocket` you might want to check out the
[#java-websocket](http://webchat.freenode.net/?channels=java-websocket) IRC room
on the FreeNode IRC network. 


License
-------

Everything found in this repo is licensed under an MIT license. See
the `LICENSE` file for specifics.



websocket 服务器内核介绍：
虽然服务器和客户端都是用NIO实现的，但是使用方式是不一样的。客户端是用一个线程来处理读写，但是服务器却是用多个线程来处理读写。
服务器使用一个选择器线程和多个工作线程。
选择器线程执行所有的NIO操作，其注册连接和读写通道。
但是选择器不做任何的编码和解码工作，这些都是工作线程在执行。
选择器和工作线程之间的交互通过队列来完成。包括解码队列，写队列和缓存队列。
其循环工作周期是：选择器从缓存队列中拿一个未使用的缓存，将通道准备好的数据放入其中，然后将该缓存加入解码队列。然后工作线程从解码队列中拿出缓存，消费其中的内容。然后将缓存重新放到缓冲区队列中。
该服务器的性能可以通过调整工作线程的数量和缓存区队列的大小，以及缓存的大小。以及通道内部的缓冲区大小 来提升。未来可能有方法可以直接设置这些参数。

























