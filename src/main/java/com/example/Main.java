package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        

        // Build the scene using ScreenBuilder
        Scene scene = ScreenBuilder.buildScene();
        
        primaryStage.setTitle("Robot Simulator GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}