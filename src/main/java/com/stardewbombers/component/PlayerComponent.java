package com.stardewbombers.component;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.ExplosionEvent;
import java.util.List;

public class PlayerComponent {
	private final Player player;
	private final MovementComponent movement;
	private final BombComponent bombs;

	public PlayerComponent(Player player, MovementComponent movement, BombComponent bombs) {
		this.player = player;
		this.movement = movement;
		this.bombs = bombs;
	}

	public Player getPlayer() { return player; }
	public MovementComponent getMovement() { return movement; }
	public BombComponent getBombs() { return bombs; }

	public void moveUp() { if (player.isAlive()) movement.moveUp(); syncPosition(); }
	public void moveDown() { if (player.isAlive()) movement.moveDown(); syncPosition(); }
	public void moveLeft() { if (player.isAlive()) movement.moveLeft(); syncPosition(); }
	public void moveRight() { if (player.isAlive()) movement.moveRight(); syncPosition(); }

	public boolean placeBomb(long nowMs) {
		if (!player.isAlive()) return false;
		return bombs.placeBomb(player.getPosition(), nowMs);
	}

	/**
	 * 处理爆炸伤害
	 */
	public boolean handleExplosionDamage(ExplosionEvent explosionEvent) {
		if (!player.isAlive()) return false;
		
		// 检查玩家是否在爆炸范围内
		Point2D playerPos = player.getPosition();
		boolean inRange = bombs.isInExplosionRange(playerPos, explosionEvent.getBomb());
		
		if (inRange) {
			// 避免自己炸自己
			if (!explosionEvent.getOwnerId().equals(player.getId())) {
				return player.takeDamage(1, explosionEvent.getExplosionTime());
			}
		}
		return false;
	}

	/**
	 * 检查玩家是否在爆炸范围内
	 */
	public boolean isInExplosionRange(ExplosionEvent explosionEvent) {
		Point2D playerPos = player.getPosition();
		return bombs.isInExplosionRange(playerPos, explosionEvent.getBomb());
	}

	public void tick(long nowMs) {
		player.tick(nowMs);
		bombs.tick(nowMs);
	}

	private void syncPosition() {
		Point2D p = movement.getPosition();
		player.setPosition(p);
	}
}
