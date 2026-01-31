package robotsimulator.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import robotsimulator.ui.ScreenBuilder;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Choose one of the layouts:
        Scene scene = ScreenBuilder.buildDefaultScene();  // Recommended
        
        
        primaryStage.setTitle("Robot Simulator");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}