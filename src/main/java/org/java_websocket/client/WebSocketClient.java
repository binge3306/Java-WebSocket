package org.java_websocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.WebsocketPongResponseException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.WebsocketConstant;

/**
 * A subclass must implement at least <var>onOpen</var>, <var>onClose</var>, and <var>onMessage</var> to be
 * useful. At runtime the user is expected to establish a connection via {@link #connect()}, then receive events like {@link #onMessage(String)} via the overloaded methods and to {@link #send(String)} data to the server.
 */
public abstract class WebSocketClient extends WebSocketAdapter implements Runnable, WebSocket {

	/**
	 * The URI this channel is supposed to connect to.
	 */
	protected URI uri = null;

	private WebSocketImpl engine = null;

	private Socket socket = null;

	private InputStream istream;

	private OutputStream ostream;

	private Proxy proxy = Proxy.NO_PROXY;

	private Thread writeThread;

	private Draft draft;

	private Map<String,String> headers;

	private CountDownLatch connectLatch = new CountDownLatch( 1 );

	private CountDownLatch closeLatch = new CountDownLatch( 1 );

	private int connectTimeout = 0;
	
	// wurunzhou add prameters at 20150611 for heartbeat begin
	// 最近ping 执行时间
	private Date pingNeartTime;

	// 最近pong 接收时间
	private Date pongNeartTime;
	// 记录ping 执行次数
	private AtomicInteger pingTimes = new AtomicInteger(0);
	// 用来标记是否抛出接收应答异常
	private AtomicInteger PongExceptionTrue = new AtomicInteger(0);
	// 是否执行心跳
	private boolean heartbeat = "1".equals(WebsocketConstant.HeartbeatTrue.getParameter())?true:false;
	// 心跳执行周期
	private int heartbeatCycle = Integer.parseInt(WebsocketConstant.HearbeatCycle.getParameter());
	// 准备用来锁定变量
	//private Lock lock =  new ReentrantLock();
	// wurunzhou add prameters at 20150611 for heartbeat end

	/** This open a websocket connection as specified by rfc6455 */
	public WebSocketClient( URI serverURI ) {
		this( serverURI, new Draft_17() );
	}

	/**
	 * Constructs a WebSocketClient instance and sets it to the connect to the
	 * specified URI. The channel does not attampt to connect automatically. The connection
	 * will be established once you call <var>connect</var>.
	 */
	public WebSocketClient( URI serverUri , Draft draft ) {
		this( serverUri, draft, null, 0 );
	}

	public WebSocketClient( URI serverUri , Draft protocolDraft , Map<String,String> httpHeaders , int connectTimeout ) {
		if( serverUri == null ) {
			throw new IllegalArgumentException();
		} else if( protocolDraft == null ) {
			throw new IllegalArgumentException( "null as draft is permitted for `WebSocketServer` only!" );
		}
		this.uri = serverUri;
		this.draft = protocolDraft;
		this.headers = httpHeaders;
		this.connectTimeout = connectTimeout;
		this.engine = new WebSocketImpl( this, protocolDraft );
	}

	/**
	 * Returns the URI that this WebSocketClient is connected to.
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Returns the protocol version this channel uses.<br>
	 * For more infos see https://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	 */
	public Draft getDraft() {
		return draft;
	}

	public void connect(){
		connect(1);
	}
	/**
	 * Initiates the websocket connection. This method does not block.
	 */
	public void connect(int heartbeat_) {
		
		// wurunzhou add  at 20150612 for 初始化心跳参数  begin
		if( heartbeat_ == 0){
			heartbeat = false;
		}else if(heartbeat_ == 1){
			heartbeat = true;
			// notify
		}else if( heartbeat_>1){
			heartbeat = true;
			heartbeatCycle = heartbeat_;
			// notify
		}else{
			// 小于1
			heartbeat = false;
		}
		//  wurunzhou add  at 20150612 for 初始化心跳参数  end 
		if( writeThread != null )
			throw new IllegalStateException( "WebSocketClient objects are not reuseable" );
		writeThread = new Thread( this );
		// 启动client线程
		writeThread.start();


	}

