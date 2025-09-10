package org.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.Player;
import org.example.service.PlayerService;
import org.example.service.impl.PlayerServiceImpl;

public class GameHomeView {
    private final PlayerService service = new PlayerServiceImpl();
    private final Player player;

    public GameHomeView(Player player) {
        this.player = player;
    }

    public void show(Stage stage) {
        GridPane root = new GridPane();
        root.setPadding(new Insets(16));
        root.setHgap(10);
        root.setVgap(10);
        root.setAlignment(Pos.TOP_LEFT);

        Label hello = new Label("欢迎 " + (player.getNickname() == null ? "玩家" : player.getNickname()));
        root.add(hello, 0, 0, 4, 1);

        // 行: 种子1/2/3 与作物1/2/3 的显示与增减
        addCounterRow(root, 1, "种子1", () -> service.getSeed1(player.getPhone()), d -> service.addSeed1(player.getPhone(), d));
        addCounterRow(root, 2, "种子2", () -> service.getSeed2(player.getPhone()), d -> service.addSeed2(player.getPhone(), d));
        addCounterRow(root, 3, "种子3", () -> service.getSeed3(player.getPhone()), d -> service.addSeed3(player.getPhone(), d));
        addCounterRow(root, 4, "作物1", () -> service.getCrop1(player.getPhone()), d -> service.addCrop1(player.getPhone(), d));
        addCounterRow(root, 5, "作物2", () -> service.getCrop2(player.getPhone()), d -> service.addCrop2(player.getPhone(), d));
        addCounterRow(root, 6, "作物3", () -> service.getCrop3(player.getPhone()), d -> service.addCrop3(player.getPhone(), d));

        stage.setScene(new Scene(root, 560, 360));
        stage.setTitle("泡泡堂 - 主界面(测试计数)");
        stage.show();
    }

    private interface IntSupplier { int get(); }
    private interface IntConsumer { boolean apply(int delta); }

    private void addCounterRow(GridPane grid, int row, String title, IntSupplier getter, IntConsumer updater) {
        Label name = new Label(title + ": ");
        Label value = new Label(String.valueOf(getter.get()));
        Button minus = new Button("-1");
        Button plus = new Button("+1");
        Button refresh = new Button("刷新");

        minus.setOnAction(e -> { if (updater.apply(-1)) value.setText(String.valueOf(getter.get())); });
        plus.setOnAction(e -> { if (updater.apply(+1)) value.setText(String.valueOf(getter.get())); });
        refresh.setOnAction(e -> value.setText(String.valueOf(getter.get())));

        grid.add(name, 0, row);
        grid.add(value, 1, row);
        grid.add(minus, 2, row);
        grid.add(plus, 3, row);
        grid.add(refresh, 4, row);
    }
}



