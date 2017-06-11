package communicator;

import communicator.controller.AppMainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppMain extends Application {
	private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/communicator-main.fxml";
	private static final String LOGO = "/icons/envelope.png";
	private AppMainController mainController = new AppMainController();

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader =  new FXMLLoader(AppMain.class.getResource(FXML_MAIN_FORM_TEMPLATE));
		loader.setController(mainController);
		
		Parent root = loader.load();
		
		primaryStage.getIcons().add(new Image(LOGO));
		primaryStage.resizableProperty().set(false);
		primaryStage.setTitle("Communicator");
		
		primaryStage.setOnCloseRequest(e ->{
			mainController.closeSocket();
		});
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		Platform.setImplicitExit(true); 
		
		launch(args);
	}
}
