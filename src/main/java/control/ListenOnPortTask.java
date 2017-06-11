package control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javafx.concurrent.Task;

public class ListenOnPortTask extends Task<String> {
	private DatagramSocket ds;

	@Override
	protected String call() throws Exception {
		DatagramPacket dp;
		
		byte[] receivedMessage = new byte[1000];
		dp = new DatagramPacket(receivedMessage, receivedMessage.length);

		try {
			ds.receive(dp);
		} catch (IOException ioe) {
			System.out.println("Error during receiving message in Port Listener");
			System.out.println("Application probably was closed");
		}
		
		String receivedMessageString = new String(dp.getData());
		receivedMessageString.trim();
		
		return dp.getAddress().getHostAddress() + ":" + Integer.toString(dp.getPort()) +
							"\n" + receivedMessageString;
				  
	}
		
	public ListenOnPortTask(DatagramSocket socket){
		ds = socket;
	}
}
