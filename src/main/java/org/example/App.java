package org.example;

import javafx.stage.Stage;
import javafx.application.Application;
import org.example.model.Player;
import org.example.ui.GameHomeView;
import org.example.ui.LoginRegisterView;

public class App extends Application{
    public static void main( String[] args )
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage)throws Exception{
        new LoginRegisterView(player -> switchToGame(primaryStage, player)).show(primaryStage);
    }

    private void switchToGame(Stage stage, Player player) {
        new GameHomeView(player).show(stage);
    }

}
