package communicator.controller;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.JFXButton;

import communicator.AppMain;
import control.ListenOnPortTask;
import control.TaskHelper;
import control.lastChatter;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AppMainController {
	private static final String FXML_CHAT_FORM_TEMPLATE = "/fxml/chat.fxml";
	private static DatagramSocket socket;
	
	private String ipAddress;
	private int portNumber = 8000;
	
	private Map<String, ChatController> mapOfChats = new HashMap<>();
	
	@FXML
	private Label yourIPLabel;
	
	@FXML
	private Label yourPortLabel;
	
	@FXML
	private TextField IPTextField;
	
	@FXML
	private TextField portTextField;
	
	@FXML
	private JFXButton newMessageButton;
	
	@FXML
	private void initialize() {
		setIPAndPort();
		
		setUpPortListener();
		
		newMessageButton.setOnMouseClicked((MouseEvent e) -> {
			createChatWindow(IPTextField.getText(), Integer.parseInt(portTextField.getText()));
		});
	}
	
	private void createChatWindow(String friendsIP, int friendsPort) {
		ChatController chatController = new ChatController(socket, friendsIP, friendsPort);
		
		String mapKey = friendsIP + ":" + Integer.toString(friendsPort);
		if(mapOfChats.containsKey(mapKey))
			mapOfChats.remove(mapKey);
		
		mapOfChats.put(mapKey, chatController);
		
		ChatWindow chatWindow = new ChatWindow(friendsIP, Integer.toString(friendsPort), chatController);
		
		try {
			chatWindow.create();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ChatWindow{
		private static final String LOGO = "/icons/envelope.png";
		private String toIP;
		private String toPort;
		private Object controller;
		
		public void create() throws IOException {
			FXMLLoader loader =  new FXMLLoader(AppMain.class.getResource(FXML_CHAT_FORM_TEMPLATE));
			loader.setController(controller);
			
			Scene scene = new Scene(loader.load());
			Stage ns = new Stage();
			
			ns.setOnCloseRequest(e -> {
				mapOfChats.remove(toIP + ":" + toPort);
			});
			
			ns.setScene(scene);
			ns.show();
			
			ns.getIcons().add(new Image(LOGO));
			ns.resizableProperty().set(false);
			ns.setTitle("From: " + ipAddress + ":" + portNumber + " To: " +
									toIP + ":" + toPort);
		}
		
		public ChatWindow(String toIP, String toPort, Object controller) {
			this.toIP = toIP;
			this.toPort = toPort;
			this.controller = controller;
		}
	}
	
	private void setIPAndPort() {
		try{
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException uhe) {
			System.out.println("Didn't find Local Host.");
			yourIPLabel.setText("Unknown");
		}
		
		yourIPLabel.setText(ipAddress);
		
		// Looking for not taken port
		// We assume there's at least one
		boolean flag = true;
		DatagramSocket ds = null;
		
		while(flag) {
			flag = false;
		
			try{
				ds = new DatagramSocket(portNumber);
			} catch(SocketException se) {
				flag = true;
				portNumber++;
			}
		}

		yourPortLabel.setText(Integer.toString(ds.getLocalPort()));
		socket = ds;
	}
	
	private void setUpPortListener() {
		Task<String> listenTask = new ListenOnPortTask(socket);
		
		listenTask.setOnSucceeded(e -> {
			String[] result = listenTask.getValue().split("\\n");
			
			if(result.length == 2) {
				if(!mapOfChats.containsKey(result[0]))
					createChatWindow(result[0].split(":")[0], Integer.parseInt(result[0].split(":")[1]));
				
				Toolkit.getDefaultToolkit().beep();
				
				mapOfChats.get(result[0]).addTextToChatBox(result[1], lastChatter.FRIEND);
			}
			
			setUpPortListener();
		});
		
		TaskHelper.start(listenTask);
	}
	
	public void closeSocket() {
		socket.close();
	}
}
