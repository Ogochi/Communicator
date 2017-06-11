package communicator.controller;

import java.net.DatagramSocket;

import control.SendMessageTask;
import control.TaskHelper;
import control.lastChatter;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ChatController {
	private DatagramSocket socket;
	private String IP;
	private int port;
	private lastChatter previous = lastChatter.NONE;
	
	@FXML
	private TextArea chatBoxTextArea;
	
	@FXML
	private TextField newMessageTextField;
	
	@FXML
	private void initialize() {
		newMessageTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ENTER)) {
				sendMessage(newMessageTextField.getText());

				newMessageTextField.clear();
			}	
		});
	}
	
	private void sendMessage(String message) {
		Task<Integer> sendTask = new SendMessageTask(socket, IP, port, message);
		
		sendTask.setOnSucceeded(e -> {
			if(sendTask.getValue() == 1)
				addTextToChatBox(message, lastChatter.YOU);
			else
				addTextToChatBox("Couldn't send message!\n", lastChatter.NONE);
		});
		
		TaskHelper.start(sendTask);
	}
	
	public void addTextToChatBox(String text, lastChatter who) {
		StringBuilder sb = new StringBuilder();
		
		switch(previous) {
			case FRIEND:
				if(who == lastChatter.YOU)
					sb.append("[YOU]\n");
				break;
			case NONE:
				if(who == lastChatter.YOU)
					sb.append("[YOU]\n");
				
				if(who == lastChatter.FRIEND)
					sb.append("[FRIEND]\n");
				break;
			case YOU:
				if(who == lastChatter.FRIEND)
					sb.append("[FRIEND]\n");
				break;
		}
		
		sb.append(text);
		sb.append("\n");
		
		chatBoxTextArea.appendText(sb.toString());
		
		previous = who;
	}
	
	public ChatController(DatagramSocket socket, String IP, int port) {
		this.socket = socket;
		this.IP = IP;
		this.port = port;
	}
}
