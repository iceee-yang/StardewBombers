package com.stardewbombers.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;

/**
 * 炸弹组件类 - 管理玩家的炸弹系统
 * 释放模式：同一时间只能存在 1 个炸弹；必须等爆炸/结束后才能放新的
 * 包含：网格吸附、爆炸范围计算
 */
public class BombComponent {
	private final String ownerId;
	private int bombPower;
	private final List<Bomb> activeBombs = new ArrayList<>();
	private final int gridSize = 50; // 网格大小

	public BombComponent(String ownerId, int bombCount, int bombPower) {
		this.ownerId = ownerId;
		this.bombPower = bombPower;
	}

	// 基础属性访问
	public int getBombPower() { return bombPower; }
	public void setBombPower(int power) { this.bombPower = power; }
	public List<Bomb> getActiveBombs() { return activeBombs; }

	/**
	 * 放置炸弹（自动网格吸附）
	 */
	public boolean placeBomb(Point2D worldPosition, long nowMs) {
		// 仅允许同一时间存在 1 个炸弹
		if (!activeBombs.isEmpty()) return false;
		
		// 网格吸附
		int gridX = (int) Math.round(worldPosition.getX() / gridSize);
		int gridY = (int) Math.round(worldPosition.getY() / gridSize);
		
		Bomb bomb = new Bomb(gridX, gridY, ownerId);
		bomb.setExplosionRadius(bombPower);
		bomb.startTicking();
		activeBombs.add(bomb);
		return true;
	}

	/**
	 * 更新所有炸弹状态
	 */
	public List<Bomb> tick(long nowMs) {
		List<Bomb> exploded = new ArrayList<>();
		Iterator<Bomb> it = activeBombs.iterator();
		
		while (it.hasNext()) {
			Bomb bomb = it.next();
			
			// 更新倒计时
			if (bomb.getState() == Bomb.BombState.TICKING) {
				double deltaTime = 0.016; // 固定步长，后续可由调用方传入
				double newFuseTime = bomb.getFuseTime() - deltaTime;
				bomb.setFuseTime(newFuseTime);
				
				if (newFuseTime <= 0) {
					bomb.explode();
				}
			}
			
			// 检查爆炸状态
			if (bomb.getState() == Bomb.BombState.EXPLODING) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - bomb.getExplosionStartTime() >= bomb.getExplosionDuration() * 1000) {
					bomb.setState(Bomb.BombState.EXPLODED);
					bomb.setHasExploded(true);
					exploded.add(bomb);
					it.remove();
				}
			}
		}
		
		return exploded;
	}

	/**
	 * 获取爆炸影响范围（十字形）
	 */
	public List<Point2D> getExplosionRange(Bomb bomb) {
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
	 * 检查位置是否在爆炸范围内
	 */
	public boolean isInExplosionRange(Point2D targetPosition, Bomb bomb) {
		List<Point2D> explosionRange = getExplosionRange(bomb);
		int targetGridX = (int) Math.round(targetPosition.getX() / gridSize);
		int targetGridY = (int) Math.round(targetPosition.getY() / gridSize);
		
		for (Point2D pos : explosionRange) {
			int gridX = (int) Math.round(pos.getX() / gridSize);
			int gridY = (int) Math.round(pos.getY() / gridSize);
			if (gridX == targetGridX && gridY == targetGridY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 强制爆炸所有炸弹
	 */
	public void forceExplodeAll() {
		for (Bomb bomb : activeBombs) {
			if (bomb.getState() == Bomb.BombState.TICKING) {
				bomb.explode();
			}
		}
	}

	/**
	 * 清除所有炸弹
	 */
	public void clearAllBombs() {
		activeBombs.clear();
	}
}

