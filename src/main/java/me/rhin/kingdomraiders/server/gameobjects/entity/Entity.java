package me.rhin.kingdomraiders.server.gameobjects.entity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;
import me.rhin.kingdomraiders.server.manager.map.Map;

public class Entity {

	public double x, y;
	public int w, h;

	public Map currentMap;
	private EntityShoot entityShoot;
	public EntityStats stats;

	public Collider collider;

	public Entity(Map map) {
		this.currentMap = map;
		this.entityShoot = new EntityShoot();
		this.stats = new EntityStats();
	}

	public void setMap(Map map) {
		this.currentMap = map;
	}

	public void setBounds(double x, double y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setCollision(double x, double y, int w, int h) {
		this.collider = new Collider(x, y, w, h);
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;

		if (this.collider != null)
			this.collider.setPosition(x, y);

		// If were moving, we know 100% that our target updated.
		this.entityShoot.targetUpdated = true;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getWidth() {
		return this.w;
	}

	public double getHeight() {
		return this.h;
	}

	public boolean isTileCollided(double velX, double velY) {
		this.collider.collided = false;
		for (int cX = -this.w * 2; cX < this.w * 2; cX += 32)
			for (int cY = -this.h * 2; cY < this.h * 2; cY += 32) {
				TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(this.currentMap,
						(this.x + cX), (this.y + cY));
				if (tile == null) // Out of bounds..
					continue;

				double tileX = ((((int) Math.round(x + cX)) / 32) * 32) + tile.tileCollider.x;
				double tileY = ((((int) Math.round(y + cY)) / 32) * 32) + tile.tileCollider.y;

				if (this.collider.x + velX < (tileX + tile.tileCollider.w)
						&& (this.collider.x + this.collider.w + velX) > tileX)
					if (this.collider.y + velY < (tileY + tile.tileCollider.h)
							&& (this.collider.y + this.collider.h + velY) > tileY) {
						if (this instanceof Projectile) {
							if (tile.projectileCollision) {
								this.collider.collided = true;
								return true;
							}

						} else if (tile.entityCollision) {
							this.collider.collided = true;
							return true;
						}

					}
			}
		return false;
	}

	// Specificly checks for entity collision
	// TODO: Find a way to make this more efficient.
	public Monster getCollidedMonster() {

		for (Monster m : Main.getServer().getManager().getMonsterManager().getMonsters(this.currentMap)) {
			if (this.collider.x < (m.x + m.w) && (this.collider.x + this.collider.w) > m.x)
				if (this.collider.y < (m.y + m.h) && (this.collider.y + this.collider.h) > m.y)
					return m;
		}
		return null;
	}

	public Player getCollidedPlayer() {
		for (Player p : Main.getServer().getManager().getPlayerManager().getPlayers(this.currentMap)) {
			if (this.collider.x < (p.x + p.w) && (this.collider.x + this.collider.w) > p.x)
				if (this.collider.y < (p.y + p.h) && (this.collider.y + this.collider.h) > p.y)
					return p;
		}
		return null;
	}

	public EntityShoot entityShoot() {
		return this.entityShoot;
	}

	public void update() {
		this.entityShoot.update();
	}

	public void fastUpdate() {
		this.entityShoot.fastUpdate();
	}

	public void slowUpdate() {

	}

	// Collider class
	public class Collider {
		public boolean collided;
		private double xOffset, yOffset;
		public double x, y, w, h;

		public Collider(double x, double y, double w, double h) {
			this.xOffset = x;
			this.yOffset = y;

			this.x = Entity.this.x + this.xOffset;
			this.y = Entity.this.y + this.yOffset;
			this.w = w;
			this.h = h;
		}

		public void setPosition(double x, double y) {
			this.x = x + this.xOffset;
			this.y = y + this.yOffset;
		}
	}

}
