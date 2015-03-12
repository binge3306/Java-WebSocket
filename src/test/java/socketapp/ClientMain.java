package socketapp;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.fy.socktapp.ClientFactory;
import com.fy.socktapp.FeedbackInterface;
import com.fy.socktapp.WebsocketClientInterface;

public class ClientMain {

	private WebsocketClientInterface websocketClient;
	private Queue<ByteBuffer> readList = null;
	
	
	/**
	 * 启动
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException{
		websocketClient = ClientFactory.getClientInstance("localhost", 8877);
		
		init();
		
		// 启动收发线程
		Thread readWork = new Thread(new ReadThread());
		readWork.start();
		Thread writeWork = new Thread(new WriteThread());
		writeWork.start();
		sendMsgAll(1000);
		readWork.join();
		writeWork.join();
	}

	/**
	 * 初始化连接
	 */
	public void init(){
		
		websocketClient.Connection("wurunzhou", "verifycode");
	}
	
	// 模拟不断的发送消息
	private void sendMsgAll(int i ){
		 
		while(true){
			i --;
			if(i<=0 ) break;
			
			sendMsg("message" + i);
			try {
				// 休眠发送消息
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送消息
	 * <br>
	 * 将消息放到发送队列
	 * @param msg 文本消息内容
	 */
	private void sendMsg(String msg){
		// 将消息放入发送消息缓存队列
		


	}
	
	
	
	
	
	public static void main(String [] args){
		System.out.println("begin ");
		try {
			new ClientMain().start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end");
		
	}
	
	class ReadThread implements Runnable,FeedbackInterface{

		
		
		@Override
		public void onMessageB(ByteBuffer msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessageT(ByteBuffer msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Exception e, String info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClose(Exception e, String info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void run() {

			while(true){
				// 如果队列满了 线程阻塞
			}
			
		}
		
	}
	
	/**
	 * 发送消息线程
	 * @author wurunzhou
	 *
	 */
	class WriteThread implements Runnable {

		// 发送
		@Override
		public void run() {

			while (true) {
				if (readList.isEmpty()) {
					try {
						readList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					readList.remove();
					synchronized (websocketClient) {
						ByteBuffer msgB = ByteBuffer.allocate("message"
								.getBytes().length);
						websocketClient.sendMsgText(msgB, 0);
					}
					System.out.println("输出");
				}

			}

		}

	}
	
}
