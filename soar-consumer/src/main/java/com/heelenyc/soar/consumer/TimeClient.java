package com.heelenyc.soar.consumer;

/**
 * @author yicheng
 * @since 2014年6月28日
 * 
 */
public class TimeClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int port = 8080;
		// for (int i = 0; i < 5; i++) {
		// new Thread(new TimeClientHandle("127.0.0.1", port),
		// "NIO-TimeClient"+i).start();
		// }
		new Thread(new TimeClientHandle("127.0.0.1", port), "NIO-TimeClient001")
				.start();
	}

}