	/**
	 * Same as <code>connect</code> but blocks until the websocket connected or failed to do so.<br>
	 * Returns whether it succeeded or not.
	 **/
	public boolean connectBlocking() throws InterruptedException {
		connect(1);
		connectLatch.await();
		return engine.isOpen();
	}

	/**
	 * Initiates the websocket close handshake. This method does not block<br>
	 * In oder to make sure the connection is closed use <code>closeBlocking</code>
	 */
	public void close() {
		if( writeThread != null ) {
			engine.close( CloseFrame.NORMAL );
		}
	}

	public void closeBlocking() throws InterruptedException {
		close();
		closeLatch.await();
	}

	/**
	 * Sends <var>text</var> to the connected websocket server.
	 * 
	 * @param text
	 *            The string which will be transmitted.
	 */
	public void send( String text ) throws NotYetConnectedException {
		engine.send( text );
	}
//	/**
//	 * wurunzhou add at 20150608
//	 * <br>
//	 * for heartbeat
//	 * @throws NotYetConnectedException
//	 */
//	public void sendPing(int heartbeat_) throws NotYetConnectedException{
//		//engine.sendPing(heartbeat);
//		if( heartbeat_ == 0){
//			heartbeat = false;
//		}else if(heartbeat_ == 1){
//			heartbeat = true;
//			// notify
//		}else if( heartbeat_>1){
//			heartbeat = true;
//			heartbeatCycle = heartbeat_;
//			// notify
//		}else{
//			// 小于1
//			heartbeat = false;
//		}
//	}

	/**
	 * Sends binary <var> data</var> to the connected webSocket server.
	 * 
	 * @param data
	 *            The byte-Array of data to send to the WebSocket server.
	 */
	public void send( byte[] data ) throws NotYetConnectedException {
		engine.send( data );
	}

	/**
	 * 启动client线程
	 * <br>
	 * 1. 创建socket连接
	 * 2. 启动写线程（写线程应该是不停的从写缓存队列中取出数据发送出去）
	 */
	public void run() {
		try {
			if( socket == null ) {
				socket = new Socket( proxy );
			} else if( socket.isClosed() ) {
				throw new IOException();
			}
			if( !socket.isBound() )
				socket.connect( new InetSocketAddress( uri.getHost(), getPort() ), connectTimeout );
			istream = socket.getInputStream();
			ostream = socket.getOutputStream();

			sendHandshake();
		} catch ( /*IOException | SecurityException | UnresolvedAddressException | InvalidHandshakeException | ClosedByInterruptException | SocketTimeoutException */Exception e ) {
			onWebsocketError( engine, e );
			engine.closeConnection( CloseFrame.NEVER_CONNECTED, e.getMessage() );
			return;
		}

		writeThread = new Thread( new WebsocketWriteThread() );
		writeThread.start();

		// wurunzhou add at 20150611 for start heartbeat thread
		new Thread(new HeartbeatSendThread()).start();
		// wurunzhou add at 20150611 for start heartbeat pong receive thread
		new Thread(new HeartbeatReceiveThread()).start();
		// wurunzhou add at 20150612 end
		
		byte[] rawbuffer = new byte[ WebSocketImpl.RCVBUF ];
		int readBytes;

		try {
			// wurunzhou comment at 20150611 for 接收通道字节流，准备将其转为协议对象 
			while ( !isClosed() && ( readBytes = istream.read( rawbuffer ) ) != -1 ) {
				// 不断读取服务器消息
				engine.decode( ByteBuffer.wrap( rawbuffer, 0, readBytes ) );
			}
			engine.eot();
		} catch ( IOException e ) {
			engine.eot();
		} catch ( RuntimeException e ) {
			// this catch case covers internal errors only and indicates a bug in this websocket implementation
			onError( e );
			engine.closeConnection( CloseFrame.ABNORMAL_CLOSE, e.getMessage() );
		}
		assert ( socket.isClosed() );
	}
	private int getPort() {
		int port = uri.getPort();
		if( port == -1 ) {
			String scheme = uri.getScheme();
			if( scheme.equals( "wss" ) ) {
				return WebSocket.DEFAULT_WSS_PORT;
			} else if( scheme.equals( "ws" ) ) {
				return WebSocket.DEFAULT_PORT;
			} else {
				throw new RuntimeException( "unkonow scheme" + scheme );
			}
		}
		return port;
	}

