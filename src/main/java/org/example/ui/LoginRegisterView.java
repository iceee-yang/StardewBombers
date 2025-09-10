package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.model.Player;
import org.example.service.PlayerService;
import org.example.service.impl.PlayerServiceImpl;

import java.util.function.Consumer;

public class LoginRegisterView {
    private final PlayerService service = new PlayerServiceImpl();
    private final Consumer<Player> onLoginSuccess;

    public LoginRegisterView(Consumer<Player> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void show(Stage stage) {
        TabPane tabs = new TabPane();

        // 注册
        GridPane reg = new GridPane();
        reg.setPadding(new Insets(16));
        reg.setHgap(8);
        reg.setVgap(8);
        TextField nickname = new TextField();
        TextField phone = new TextField();
        PasswordField pass = new PasswordField();
        Button btnReg = new Button("注册");
        Label regMsg = new Label();
        reg.addRow(0, new Label("昵称:"), nickname);
        reg.addRow(1, new Label("手机号:"), phone);
        reg.addRow(2, new Label("密码:"), pass);
        reg.addRow(3, btnReg, regMsg);
        btnReg.setOnAction(e -> {
            boolean ok = service.register(phone.getText().trim(), nickname.getText().trim(), pass.getText());
            regMsg.setText(ok ? "注册成功" : "注册失败(手机号重复?)");
        });

        // 登录
        GridPane log = new GridPane();
        log.setPadding(new Insets(16));
        log.setHgap(8);
        log.setVgap(8);
        TextField lphone = new TextField();
        PasswordField lpass = new PasswordField();
        Button btnLog = new Button("登录");
        Label logMsg = new Label();
        log.addRow(0, new Label("手机号:"), lphone);
        log.addRow(1, new Label("密码:"), lpass);
        log.addRow(2, btnLog, logMsg);
        btnLog.setOnAction(e -> {
            boolean ok = service.login(lphone.getText().trim(), lpass.getText());
            if (ok) {
                Player p = service.getProfile(lphone.getText().trim());
                logMsg.setText("欢迎, " + (p == null ? "玩家" : p.getNickname()));
                if (p != null && onLoginSuccess != null) {
                    onLoginSuccess.accept(p);
                }
            } else {
                logMsg.setText("账号或密码错误");
            }
        });

        tabs.getTabs().add(new Tab("注册", reg));
        tabs.getTabs().add(new Tab("登录", log));
        tabs.getTabs().forEach(t -> t.setClosable(false));

        stage.setTitle("泡泡堂 - 注册/登录");
        stage.setScene(new Scene(tabs, 420, 260));
        stage.show();
    }
}


