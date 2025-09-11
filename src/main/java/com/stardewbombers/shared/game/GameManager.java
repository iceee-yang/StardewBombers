package com.stardewbombers.shared.game;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.server.game.CollisionDetector;
import java.util.List;
import java.util.ArrayList;

/**
 * 游戏管理器
 * 处理爆炸事件、伤害计算、碰撞检测等游戏逻辑
 */
public class GameManager {
    private final List<PlayerComponent> players = new ArrayList<>();
    private final List<ExplosionEvent> activeExplosions = new ArrayList<>();
    private final int gridSize = 50;
    private GameMap gameMap;
    private CollisionDetector collisionDetector;

    /**
     * 设置游戏地图
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        this.collisionDetector = new CollisionDetector(gameMap);
        
        // 为所有现有玩家设置碰撞检测器
        for (PlayerComponent player : players) {
            player.getMovement().setCollisionDetector(collisionDetector);
        }
    }
    
    /**
     * 设置游戏地图（带缩放）
     */
    public void setGameMap(GameMap gameMap, double scale) {
        this.gameMap = gameMap;
        this.collisionDetector = new CollisionDetector(gameMap, scale);
        
        // 为所有现有玩家设置碰撞检测器
        for (PlayerComponent player : players) {
            player.getMovement().setCollisionDetector(collisionDetector);
        }
    }

    /**
     * 获取游戏地图
     */
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * 获取碰撞检测器
     */
    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    /**
     * 添加玩家
     */
    public void addPlayer(PlayerComponent playerComponent) {
        players.add(playerComponent);
        
        // 为新玩家设置碰撞检测器
        if (collisionDetector != null) {
            playerComponent.getMovement().setCollisionDetector(collisionDetector);
        }
    }

    /**
     * 移除玩家
     */
    public void removePlayer(PlayerComponent playerComponent) {
        players.remove(playerComponent);
    }

    /**
     * 处理爆炸事件
     */
    public void handleExplosion(Bomb bomb) {
        // 创建爆炸事件
        List<Point2D> affectedPositions = calculateExplosionRange(bomb);
        ExplosionEvent explosionEvent = new ExplosionEvent(bomb, affectedPositions, System.currentTimeMillis());
        activeExplosions.add(explosionEvent);

        // 处理对玩家的伤害
        for (PlayerComponent playerComponent : players) {
            playerComponent.handleExplosionDamage(explosionEvent);
        }

        // 处理对地图方块的破坏
        if (gameMap != null) {
            handleMapDamage(explosionEvent);
        }
    }

    /**
     * 处理爆炸对地图方块的破坏
     */
    private void handleMapDamage(ExplosionEvent explosionEvent) {
        for (Point2D pos : explosionEvent.getAffectedPositions()) {
            // 将世界坐标转换为网格坐标
            int gridX = (int) (pos.getX() / gridSize);
            int gridY = (int) (pos.getY() / gridSize);
            
            // 破坏方块
            gameMap.destroyBlock(gridX, gridY);
        }
    }

    /**
     * 计算爆炸范围
     */
    private List<Point2D> calculateExplosionRange(Bomb bomb) {
        List<Point2D> affectedPositions = new ArrayList<>();
        int radius = bomb.getExplosionRadius();
        int centerX = bomb.getX();
        int centerY = bomb.getY();

        // 中心点
        affectedPositions.add(new Point2D(centerX * gridSize, centerY * gridSize));

        // 四个方向，检查每个方向是否被阻挡
        int[] directions = {-1, 1}; // -1: 上/左, 1: 下/右
        
        // 垂直方向（上下）
        for (int dir : directions) {
            for (int i = 1; i <= radius; i++) {
                int checkY = centerY + (dir * i);
                if (gameMap != null && !gameMap.isWalkable(centerX, checkY)) {
                    // 遇到不可通行的方块，停止这个方向的爆炸
                    break;
                }
                affectedPositions.add(new Point2D(centerX * gridSize, checkY * gridSize));
            }
        }
        
        // 水平方向（左右）
        for (int dir : directions) {
            for (int i = 1; i <= radius; i++) {
                int checkX = centerX + (dir * i);
                if (gameMap != null && !gameMap.isWalkable(checkX, centerY)) {
                    // 遇到不可通行的方块，停止这个方向的爆炸
                    break;
                }
                affectedPositions.add(new Point2D(checkX * gridSize, centerY * gridSize));
            }
        }

        return affectedPositions;
    }

    /**
     * 检查玩家碰撞
     */
    public boolean checkPlayerCollision(PlayerComponent player1, PlayerComponent player2) {
        Point2D pos1 = player1.getPlayer().getPosition();
        Point2D pos2 = player2.getPlayer().getPosition();
        
        // 简单的圆形碰撞检测
        double distance = pos1.distance(pos2);
        return distance < 30; // 玩家半径约15像素
    }

    /**
     * 检查玩家与炸弹碰撞
     */
    public boolean checkBombCollision(PlayerComponent playerComponent, Bomb bomb) {
        Point2D playerPos = playerComponent.getPlayer().getPosition();
        Point2D bombPos = new Point2D(bomb.getX() * gridSize, bomb.getY() * gridSize);
        
        double distance = playerPos.distance(bombPos);
        return distance < 25; // 炸弹半径约12.5像素
    }

    /**
     * 更新游戏状态
     */
    public void update(long nowMs) {
        // 更新所有玩家
        for (PlayerComponent playerComponent : players) {
            playerComponent.tick(nowMs);
            
            // 检查炸弹爆炸
            List<Bomb> explodedBombs = playerComponent.getBombs().tick(nowMs);
            for (Bomb bomb : explodedBombs) {
                handleExplosion(bomb);
            }
        }

        // 清理过期的爆炸效果
        activeExplosions.removeIf(explosion -> 
            nowMs - explosion.getExplosionTime() > 500); // 爆炸效果持续500ms
    }

    /**
     * 获取所有玩家
     */
    public List<PlayerComponent> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * 获取活跃爆炸
     */
    public List<ExplosionEvent> getActiveExplosions() {
        return new ArrayList<>(activeExplosions);
    }
}