	/**
	 * 发送握手
	 * @throws InvalidHandshakeException
	 */
	private void sendHandshake() throws InvalidHandshakeException {
		String path;
		String part1 = uri.getPath();
		String part2 = uri.getQuery();
		if( part1 == null || part1.length() == 0 )
			path = "/";
		else
			path = part1;
		if( part2 != null )
			path += "?" + part2;
		int port = getPort();
		String host = uri.getHost() + ( port != WebSocket.DEFAULT_PORT ? ":" + port : "" );

		HandshakeImpl1Client handshake = new HandshakeImpl1Client();
		handshake.setResourceDescriptor( path );
		handshake.put( "Host", host );
		if( headers != null ) {
			for( Map.Entry<String,String> kv : headers.entrySet() ) {
				handshake.put( kv.getKey(), kv.getValue() );
			}
		}
		engine.startHandshake( handshake );
	}

	/**
	 * This represents the state of the connection.
	 */
	public READYSTATE getReadyState() {
		return engine.getReadyState();
	}

	/**
	 * Calls subclass' implementation of <var>onMessage</var>.
	 */
	@Override
	public final void onWebsocketMessage( WebSocket conn, String message ) {
		onMessage( message );
	}

	@Override
	public final void onWebsocketMessage( WebSocket conn, ByteBuffer blob ) {
		onMessage( blob );
	}

