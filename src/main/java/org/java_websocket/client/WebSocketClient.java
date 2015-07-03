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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.ExceptionErrorCode;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.TimeQueue;
import org.java_websocket.util.logger.LoggerUtil;

/**
 * A subclass must implement at least <var>onOpen</var>, <var>onClose</var>, and <var>onMessage</var> to be
 * useful. At runtime the user is expected to establish a connection via {@link #connect()}, then receive events like {@link #onMessage(String)} via the overloaded methods and to {@link #send(String)} data to the server.
 */
public abstract class WebSocketClient extends WebSocketAdapter implements Runnable, WebSocket {

//	/**
//	 * 为了测试的方便添加该变量用来表示这是模拟的第几个线程
//	 */
//	private static int ThreadNUM = 0;
	/**
	 * 日志
	 */
	private Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	/**
	 * The URI this channel is supposed to connect to.
	 */
	protected URI uri = null;

	private WebSocketImpl engine = null;

	private Socket socket = null;

	private InputStream istream;

	private OutputStream ostream;

	private Proxy proxy = Proxy.NO_PROXY;

	private Thread workThread;
	
	private Thread writeThread;

	private Draft draft;

	private Map<String,String> headers;

	private CountDownLatch connectLatch = new CountDownLatch( 1 );

	private CountDownLatch closeLatch = new CountDownLatch( 1 );

	private int connectTimeout = 0;
	
	// wurunzhou  add parameter  save sendTime  begin
	/**
	 * 为了确认消息收发时间，来确认消息系统的效率。
	 */
	private BlockingQueue<Date> outQueueTime;
	
	//收到回复 才会放闸 让你计算 延时时间。
	private TimeQueue timequeue;
	// wurunzhou  add parameter  save sendTime  end 

	/** This open a websocket connection as specified by rfc6455 */
	public WebSocketClient( URI serverURI ) {
		this( serverURI, new Draft_17() );
	}
	
	private int USERID = 0;
	public WebSocketClient( URI serverURI,int userid ) {
		this( serverURI, new Draft_17() );
		this.USERID = userid;
		outQueueTime = new LinkedBlockingQueue<Date>();
		timequeue = new TimeQueue();
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
		connect(0);
	}
	/**
	 * Initiates the websocket connection. This method does not block.
	 */
	public void connect(int heartbeat_) {
		
		logger.log(Level.INFO, "connect function");
		if( workThread != null )
			throw new IllegalStateException( "WebSocketClient objects are not reuseable" );
		workThread = new Thread( this);
		// 启动client线程
		//workThread.setDaemon();
		workThread.start();


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
	 * Initiates the websocket close handshake. 
	 * This method does not block<br>
	 * In oder to make sure the connection is closed
	 * use <code>closeBlocking</code>
	 */
	public void close() {
		logger.log(Level.INFO,"close function");
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
		Thread.currentThread().setName("workMain");
		logger.log(Level.INFO,"dealMainThread start");

			Thread.currentThread().setName("WebsocketReceiveThread" +USERID);
			logger.log(Level.INFO,"begin 线程");
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
			writeThread.setDaemon(true);
			writeThread.start();
			
			writeThread = new Thread(new DelayDealThread());
			writeThread.setDaemon(true);
			writeThread.start();
			byte[] rawbuffer = new byte[ WebSocketImpl.RCVBUF ];
			int readBytes;

			try {
				// wurunzhou comment at 20150611 for 接收通道字节流，准备将其转为协议对象 
				while ( !isClosed() && ( readBytes = istream.read( rawbuffer ) ) != -1&&(!Thread.currentThread().isInterrupted()) ) {
					// 不断读取服务器消息
					engine.decode( ByteBuffer.wrap( rawbuffer, 0, readBytes ) );
					//logger.log(Level.INFO,"-");
				}
				engine.eot();
			} catch ( IOException e ) {
				engine.eot();
				logger.log(Level.SEVERE,"--异常"+e.toString());
			} catch ( RuntimeException e ) {
				// this catch case covers internal errors only and indicates a bug in this websocket implementation
				onError( e );
				engine.closeConnection( CloseFrame.ABNORMAL_CLOSE, e.getMessage() );
				logger.log(Level.SEVERE,"----异常"+e.toString());
			}
			assert ( socket.isClosed() );
			logger.log(Level.WARNING,"4444444-websocketClient");
	
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
		logger.log(Level.INFO,"startHandshake");
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
		if("msg__OK".equals(message)){
			timequeue.put(new Date());
			
		}else{
			onMessage( message );
		}
	}

	@Override
	public final void onWebsocketMessage( WebSocket conn, ByteBuffer blob ) {
		onMessage( blob );
	}

	@Override
	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
		onFragment( frame );
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
		outQueueTime.clear();
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
		
		logger.log(Level.INFO,"收到心跳应答，onWebsocketPong at"+dateString.format(currentTime));
		//lock.lock();

	}

	@Override
	public final void onWriteDemand( WebSocket conn ) {
		// 待发送消息已经放到发送队列中了
		
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
			Thread.currentThread().setName( "WebsocketWriteThread"+USERID);
			try {
				while ( !Thread.interrupted() ) {
					ByteBuffer buffer = engine.outQueue.take();
					ostream.write( buffer.array(), 0, buffer.limit() );
					ostream.flush();
					outQueueTime.add(new Date());
				}
			} catch ( IOException e ) {
				engine.eot();
			} catch ( InterruptedException e ) {
				// this thread is regularly terminated via an interrupt
				logger.log(Level.SEVERE,"websocketWriteThread be interrupted");
			}
			logger.log(Level.WARNING,"44444444444wesocketWriteThread");
		}
		
	}
	
	private class DelayDealThread implements Runnable{

		@Override
		public void run() {
			Thread.currentThread().setName( "DelayDealThread"+USERID);
			boolean pass = true;
			while ( !Thread.interrupted()&&pass ) {

				Date current  = timequeue.get();
				if(outQueueTime.isEmpty()){
					continue;
				}
				try {

					if(outQueueTime.size() > 60)
						onError(new InvalidDataException(ExceptionErrorCode.OutQueTimeOverFlow.getErrorCode()));
					Date date1 = outQueueTime.take();
					
					long times = current.getTime()-date1.getTime();
					logger.log(Level.INFO,Thread.currentThread().getName()+" 消息延时时间 "+times  +" (毫秒)");
				} catch (InterruptedException e) {

					logger.log(Level.SEVERE,"处理确认消息发送成功，发生异常" + e.toString());
					pass = false;
				}
			}
		}
		
	}

	public void setProxy( Proxy proxy ) {
		if( proxy == null )
			throw new IllegalArgumentException();
		this.proxy = proxy;
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
