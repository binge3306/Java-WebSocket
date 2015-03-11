package socketapp;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import com.fy.socktapp.ClientFactory;
import com.fy.socktapp.FeedbackInterface;
import com.fy.socktapp.WebsocketClientInterface;

public class ClientMain {

	private WebsocketClientInterface websocketClient;

	
	
	/**
	 * 启动
	 */
	public void start(){
		websocketClient = ClientFactory.getClientInstance("", 8877);
		
		init();
		
		Thread readWork = new Thread(new ReadThread());
		readWork.start();
		
	}
	
	/**
	 * 初始化连接
	 */
	public void init(){
		
		websocketClient.Connection("wurunzhou", "verifycode");
	}
	
	
	public static void main(String [] args){
		System.out.println("begin ");
		new ClientMain().start();
		
	}
	
	class ReadThread implements Runnable,FeedbackInterface{

		private Queue<ByteBuffer> readList = null;
		
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
				if(readList.isEmpty()){
					try {
						readList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					readList.remove();
					System.out.println("输出");
				}
				
			}
			
		}
		
	}
	
}
