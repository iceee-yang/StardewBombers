package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.component.MovementComponent;
import com.stardewbombers.component.BombComponent;
import com.stardewbombers.shared.enums.PowerUpType;
import com.stardewbombers.shared.util.SimpleMapLoader;
import com.stardewbombers.shared.util.MapLoader;
import com.stardewbombers.shared.util.GameConfig;
import com.stardewbombers.shared.game.GameManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 玩家和炸弹系统可视化测试程序
 * 在地图中显示玩家移动、炸弹放置和爆炸效果
 */
public class PlayerBombVisualTest extends Application {
    
    private static final int TILE_SIZE = 40;
    private static final int SCALE = 1; // 减小缩放，从2改为1
    // 窗口大小调整为能完整显示地图：15格×40像素=600，13格×40像素=520
    private static final int WINDOW_WIDTH = 800;   // 600 + 200边距
    private static final int WINDOW_HEIGHT = 600;  // 520 + 80边距
    
    // 游戏对象
    private GameMap gameMap;
    private GameManager gameManager;
    private List<PlayerComponent> players;
    private Map<String, Circle> playerVisuals;
    private Map<String, List<Rectangle>> bombVisuals;
    private List<Rectangle> explosionVisuals;
    
    // UI组件
    private Group root;
    private Label statusLabel;
    private Label playerInfoLabel;
    private Label bombInfoLabel;
    
    // 游戏状态
    private long lastUpdateTime;
    private boolean gameRunning;
    private String currentMapName = "farm_map";
    
    @Override
    public void start(Stage primaryStage) {
        // 初始化游戏
        initializeGame();
        
        // 创建UI
        createUI();
        
        // 设置场景
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(Color.LIGHTGRAY);
        
        // 设置键盘控制
        setupControls(scene);
        
        // 设置窗口
        primaryStage.setTitle("StardewBombers - 玩家和炸弹系统可视化测试");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // 启动游戏循环
        startGameLoop();
    }
    
    private void initializeGame() {
        // 加载Tiled地图
        try {
            gameMap = MapLoader.loadMap(currentMapName); // 使用你的Tiled地图
            System.out.println("成功加载Tiled地图: " + currentMapName + ".json");
        } catch (Exception e) {
            System.out.println("加载Tiled地图失败，使用默认地图: " + e.getMessage());
            gameMap = SimpleMapLoader.createFarmMap(); // 回退到默认地图
        }
        
        // 初始化游戏管理器
        gameManager = new GameManager();
        gameManager.setGameMap(gameMap, SCALE); // 使用缩放因子
        
        // 创建玩家
        players = new ArrayList<>();
        playerVisuals = new HashMap<>();
        bombVisuals = new HashMap<>();
        explosionVisuals = new ArrayList<>();
        
        // 创建两个测试玩家，位置适应地图大小
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        System.out.println("地图大小: " + mapWidth + "x" + mapHeight);
        
        // 玩家1：第3排第3列格子的正中央（确保在地图范围内）
        createPlayer("player1", new Point2D( (3 * TILE_SIZE + TILE_SIZE/2), (3 * TILE_SIZE + TILE_SIZE/2)), Color.BLUE);
        // 玩家2：第9排第11列格子的正中央（确保在地图范围内）
        createPlayer("player2", new Point2D( (11 * TILE_SIZE + TILE_SIZE/2),  (9 * TILE_SIZE + TILE_SIZE/2)), Color.RED);
        
        lastUpdateTime = System.currentTimeMillis();
        gameRunning = true;
    }
    
    private void createPlayer(String id, Point2D position, Color color) {
        // 创建玩家实体和组件
        Player player = new Player(id, position);
        MovementComponent movement = new MovementComponent(position, TILE_SIZE * SCALE); // 平滑移动一个格子的距离
        BombComponent bombs = new BombComponent(id, 1, 2);
        PlayerComponent playerComponent = new PlayerComponent(player, movement, bombs);
        
        players.add(playerComponent);
        
        // 将玩家添加到游戏管理器
        gameManager.addPlayer(playerComponent);
        
        // 创建玩家视觉表示
        Circle playerVisual = new Circle(TILE_SIZE / 4);
        playerVisual.setFill(color);
        playerVisual.setStroke(Color.BLACK);
        playerVisual.setStrokeWidth(2);
        playerVisual.setCenterX(position.getX());
        playerVisual.setCenterY(position.getY());
        
        playerVisuals.put(id, playerVisual);
        bombVisuals.put(id, new ArrayList<>());
    }
    
