package com.stardewbombers.client.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuView extends StackPane {

	private Runnable onStart = () -> {};
	private Runnable onSettings = () -> {};
	private Runnable onExit = () -> System.exit(0);

	public MainMenuView() {
		// 背景图片
		Image bgImg = safeLoad("/assets/textures/menu_background.png");
		ImageView bgView = new ImageView(bgImg);
		bgView.setPreserveRatio(false);
		bgView.setSmooth(true);
		bgView.fitWidthProperty().bind(widthProperty());
		bgView.fitHeightProperty().bind(heightProperty());

		// 顶部标题图片
		Image titleImg = safeLoad("/assets/textures/title.png");
		ImageView titleView = new ImageView(titleImg);
		titleView.setPreserveRatio(true);
		titleView.setFitWidth(520);

		HBox titleBox = new HBox(titleView);
		titleBox.setAlignment(Pos.TOP_CENTER);
		titleBox.setPadding(new Insets(40, 0, 0, 0));

		// 底部按钮
		Button btnStart = new Button("开始");
		Button btnSettings = new Button("设置");
		Button btnExit = new Button("退出");

		String btnStyle = "-fx-background-color: rgba(0,0,0,0.55);"
				+ "-fx-text-fill: white;"
				+ "-fx-font-size: 20px;"
				+ "-fx-padding: 10 28;"
				+ "-fx-background-radius: 8;";
		btnStart.setStyle(btnStyle);
		btnSettings.setStyle(btnStyle);
		btnExit.setStyle(btnStyle);

		btnStart.setOnAction(e -> onStart.run());
		btnSettings.setOnAction(e -> onSettings.run());
		btnExit.setOnAction(e -> onExit.run());

		HBox buttons = new HBox(24, btnStart, btnSettings, btnExit);
		buttons.setAlignment(Pos.BOTTOM_CENTER);
		buttons.setPadding(new Insets(0, 0, 40, 0));

		VBox layout = new VBox();
		layout.setFillWidth(true);
		layout.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(titleBox, Priority.NEVER);
		layout.getChildren().addAll(titleBox);

		getChildren().addAll(bgView, layout, buttons);
	}

	private Image safeLoad(String path) {
		Image img;
		try {
			img = new Image(getClass().getResourceAsStream(path));
		} catch (Exception ex) {
			// 占位透明像素，避免资源缺失导致崩溃
			img = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAuMBgk8m2uYAAAAASUVORK5CYII=");
		}
		return img;
	}

	public void setOnStart(Runnable onStart) {
		this.onStart = onStart != null ? onStart : () -> {};
	}

	public void setOnSettings(Runnable onSettings) {
		this.onSettings = onSettings != null ? onSettings : () -> {};
	}

	public void setOnExit(Runnable onExit) {
		this.onExit = onExit != null ? onExit : () -> System.exit(0);
	}
}
