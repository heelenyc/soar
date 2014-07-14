package com.heelenyc.soar.consumer.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yicheng
 * @since 2014年6月28日
 *
 */
public class TimeClientHandle implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;
	
	public TimeClientHandle(String host,int port){
		this.host = host == null ? "localhost" : host;
		this.port = port;
		
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void stop() {
		this.stop = true;
	}
	
	@Override
	public void run() {
		try {
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				SelectionKey key = null;
				
				while(iterator.hasNext()){
					key = iterator.next();
					try {
						handleInput(key);
					} catch (Exception e) {
						e.printStackTrace();
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
					iterator.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param key
	 * @throws IOException 
	 */
	private void handleInput(SelectionKey key) throws IOException {

		if (key.isValid()) {
			SocketChannel sc = (SocketChannel) key.channel();
			
			if (key.isConnectable()) {
				if (sc.finishConnect()) {
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				}else {
					System.out.println("can not finish connect ,exit!");
					System.exit(1);
				}
			}
			
			if (key.isReadable()) {
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF-8");
					System.out.println("Now is :" + body);
					stop();
				}else if (readBytes < 0) {
					key.channel();
					sc.close();
				}else {
					
				}
;			}
		}
	}
	/**
	 * @throws IOException 
	 * 
	 */
	private void doConnect() throws IOException {
		if (socketChannel.connect(new InetSocketAddress(host,port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	/**
	 * @param socketChannel2
	 * @throws IOException 
	 * 
	 */
	private void doWrite(SocketChannel socketChannel) throws IOException {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		socketChannel.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {
			System.out.println("Send order to server succeed!");
		}
	}

}
