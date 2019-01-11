package me.rhin.kingdomraiders.server.gameobjects.entity.projectile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class Projectile extends Entity {

	public boolean remove;

	private Entity owner;
	private double targetX, targetY, angle;

	// Hacked-together boolean should probally remove
	private boolean delayedCollidedFromMonster = false;

	public Projectile(Entity entity, JSONObject projectileJSON, double x, double y, double targetX, double targetY) {
		super(entity.currentMap);
		this.stats.duration = projectileJSON.getInt("duration");
		this.stats.speed = projectileJSON.getInt("speed");
		this.stats.damage = projectileJSON.getInt("damage");

		// We set our bounds in normal 32x32 for the angle calculation.
		this.setBounds(x, y, 32, 32);
		this.setCollision(8, 5, 16, 22);
		this.owner = entity;

		this.targetX = targetX;
		this.targetY = targetY;
		double deltaX = this.targetX - this.x;
		double deltaY = this.targetY - this.y;

		this.angle = Math.atan2(deltaY, deltaX);

		// System.out.println("Starting: " + this.x + "," + this.y);
		// System.out.println("Target: " + Math.round(this.targetX) + "," +
		// Math.round(this.targetY));
		// System.out.println(System.currentTimeMillis());
	}

	private void moveToTarget() {
		// Delayed remove boolean from our thread below
		if (this.delayedCollidedFromMonster && this.getCollidedPlayer() != null) {
			this.getCollidedPlayer().damage(this.stats.damage);
			this.kill();
			return;
		}

		// If this was shot by a player & hits a monster...
		if (this.owner instanceof Player)
			if (this.getCollidedMonster() != null) {
				Monster m = this.getCollidedMonster();

				m.damage(this.owner.stats.damage + this.stats.damage);
				this.kill();
				return;
			}

		// If this was shot by a monster & hits a player...
		if (this.owner instanceof Monster)
			if (this.getCollidedPlayer() != null) {

				Projectile thisClass = this;
				if (this.getCollidedPlayer().getPing() >= 67)
					new Thread(() -> {
						try {
							Thread.sleep(thisClass.getCollidedPlayer().getPing() - 66); // 66 is the update thread
							if (thisClass.getCollidedPlayer() != null)
								thisClass.delayedCollidedFromMonster = true;
							Thread.currentThread().stop(); // Depreciated
						} catch (Exception e) {
							System.err.println(e);
						}
					}).start();
				else {// Normally kill the projectile
					this.getCollidedPlayer().damage(this.stats.damage);
					this.kill();

					return;
				}
			}

		double velX = this.stats.speed * Math.cos(this.angle);
		double velY = this.stats.speed * Math.sin(this.angle);

		// Move to target as long as we don't run into any tiles & have a duration.
		if (--this.stats.duration > 0 && !this.isTileCollided(velX, velY)) {
			this.setPosition(this.x + velX, this.y + velY);

		} else {
			this.kill();
		}

	}

	private void kill() {
		this.remove = true;
	}

	public void update() {
		this.moveToTarget();

		// System.out.println(Math.round(this.collider.x) + "," +
		// Math.round(this.collider.y));
	}

	public static void fire(Entity entity, JSONObject projectileJSON, double x, double y, double targetX,
			double targetY) {
		Main.getServer().getManager().getProjectileManager().projectiles
				.add(new Projectile(entity, projectileJSON, x, y, targetX, targetY));
	}
}
