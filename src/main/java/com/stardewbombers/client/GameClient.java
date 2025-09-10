package com.stardewbombers.client;

import com.stardewbombers.client.network.NetworkClient;
import com.stardewbombers.client.network.ClientMessageHandler;
import com.stardewbombers.client.controller.GameController;

public class GameClient {
    private NetworkClient networkClient;
    private GameController gameController;

    public void start() {
        gameController = new GameController();
        networkClient = new NetworkClient();

        ClientMessageHandler messageHandler = new ClientMessageHandler(gameController);
        networkClient.setMessageHandler(messageHandler);

        // 连接到服务器
        networkClient.connect("localhost", 8888);
    }

    public static void main(String[] args) {
        GameClient client = new GameClient();
        client.start();
    }
}
