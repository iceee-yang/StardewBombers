package com.stardewbombers;

import javafx.application.Application;
import javafx.stage.Stage;

public class StardewBombersApp extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Stardew Bombers");
		stage.show();
	}
}
