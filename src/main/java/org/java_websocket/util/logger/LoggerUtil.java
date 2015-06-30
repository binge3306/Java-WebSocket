package org.java_websocket.util.logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author wurunzhou edit at 20150303
 * <br>
 * 该类用来创建一个Logger 对象
 * <br>
 * 通过该类你可以将应用程序中产生的所有被格式化的日志消息输出到指定文件（fysocket+Time.log）。
 * <br>
 * 通过该类来创建logger对象，创建logger对象的时候需要传入类名作为参数。
 *
 */
public class LoggerUtil {
	
 
	/**
	 * Hander 使用来控制日志输出位置 （文件 fysocket+Time.log ）
	 */
	private static Handler handler;
	
	
	/**
	 * 该静态方法返回一个log 对象，该对象获取调用者类名作为一个参数。
	 * 如果这是一个Logger对象，该方法设置配置。
	 * 
	 * @param name Logger对象名
	 * @return Logger 对象
	 */
	public static Logger getLogger(String name){
 
		
		Logger logger=Logger.getLogger(name);
		/*
		 * Set the level to show all the messages
		 */
		logger.setLevel(Level.ALL);
		try {
			/*
			 * If the Handler object is null, we create one to
			 * write the log messages in the recipe8.log file
			 * with the format specified by the MyFormatter class
			 */
			if (handler==null) {
				
				String loggerFileName = "fysocket"+ getConcurrentTime() +".log";
				handler=new FileHandler(loggerFileName);
				Formatter format=new LoggerFormatter();
				handler.setFormatter(format);
			}
			/*
			 * If the Logger object hasn't handler, we add the Handler object
			 * to it
			 */
			if (logger.getHandlers().length==0) {
				logger.addHandler(handler);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
		}
		
		/*
		 * Return the Logger object.
		 */
		return logger;
	}
	
	private static String getConcurrentTime(){
	      Date dNow = new Date( );
	      SimpleDateFormat ft = 
	      new SimpleDateFormat ("yyyyMMdd-hhmmss");
	      return ft.format(dNow);
	}
	
}
