package com.stardewbombers.component;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Player;

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

	public void tick(long nowMs) {
		player.tick(nowMs);
		bombs.tick(nowMs);
	}

	private void syncPosition() {
		Point2D p = movement.getPosition();
		player.setPosition(p);
	}
}
