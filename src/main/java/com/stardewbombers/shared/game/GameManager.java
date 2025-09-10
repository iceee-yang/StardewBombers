package com.stardewbombers.shared.game;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.component.PlayerComponent;
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

    /**
     * 添加玩家
     */
    public void addPlayer(PlayerComponent playerComponent) {
        players.add(playerComponent);
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

        // 处理对障碍物的影响（待实现）
        // handleObstacleDamage(explosionEvent);
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

        // 四个方向
        for (int i = 1; i <= radius; i++) {
            affectedPositions.add(new Point2D(centerX * gridSize, (centerY - i) * gridSize)); // 上
            affectedPositions.add(new Point2D(centerX * gridSize, (centerY + i) * gridSize)); // 下
            affectedPositions.add(new Point2D((centerX - i) * gridSize, centerY * gridSize)); // 左
            affectedPositions.add(new Point2D((centerX + i) * gridSize, centerY * gridSize)); // 右
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
