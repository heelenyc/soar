package com.heelenyc.soar.provider.nio;


/**
 * @author yicheng
 * @since 2014年6月28日
 *
 */
public class TimeServer {
	public static void main(String args[]){
		int port = 8080;
		
		MultiplexerTimeServer server = new MultiplexerTimeServer(port);
		
		new Thread(server,"NIO-MultiplexerTimeServer-001").start();
	}
}