	@Override
	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
		onFragment( frame );
	}
	
	

	/* (non-Javadoc)
	 * @see org.java_websocket.WebSocketAdapter#onWebsocketPing1()
	 */
	@Override
	public void onWebsocketPing1() {
		System.out.println("onWebsocketPing1 执行");
		//lock.lock();
		pongNeartTime = new Date();
		//lock.unlock();
		pingTimes.set(0);
	}

	/**
	 * Calls subclass' implementation of <var>onOpen</var>.
	 */
	@Override
	public final void onWebsocketOpen( WebSocket conn, Handshakedata handshake ) {
		connectLatch.countDown();
		onOpen( (ServerHandshake) handshake );
	}

	/**
	 * Calls subclass' implementation of <var>onClose</var>.
	 */
	@Override
	public final void onWebsocketClose( WebSocket conn, int code, String reason, boolean remote ) {
		connectLatch.countDown();
		closeLatch.countDown();
		if( writeThread != null )
			writeThread.interrupt();
		try {
			if( socket != null )
				socket.close();
		} catch ( IOException e ) {
			onWebsocketError( this, e );
		}
		onClose( code, reason, remote );
	}

	/**
	 * Calls subclass' implementation of <var>onIOError</var>.
	 */
	@Override
	public final void onWebsocketError( WebSocket conn, Exception ex ) {
		onError( ex );
	}

	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f) {
		Date currentTime = new Date();
		SimpleDateFormat dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println("onWebsocketPong"+dateString.format(currentTime));
	}

	@Override
	public final void onWriteDemand( WebSocket conn ) {
		// nothing to do
	}

	@Override
	public void onWebsocketCloseInitiated( WebSocket conn, int code, String reason ) {
		onCloseInitiated( code, reason );
	}

	@Override
	public void onWebsocketClosing( WebSocket conn, int code, String reason, boolean remote ) {
		onClosing( code, reason, remote );
	}

	public void onCloseInitiated( int code, String reason ) {
	}

	public void onClosing( int code, String reason, boolean remote ) {
	}

	public WebSocket getConnection() {
		return engine;
	}

	@Override
	public InetSocketAddress getLocalSocketAddress( WebSocket conn ) {
		if( socket != null )
			return (InetSocketAddress) socket.getLocalSocketAddress();
		return null;
	}

	@Override
	public InetSocketAddress getRemoteSocketAddress( WebSocket conn ) {
		if( socket != null )
			return (InetSocketAddress) socket.getRemoteSocketAddress();
		return null;
	}

	// ABTRACT METHODS /////////////////////////////////////////////////////////
	public abstract void onOpen( ServerHandshake handshakedata );
	public abstract void onMessage( String message );
	public abstract void onClose( int code, String reason, boolean remote );
	public abstract void onError( Exception ex );
	public void onMessage( ByteBuffer bytes ) {
	}
	public void onFragment( Framedata frame ) {
	}


	/**
	 * 写线程
	 * <br>
	 * 该线程从egine中的输出队列中去待发送数据（ByteBuffer格式）。
	 * <br>
	 * 然后将该数据通过socket输出流写出
	 * @author wurunzhou
	 *
	 */
	private class WebsocketWriteThread implements Runnable {
		
		@Override
		public void run() {
			Thread.currentThread().setName( "WebsocketWriteThread" );
			try {
				while ( !Thread.interrupted() ) {
					ByteBuffer buffer = engine.outQueue.take();
					ostream.write( buffer.array(), 0, buffer.limit() );
					ostream.flush();
				}
			} catch ( IOException e ) {
				engine.eot();
			} catch ( InterruptedException e ) {
				// this thread is regularly terminated via an interrupt
			}
		}
	}
	/**
	 * recevie 接受心跳应答控制
	 * @author wusir
	 *
	 */
	private class HeartbeatReceiveThread implements Runnable{

		@Override
		public void run() {
			
			boolean pass = true;
			
			while(pass){
				
				try {
					TimeUnit.SECONDS.sleep(120);
				} catch (InterruptedException e) {
					pass = false;
				}
				// 休眠2分钟 判断 最近一次接收pong应答的时间与当前时间差
				Date currentTime = new Date();
				int devision = (int) (currentTime.getTime() - pongNeartTime
						.getTime()) / 1000;
				// 如果当前时间和上次接受pong应答时间
				if(devision < Integer.parseInt(WebsocketConstant.HeartbeatPongCycle.getParameter())){
					// 小于 定义的 抛出异常上限时间 继续休眠等待
				}else{
					// 否则 抛出异常，结束线程。
					PongExceptionTrue.addAndGet(1);
					pass = false;
				}
			}
		}
		
	}
	
	
	/**
	 * 周期发送心跳线程
	 * @author wurunzhou
	 *
	 */
	private class HeartbeatSendThread implements Runnable{

		public HeartbeatSendThread(){
			// wurunzhou add code at 20150611 for init hearbeat premeter begin
			pingNeartTime = new Date();
			pongNeartTime = pingNeartTime;
			// wurunzhou add code at 20150611 for init hearbeat premeter end
		}
		@Override
		public void run() {
			System.out.println("启动heartbeat send Thread ");
			boolean pass = true;
			if(!heartbeat){
				// 在这里waite
				// object.w
				return ;
			}
			System.out.println("开始发送心跳");
			int checkExceptionTimes = 0;
			while(pass){
				try {
					
					// 获得当前时间
					Date currentTime = new Date();
					// 当前时间和最近一次发送心跳时间比较
					int devision = (int) (currentTime.getTime() - pingNeartTime
							.getTime()) / 1000;
					// 是否满足发送周期 否则休眠五秒
					if (devision < Integer.parseInt(WebsocketConstant.HearbeatCycle
							.getParameter())) {
						// 休眠5秒
						TimeUnit.SECONDS.sleep(Integer
								.parseInt(WebsocketConstant.SleepTime
										.getParameter()));
					} else {

						// 将心跳添加到发送队列
						if (engine.sendPing() == 1) {
							// 发送队列为空，插入心跳成功

							// 更新ping发送时间,ping次数+1
							pingNeartTime = new Date();
							// ping 次数是否超过10次
							if (pingTimes.getAndIncrement() < Integer
									.parseInt(WebsocketConstant.PingConfine
											.getParameter())) {
								// 没有超过十次
								
							} else {
								// 超过十次
								if(checkException()){
									// 有异常
									pass = false;
								}else{
									// 无异常，再发一次看一看
									if(checkExceptionTimes ++ > 10) pass = false;
								}
							}

						} else {
							// 发送队列不为空
							// 休眠五秒
							TimeUnit.SECONDS.sleep(Integer
									.parseInt(WebsocketConstant.SleepTime
											.getParameter()));
						}
					}
				}catch(InterruptedException e){
					System.out.println("休眠5秒过程出现异常");
					pass = false;
				}
				
			}

			// 心跳判断连接失效 不再发送ping 心跳
			// 启动重连

			//onWebsocketError(conn,);
			onError(new WebsocketPongResponseException());
		}
		
	}

	public void setProxy( Proxy proxy ) {
		if( proxy == null )
			throw new IllegalArgumentException();
		this.proxy = proxy;
	}

	/**
	 * 判断接受心跳应答是否有异常
	 * @return 为真表示有异常，否则表示没有异常。
	 */
	public boolean  checkException() {
		// 如果还是为0 表示没有异常
		if(PongExceptionTrue.get() == 0){
			return true;	
		}else{
			// 否则表示有接收应答异常
		}
		return false;
	}

	/**
	 * Accepts bound and unbound sockets.<br>
	 * This method must be called before <code>connect</code>.
	 * If the given socket is not yet bound it will be bound to the uri specified in the constructor.
	 **/
	public void setSocket( Socket socket ) {
		if( this.socket != null ) {
			throw new IllegalStateException( "socket has already been set" );
		}
		this.socket = socket;
	}

	@Override
	public void sendFragmentedFrame( Opcode op, ByteBuffer buffer, boolean fin ) {
		engine.sendFragmentedFrame( op, buffer, fin );
	}

	@Override
	public boolean isOpen() {
		return engine.isOpen();
	}

	@Override
	public boolean isFlushAndClose() {
		return engine.isFlushAndClose();
	}

	@Override
	public boolean isClosed() {
		return engine.isClosed();
	}

	@Override
	public boolean isClosing() {
		return engine.isClosing();
	}

	@Override
	public boolean isConnecting() {
		return engine.isConnecting();
	}

	@Override
	public boolean hasBufferedData() {
		return engine.hasBufferedData();
	}

	@Override
	public void close( int code ) {
		engine.close();
	}

	@Override
	public void close( int code, String message ) {
		engine.close( code, message );
	}

	@Override
	public void closeConnection( int code, String message ) {
		engine.closeConnection( code, message );
	}

	@Override
	public void send( ByteBuffer bytes ) throws IllegalArgumentException , NotYetConnectedException {
		engine.send( bytes );
	}

	@Override
	public void sendFrame( Framedata framedata ) {
		engine.sendFrame( framedata );
	}

	@Override
	public InetSocketAddress getLocalSocketAddress() {
		return engine.getLocalSocketAddress();
	}
	@Override
	public InetSocketAddress getRemoteSocketAddress() {
		return engine.getRemoteSocketAddress();
	}
	
	@Override
	public String getResourceDescriptor() {
		return uri.getPath();
	}
}
