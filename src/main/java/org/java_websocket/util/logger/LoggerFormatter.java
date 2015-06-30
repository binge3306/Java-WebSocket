package org.java_websocket.util.logger;

/**
 * wurunzhou edit at 20150303
 * 
 */
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 该类用来格式化日子信息，
 * LoggerUtil 在打日志的时候调用format函数来格式化日志信息。
 * format 函数接收一个LogRecord 对象作为参数。这个对象有所有的消息信息。
 */
public class LoggerFormatter extends Formatter {

 
	/**
	 * 格式化日志信息
	 * format是Formatter类的抽象方法。
	 * 它被LoggerUtil调用，它接收一个LogReacord 对象作为一个参数
	 */
	@Override
	public String format(LogRecord record) {
		
		/*
		 * 来用存储日志格式信息
		 */
		StringBuilder sb=new StringBuilder();
		
		/*
		 * 定义了日志信息格式
		 */
		sb.append("["+record.getLevel()+"] - ");
		sb.append(new Date(record.getMillis())+" : ");
		sb.append(record.getSourceClassName()+"."+record.getSourceMethodName()+" : ");
		sb.append(record.getMessage()+"\n");
		
		/*
		 * 转为String,然后返回
		 */
		return sb.toString();
	}
}
