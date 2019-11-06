package application;
	
import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.opencv.core.Core;
import org.opencv.core.CvType;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

public class Main extends Application {
//	final ProgressBar[] pbs = new ProgressBar[];
//	final ProgressIndicator[] pins = new ProgressIndicator[values.length];
	
	private Stage primaryStage;
	private AnchorPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MenuUI_0.1 | Austin College");
		
		try {
			initRootLayout();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Menu_UI.fxml"));
			rootLayout = (AnchorPane) loader.load();
			
			//Show the scene containing the layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		launch(args);
	}
}