    private void createUI() {
        root = new Group();
        
        // 绘制地图背景
        drawMap();
        
        // 添加玩家视觉
        for (Circle playerVisual : playerVisuals.values()) {
            root.getChildren().add(playerVisual);
        }
        
        // 创建信息标签
        createInfoLabels();
        
        // 添加所有UI元素到根节点
        root.getChildren().addAll(statusLabel); // 只添加状态标签
        // root.getChildren().addAll(statusLabel, playerInfoLabel, bombInfoLabel); // 注释掉玩家信息和炸弹信息
    }
    
    private void drawMap() {
        // 初始化纹理管理器
        TextureManager.initialize(TILE_SIZE, TILE_SIZE);
        
        // 绘制地图背景
        Rectangle background = new Rectangle(
            gameMap.getWidth() * TILE_SIZE * SCALE,
            gameMap.getHeight() * TILE_SIZE * SCALE
        );
        background.setFill(Color.web("#D9D9D9"));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        root.getChildren().add(background);
        
        // 绘制地图方块
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                // 先绘制地板纹理
                Image floorImage = TextureManager.getImage(com.stardewbombers.shared.enums.BlockType.FLOOR);
                if (floorImage != null) {
                    ImageView floorView = new ImageView(floorImage);
                    floorView.setFitWidth(TILE_SIZE * SCALE);
                    floorView.setFitHeight(TILE_SIZE * SCALE);
                    floorView.setPreserveRatio(false);
                    floorView.setX(x * TILE_SIZE * SCALE);
                    floorView.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(floorView);
                }
                
                // 绘制其他方块
                var block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != com.stardewbombers.shared.enums.BlockType.FLOOR) {
                    Image blockImage = TextureManager.getImage(block.getType());
                    if (blockImage != null) {
                        // 使用纹理图片
                        ImageView tileView = new ImageView(blockImage);
                        tileView.setFitWidth(TILE_SIZE * SCALE);
                        tileView.setFitHeight(TILE_SIZE * SCALE);
                        tileView.setPreserveRatio(false);
                        tileView.setX(x * TILE_SIZE * SCALE);
                        tileView.setY(y * TILE_SIZE * SCALE);
                        root.getChildren().add(tileView);
                    } else {
                        // 回退到颜色方块
                        Rectangle tile = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
                        tile.setX(x * TILE_SIZE * SCALE);
                        tile.setY(y * TILE_SIZE * SCALE);
                        tile.setFill(getBlockColor(block.getType()));
                        tile.setStroke(Color.BLACK);
                        tile.setStrokeWidth(1);
                        root.getChildren().add(tile);
                    }
                }
            }
        }
        
        // 绘制网格线
        drawGridLines();
    }
    
    private Color getBlockColor(com.stardewbombers.shared.enums.BlockType type) {
        switch (type) {
            case BUSHES: return Color.GREEN;
            case PUMPKIN: return Color.ORANGE;
            case MELON: return Color.LIGHTGREEN;
            case STUMP: return Color.BROWN;
            case CABINET1:
            case CABINET2: return Color.DARKRED;
            case TABLE:
            case STOOL:
            case CHAIR: return Color.DARKGOLDENROD;
            case FIREPLACE1:
            case FIREPLACE2: return Color.RED;
            case RUG: return Color.ORANGE;
            default: return Color.GRAY;
        }
    }
    
    private void drawGridLines() {
        // 垂直线
        for (int x = 0; x <= gameMap.getWidth(); x++) {
            Rectangle line = new Rectangle(1, gameMap.getHeight() * TILE_SIZE * SCALE);
            line.setFill(Color.BLACK);
            line.setX(x * TILE_SIZE * SCALE);
            line.setY(0);
            root.getChildren().add(line);
        }
        
        // 水平线
        for (int y = 0; y <= gameMap.getHeight(); y++) {
            Rectangle line = new Rectangle(gameMap.getWidth() * TILE_SIZE * SCALE, 1);
            line.setFill(Color.BLACK);
            line.setX(0);
            line.setY(y * TILE_SIZE * SCALE);
            root.getChildren().add(line);
        }
    }
    
    private void createInfoLabels() {
        // 状态标签
        statusLabel = new Label("游戏状态: 运行中");
        statusLabel.setFont(new Font(14));
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(10);
        statusLabel.setTextFill(Color.BLUE); // 改为蓝色
        
        // 玩家信息标签 - 注释掉
        /*
        playerInfoLabel = new Label();
        playerInfoLabel.setFont(new Font(12));
        playerInfoLabel.setLayoutX(10);
        playerInfoLabel.setLayoutY(40);
        playerInfoLabel.setTextFill(Color.BLACK);
        
        // 炸弹信息标签
        bombInfoLabel = new Label();
        bombInfoLabel.setFont(new Font(12));
        bombInfoLabel.setLayoutX(10);
        bombInfoLabel.setLayoutY(200);
        bombInfoLabel.setTextFill(Color.BLACK);
        */
    }
    
    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            
            // 玩家1控制 (WASD)
            if (key == KeyCode.W) {
                players.get(0).moveUp();
            } else if (key == KeyCode.S) {
                players.get(0).moveDown();
            } else if (key == KeyCode.A) {
                players.get(0).moveLeft();
            } else if (key == KeyCode.D) {
                players.get(0).moveRight();
            } else if (key == KeyCode.SPACE) {
                players.get(0).placeBomb(System.currentTimeMillis());
            }
            
            // 玩家2控制 (方向键)
            if (players.size() > 1) {
                if (key == KeyCode.UP) {
                    players.get(1).moveUp();
                } else if (key == KeyCode.DOWN) {
                    players.get(1).moveDown();
                } else if (key == KeyCode.LEFT) {
                    players.get(1).moveLeft();
                } else if (key == KeyCode.RIGHT) {
                    players.get(1).moveRight();
                } else if (key == KeyCode.ENTER) {
                    players.get(1).placeBomb(System.currentTimeMillis());
                }
            }
            
            // 特殊控制
            if (key == KeyCode.R) {
                // 重置游戏
                resetGame();
            } else if (key == KeyCode.P) {
                // 暂停/继续
                gameRunning = !gameRunning;
            } else if (key == KeyCode.M) {
                // 切换地图
                switchMap();
            }
        });
    }
    
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    updateGame();
                    updateVisuals();
                    updateUI();
                }
            }
        };
        gameLoop.start();
    }
    
    private void updateGame() {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        
        // 使用游戏管理器更新游戏状态
        gameManager.update(currentTime);
        
        // 更新所有玩家
        for (PlayerComponent playerComponent : players) {
            // 更新移动组件
            playerComponent.getMovement().update();
            
            playerComponent.tick(currentTime);
            
            // 检查炸弹爆炸
            List<Bomb> explodedBombs = playerComponent.getBombs().tick(currentTime);
            for (Bomb bomb : explodedBombs) {
                handleBombExplosion(bomb, currentTime);
            }
        }
    }
    
    private void handleBombExplosion(Bomb bomb, long currentTime) {
        // 创建爆炸事件
        BombComponent bombComponent = null;
        for (PlayerComponent playerComponent : players) {
            if (playerComponent.getBombs().getActiveBombs().contains(bomb)) {
                bombComponent = playerComponent.getBombs();
                break;
            }
        }
        
        if (bombComponent != null) {
            List<Point2D> explosionRange = bombComponent.getExplosionRange(bomb);
            ExplosionEvent explosionEvent = new ExplosionEvent(bomb, explosionRange, currentTime);
            
            // 检查所有玩家是否在爆炸范围内
            for (PlayerComponent playerComponent : players) {
                if (playerComponent.isInExplosionRange(explosionEvent)) {
                    playerComponent.handleExplosionDamage(explosionEvent);
                }
            }
            
            // 创建爆炸视觉效果
            createExplosionVisual(explosionRange);
        }
    }
    
    private void createExplosionVisual(List<Point2D> explosionRange) {
        // 清除之前的爆炸效果
        root.getChildren().removeAll(explosionVisuals);
        explosionVisuals.clear();
        
        // 创建新的爆炸效果
        for (Point2D pos : explosionRange) {
            Rectangle explosion = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
            explosion.setX(pos.getX());
            explosion.setY(pos.getY());
            explosion.setFill(Color.YELLOW);
            explosion.setStroke(Color.ORANGE);
            explosion.setStrokeWidth(3);
            explosion.setOpacity(0.7);
            
            explosionVisuals.add(explosion);
            root.getChildren().add(explosion);
        }
        
        // 0.5秒后清除爆炸效果
        Platform.runLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                root.getChildren().removeAll(explosionVisuals);
                explosionVisuals.clear();
            });
        });
    }
    
    private void updateVisuals() {
        // 更新玩家位置
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            Circle playerVisual = playerVisuals.get(playerId);
            Point2D position = playerComponent.getPlayer().getPosition();
            
            playerVisual.setCenterX(position.getX());
            playerVisual.setCenterY(position.getY());
            
            // 更新玩家颜色（根据状态）
            if (!playerComponent.getPlayer().isAlive()) {
                playerVisual.setFill(Color.GRAY);
            } else if (playerComponent.getPlayer().getStatus() == Player.Status.INVINCIBLE) {
                playerVisual.setFill(Color.CYAN);
            }
        }
        
        // 更新炸弹视觉
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            List<Rectangle> playerBombVisuals = bombVisuals.get(playerId);
            
            // 清除旧的炸弹视觉
            root.getChildren().removeAll(playerBombVisuals);
            playerBombVisuals.clear();
            
            // 创建新的炸弹视觉
            for (Bomb bomb : playerComponent.getBombs().getActiveBombs()) {
                Rectangle bombVisual = new Rectangle(TILE_SIZE * SCALE / 2, TILE_SIZE * SCALE / 2);
                // 使用世界坐标直接渲染炸弹
                bombVisual.setX(bomb.getWorldX() - TILE_SIZE * SCALE / 4);
                bombVisual.setY(bomb.getWorldY() - TILE_SIZE * SCALE / 4);
                
                // 根据炸弹状态设置颜色
                switch (bomb.getState()) {
                    case PLACED:
                        bombVisual.setFill(Color.BLACK);
                        break;
                    case TICKING:
                        // 根据引信进度改变颜色
                        double fuseProgress = bomb.getFuseProgress();
                        if (fuseProgress < 0.3) {
                            bombVisual.setFill(Color.BLACK);
                        } else if (fuseProgress < 0.6) {
                            bombVisual.setFill(Color.DARKRED);
                        } else {
                            bombVisual.setFill(Color.RED);
                        }
                        break;
                    case EXPLODING:
                        bombVisual.setFill(Color.YELLOW);
                        break;
                    default:
                        bombVisual.setFill(Color.GRAY);
                }
                
                bombVisual.setStroke(Color.WHITE);
                bombVisual.setStrokeWidth(2);
                
                playerBombVisuals.add(bombVisual);
                root.getChildren().add(bombVisual);
            }
        }
    }
    
    private void updateUI() {
        // 更新玩家信息 - 注释掉
        /*
        StringBuilder playerInfo = new StringBuilder("玩家信息:\n");
        for (int i = 0; i < players.size(); i++) {
            PlayerComponent playerComponent = players.get(i);
            Player player = playerComponent.getPlayer();
            playerInfo.append(String.format("玩家%d (%s):\n", i + 1, player.getId()));
            playerInfo.append(String.format("  生命值: %d/%d\n", player.getHealth(), GameConfig.PLAYER_MAX_HP));
            playerInfo.append(String.format("  位置: (%.1f, %.1f)\n", player.getPosition().getX(), player.getPosition().getY()));
            playerInfo.append(String.format("  状态: %s\n", player.getStatus()));
            playerInfo.append(String.format("  炸弹数量: %d\n", player.getBombCount()));
            playerInfo.append(String.format("  炸弹威力: %d\n", player.getBombPower()));
            playerInfo.append(String.format("  是否存活: %s\n", player.isAlive()));
            playerInfo.append("\n");
        }
        playerInfoLabel.setText(playerInfo.toString());
        
        // 更新炸弹信息
        StringBuilder bombInfo = new StringBuilder("炸弹信息:\n");
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            List<Bomb> activeBombs = playerComponent.getBombs().getActiveBombs();
            bombInfo.append(String.format("%s 的炸弹:\n", playerId));
            for (Bomb bomb : activeBombs) {
                bombInfo.append(String.format("  位置: (%d, %d)\n", bomb.getX(), bomb.getY()));
                bombInfo.append(String.format("  状态: %s\n", bomb.getState()));
                bombInfo.append(String.format("  引信时间: %.2f/%.2f\n", bomb.getFuseTime(), bomb.getTotalFuseTime()));
                bombInfo.append(String.format("  引信进度: %.1f%%\n", bomb.getFuseProgress() * 100));
                bombInfo.append(String.format("  爆炸范围: %d\n", bomb.getExplosionRadius()));
            }
            if (activeBombs.isEmpty()) {
                bombInfo.append("  无活跃炸弹\n");
            }
            bombInfo.append("\n");
        }
        bombInfoLabel.setText(bombInfo.toString());
        */
        
        // 更新状态
        statusLabel.setText(String.format("游戏状态: %s | 当前地图: %s | 时间: %d", 
            gameRunning ? "运行中" : "暂停", currentMapName, System.currentTimeMillis()));
    }
    
    private void resetGame() {
        // 重置所有玩家
        for (PlayerComponent playerComponent : players) {
            Player player = playerComponent.getPlayer();
            playerComponent.getPlayer().setPosition(new Point2D(
                player.getId().equals("player1") ? 3 * TILE_SIZE + TILE_SIZE/2 : 9 * TILE_SIZE + TILE_SIZE/2,
                player.getId().equals("player1") ? 3 * TILE_SIZE + TILE_SIZE/2 : 11 * TILE_SIZE + TILE_SIZE/2
            ));
            // 重置生命值（这里需要重新创建玩家，因为生命值是final的）
        }
        
        // 清除所有炸弹
        for (PlayerComponent playerComponent : players) {
            playerComponent.getBombs().clearAllBombs();
        }
        
        // 清除爆炸效果
        root.getChildren().removeAll(explosionVisuals);
        explosionVisuals.clear();
        
        // 重新初始化游戏
        initializeGame();
    }
    
    private void switchMap() {
        // 切换地图
        if (currentMapName.equals("farm_map")) {
            currentMapName = "home_map";
        } else {
            currentMapName = "farm_map";
        }
        
        System.out.println("切换到地图: " + currentMapName);
        
        // 重新加载地图
        try {
            gameMap = MapLoader.loadMap(currentMapName);
            System.out.println("成功加载Tiled地图: " + currentMapName + ".json");
        } catch (Exception e) {
            System.out.println("加载Tiled地图失败，使用默认地图: " + e.getMessage());
            gameMap = SimpleMapLoader.createFarmMap();
        }
        
        // 重新创建UI
        root.getChildren().clear();
        createUI();
        
        // 重新创建玩家
        players.clear();
        playerVisuals.clear();
        bombVisuals.clear();
        explosionVisuals.clear();
        
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        // 玩家1：第4排第4列格子的正中央（数组索引：第3排第3列）
        createPlayer("player1", new Point2D(3 * TILE_SIZE + TILE_SIZE/2, 3 * TILE_SIZE + TILE_SIZE/2), Color.BLUE);
        // 玩家2：第12排第10列格子的正中央（数组索引：第11排第9列）
        createPlayer("player2", new Point2D(9 * TILE_SIZE + TILE_SIZE/2, 11 * TILE_SIZE + TILE_SIZE/2), Color.RED);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
