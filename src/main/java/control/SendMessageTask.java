package control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.concurrent.Task;

public class SendMessageTask extends Task<Integer> {
	private DatagramSocket socket;
	private String ip;
	private int port;
	private String message;
	
	@Override
	protected Integer call() throws Exception {
		byte[] b = message.getBytes();
		DatagramPacket packet = null;
		
		try{
			packet = new DatagramPacket(b, b.length, InetAddress.getByName(ip), port);
		} catch(UnknownHostException uhe) {
			System.out.println("Invalid or not found IP address");
			return 0;
		}
		
		try {
			socket.send(packet);
		} catch(IOException ioe) {
			System.out.println("Couldn't send packet");
			return 0;
		}
		
		return 1;
	}
	
	public SendMessageTask(DatagramSocket socket, String ip, int port, String message) {
		this.socket = socket;
		this.ip = ip;
		this.port = port;
		this.message = message;
	}
}
