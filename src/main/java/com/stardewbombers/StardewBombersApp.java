package com.stardewbombers;

import com.stardewbombers.client.ui.MainMenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StardewBombersApp extends Application {

	@Override
	public void start(Stage stage) {
		MainMenuView view = new MainMenuView();
		view.setOnStart(() -> System.out.println("开始游戏被点击"));
		view.setOnSettings(() -> System.out.println("设置被点击"));
		view.setOnExit(() -> {
			System.out.println("退出被点击");
			stage.close();
		});

		Scene scene = new Scene(view, 960, 540);
		stage.setTitle("Stardew Bombers");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
