# StardewBombers
Project for 2025 CSU-SE Java traning
## 一、项目架构
    ···
    BombFarmGame/  
    ├── pom.xml                           # Maven配置文件  
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/bombfarm/
    │   │   │       ├── BombFarmApp.java  # 主启动类
    │   │   │       ├── client/           # 客户端模块
    │   │   │       │   ├── GameClient.java
    │   │   │       │   ├── ui/           # UI界面
    │   │   │       │   │   ├── MainMenuView.java
    │   │   │       │   │   ├── GameView.java
    │   │   │       │   │   ├── FarmView.java
    │   │   │       │   │   └── LobbyView.java
    │   │   │       │   ├── controller/   # 客户端控制器
    │   │   │       │   │   ├── GameController.java
    │   │   │       │   │   ├── PlayerController.java
    │   │   │       │   │   └── FarmController.java
    │   │   │       │   └── network/      # 网络通信
    │   │   │       │       ├── NetworkClient.java
    │   │   │       │       └── MessageHandler.java
    │   │   │       ├── server/           # 服务端模块
    │   │   │       │   ├── GameServer.java
    │   │   │       │   ├── network/
    │   │   │       │   │   ├── NetworkServer.java
    │   │   │       │   │   └── ClientHandler.java
    │   │   │       │   ├── game/
    │   │   │       │   │   ├── GameRoom.java
    │   │   │       │   │   ├── GameLogic.java
    │   │   │       │   │   └── CollisionDetector.java
    │   │   │       │   └── database/
    │   │   │       │       ├── DatabaseManager.java
    │   │   │       │       └── PlayerDataDAO.java
    │   │   │       ├── shared/           # 共享模块
    │   │   │       │   ├── entity/       # 游戏实体
    │   │   │       │   │   ├── Player.java
    │   │   │       │   │   ├── Bomb.java
    │   │   │       │   │   ├── PowerUp.java
    │   │   │       │   │   ├── Block.java
    │   │   │       │   │   └── farm/
    │   │   │       │   │       ├── Crop.java
    │   │   │       │   │       ├── Seed.java
    │   │   │       │   │       └── Farm.java
    │   │   │       │   ├── protocol/     # 网络协议
    │   │   │       │   │   ├── Message.java
    │   │   │       │   │   ├── GameMessage.java
    │   │   │       │   │   ├── FarmMessage.java
    │   │   │       │   │   └── MessageType.java
    │   │   │       │   ├── enums/        # 枚举类
    │   │   │       │   │   ├── GameState.java
    │   │   │       │   │   ├── PowerUpType.java
    │   │   │       │   │   ├── CropType.java
    │   │   │       │   │   └── BlockType.java
    │   │   │       │   └── util/         # 工具类
    │   │   │       │       ├── GameConfig.java
    │   │   │       │       ├── MapLoader.java
    │   │   │       │       └── JsonUtil.java
    │   │   │       └── component/        # FXGL组件
    │   │   │           ├── PlayerComponent.java
    │   │   │           ├── BombComponent.java
    │   │   │           ├── FarmComponent.java
    │   │   │           └── MovementComponent.java
    │   │   └── resources/
    │   │       ├── assets/               # 游戏资源
    │   │       │   ├── textures/         # 贴图资源
    │   │       │   │   ├── player/
    │   │       │   │   ├── bomb/
    │   │       │   │   ├── blocks/
    │   │       │   │   └── crops/
    │   │       │   ├── sounds/           # 音效资源
    │   │       │   └── maps/             # 地图文件
    │   │       │       └── default_map.json
    │   │       ├── fxml/                 # FXML布局文件
    │   │       │   ├── main_menu.fxml
    │   │       │   ├── game_view.fxml
    │   │       │   └── farm_view.fxml
    │   │       ├── css/                  # 样式文件
    │   │       │   └── game_style.css
    │   │       └── application.properties
    │   └── test/
    │       └── java/
    │           └── com/bombfarm/
    │               ├── GameLogicTest.java
    │               ├── FarmSystemTest.java
    │               └── NetworkTest.java
    ···

## 二、接口设计
1. 游戏核心接口
  - 游戏状态管理接口IGameState
  - 玩家管理接口IPlayerManager
  - 炸弹系统接口IBombSystem
  - 碰撞检测接口ICollisionDetector
2. 农场系统接口
3. 网络通信接口
  - 网络客户端接口INetworkClient
  - 网络服务器端接口INetworkServer
  - 消息处理接口IMessageHandler
4. 数据持久化接口
5. UI界面接口
  - 游戏视图接口IGameView
  - 农场视图接口IFarmView
