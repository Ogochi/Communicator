package control;

import javafx.concurrent.Task;

public class TaskHelper {
	@SuppressWarnings("rawtypes")
	public static void start(Task t) {
		Thread th = new Thread(t);
        th.setDaemon(true);
        
        th.start();
	}
}
